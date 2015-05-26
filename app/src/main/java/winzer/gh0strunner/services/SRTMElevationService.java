package winzer.gh0strunner.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.loopj.android.http.Base64;
import org.apache.http.Header;

import java.io.*;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class SRTMElevationService extends Service {

    Context context;
    private final IBinder binder = new ElevationBinder();
    private ElevationListener elevationListener;

    public class ElevationBinder extends Binder {
        public SRTMElevationService getService() {
            return SRTMElevationService.this;
        }


        public void setListener(ElevationListener lListener) {
            elevationListener = lListener;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        context = this;
        final Intent finalIntent = intent;
        new Thread(new Runnable() {
            public void run() {
                arrayLength = finalIntent.getIntExtra("distance", 10) * 11;
                initElevationService(finalIntent.getDoubleExtra("lat", 0.0), finalIntent.getDoubleExtra("lon", 0.0));
            }
        }).start();
        return binder;
    }

    public static final int FILE_GRANULARITY = 1; // degrees
    public static final int HEADER_LINES = 6;
    public static final double DATA_RESOLUTION = 0.00083333333333333; // degrees
    public static final int CELLPART_COUNT = 1200;
    public static int arrayLength = 110; // must be smaller or equal CELLPART_COUNT
    public static final short NO_DATA_VALUE = -9999;

    private boolean initiating = false;
    private double startLat;
    private double startLon;
    private short[][] elevationArray = null; //[lat][lon] / [row][col] pivot 0-0 : lower left

    public void initElevationService(double lat, double lon) {
        Log.v("elevationservice init", "lat " + lat + " lon " + lon);
        elevationArray = new short[arrayLength][arrayLength];
        startLat = lat - calcCellPartRest(lat) - arrayLength / 2 * DATA_RESOLUTION;
        startLon = lon - calcCellPartRest(lon) - arrayLength / 2 * DATA_RESOLUTION;
        int startRow = HEADER_LINES + CELLPART_COUNT - calcCellPart(lat + arrayLength / 2 * DATA_RESOLUTION);
        int startCol = calcCellPart(startLon);

        // load necessary files
        boolean useNextLat = calcFilePart(startLat) != calcFilePart(startLat + arrayLength * DATA_RESOLUTION);
        boolean useNextLon = calcFilePart(startLon) != calcFilePart(startLon + arrayLength * DATA_RESOLUTION);
        BufferedReader srtm1 = openSrtmFile(startLat, startLon);
        BufferedReader srtm2 = null;
        BufferedReader srtm3 = null;
        BufferedReader srtm4 = null;
        if (useNextLon) {
            srtm2 = openSrtmFile(startLat, startLon + FILE_GRANULARITY);
        }
        if (useNextLat) {
            srtm3 = srtm1;
            srtm1 = openSrtmFile(startLat + FILE_GRANULARITY, startLon);
        }
        if (useNextLat && useNextLon) {
            srtm4 = srtm2;
            srtm2 = openSrtmFile(startLat + FILE_GRANULARITY, startLon + FILE_GRANULARITY);
        }
        fillElevationArray(srtm1, srtm2, srtm3, srtm4, startRow, startCol);
        elevationListener.initiated();
    }

    private void fillElevationArray(BufferedReader srtm1, BufferedReader srtm2, BufferedReader srtm3, BufferedReader srtm4, int startRow, int startCol) {
        try {
            Log.v("init", "filling elevation array");
            boolean filesSwitched = false;
            String[] line1;
            String[] line2;
            for (int i = 0; i < startRow; i++) {
                if (srtm1 != null) {
                    srtm1.readLine();
                }
                if (srtm2 != null) {
                    srtm2.readLine();
                }
            }

            for (int row = 1; row <= arrayLength; row++) {
                if (startRow + row > CELLPART_COUNT + HEADER_LINES && !filesSwitched) { // must be >= instead of >, because file has too many entries, last one is always skipped TODO change for custom files?
                    if (srtm1 != null) {
                        srtm1.close();
                    }
                    if (srtm2 != null) {
                        srtm2.close();
                    }
                    srtm1 = srtm3;
                    srtm2 = srtm4;
                    for (int i = 0; i < HEADER_LINES; i++) {
                        if (srtm1 != null) {
                            srtm1.readLine();
                        }
                        if (srtm2 != null) {
                            srtm2.readLine();
                        }
                    }
                    filesSwitched = true;
                }
                line1 = srtm1 == null ? null : srtm1.readLine().split(" ");
                line2 = srtm2 == null ? null : srtm2.readLine().split(" ");
                for (int col = 0; col < arrayLength; col++) {
                    if (startCol + col < CELLPART_COUNT) { // must be < instead of <=, because file has too many entries, last one is always skipped TODO change for custom files?
                        elevationArray[arrayLength - row][col] = line1 == null ? NO_DATA_VALUE : Short.parseShort(line1[startCol + col]);
                    } else {
                        elevationArray[arrayLength - row][col] = line2 == null ? NO_DATA_VALUE : Short.parseShort(line2[startCol + col - CELLPART_COUNT]);
                    }
                }
            }
            if (srtm1 != null) {
                srtm1.close();
            }
            if (srtm2 != null) {
                srtm2.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean coordsInArrayRange(double lat, double lon) {
        if (lat < startLat || lat > startLat + arrayLength * DATA_RESOLUTION) {
            return false;
        } else if (lon < startLon || lon > startLon + arrayLength * DATA_RESOLUTION) {
            return false;
        } else {
            return true;
        }
    }

    public short getElevation(double lat, double lon) {
        if (!initiating) {
            if (elevationArray == null || !coordsInArrayRange(lat, lon)) {
                initiating = true;
                initElevationService(lat, lon);
                initiating = false;
            }
            return getInterpolatedElevation(lat, lon, 50);
        } else {
            return -9999;
        }
    }

    public short getInterpolatedElevation(double lat, double lon, int accuracy) {
        int latIndex = calcLatIndex(lat);
        int lonIndex = calcLonIndex(lon);
        short height;
        double distance;
        double weightedHeights = 0;
        double weightings = 0;
        boolean heightFound = false;
        for (int row = latIndex - accuracy; row <= latIndex + 1 + accuracy && row < arrayLength; row++) {
            for (int col = lonIndex - accuracy; col <= lonIndex + 1 && col < arrayLength + accuracy; col++) {
                height = elevationArray[row][col];
                if (height != NO_DATA_VALUE) {
                    heightFound = true;
                    distance = distance(calcLatDistance(lat, row), calcLonDistance(lon, row));
                    weightedHeights += 1 / distance * height;
                    weightings += 1 / distance;
                }
            }
        }
        if (!heightFound) {
            return -9999;
        }
        return (short) (weightedHeights / weightings);
    }

    private double distance(double latDistance, double lonDistance) {
        return (Math.sqrt(latDistance * latDistance + lonDistance * lonDistance));
    }

    private double calcLatDistance(double lat, int latIndex) {
        return Math.abs(calcLatIndexCoords(latIndex) - lat);
    }

    private double calcLonDistance(double lon, int lonIndex) {
        return Math.abs(calcLonIndexCoords(lonIndex) - lon);
    }

    private double calcLatIndexCoords(int latIndex) {
        return startLat + latIndex * DATA_RESOLUTION;
    }

    private double calcLonIndexCoords(int lonIndex) {
        return startLon + lonIndex * DATA_RESOLUTION;
    }

    private int calcLatIndex(double lat) {
        return (int) ((lat - startLat) / DATA_RESOLUTION);
    }

    private int calcLonIndex(double lon) {
        return (int) ((lon - startLon) / DATA_RESOLUTION);
    }

    private int calcCellPart(double coordPart) {
        return (int) (calcFilePartRest(coordPart) / DATA_RESOLUTION);
    }

    private double calcCellPartRest(double coordPart) {
        return calcFilePartRest(coordPart) - calcCellPart(coordPart) * DATA_RESOLUTION;
    }

    private double calcFilePartRest(double coordPart) {
        double filePartRest;
        if (coordPart < 0) {
            filePartRest = FILE_GRANULARITY + coordPart % FILE_GRANULARITY;
        } else {
            filePartRest = coordPart % FILE_GRANULARITY;
        }
        return filePartRest;
    }

    private int calcFilePart(double coordPart) {
        int filePart;
        if (coordPart < 0) {
            filePart = (int) (coordPart - coordPart % FILE_GRANULARITY - FILE_GRANULARITY);
        } else {
            filePart = (int) (coordPart - coordPart % FILE_GRANULARITY);
        }
        return filePart;
    }

    private File loadSrtmFile(final double startLat, final double startLon) {
        final File srtmFile = new File(getFilesDir().getAbsolutePath() + "/" + calcSrtmFileName(startLat, startLon));
        if (!srtmFile.exists()) {
            String url = "/tile/" + calcFilePart(startLat) + "/" + calcFilePart(startLon);
            RestClient.get(url, new RetryAsyncHttpResponseHandler(url, context, RetryAsyncHttpResponseHandler.GET_REQUEST) {

                @Override
                public void onSuccessful(int statusCode, Header[] headers, byte[] binaryData) {
                    binaryData = Base64.decode(binaryData, Base64.DEFAULT);
                    try {
                        FileOutputStream fos = new FileOutputStream(srtmFile);
                        fos.write(binaryData);
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onUnsuccessful(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
                    error.printStackTrace();
                    Log.v("onfailure", "Status http" + statusCode);
                }

            });
        }
        return srtmFile;
    }

    private BufferedReader openSrtmFile(double startLat, double startLon) {
        try {
            ZipFile zipFile = new ZipFile(loadSrtmFile(startLat, startLon));
            return new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipFile.entries().nextElement())));
        } catch (ZipException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String calcSrtmFileName(double lat, double lon) {
        return FILE_GRANULARITY + "x" + FILE_GRANULARITY + "_lat" + calcFilePart(lat) + "_lon" + calcFilePart(lon) + ".zip";
    }

}