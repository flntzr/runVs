import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.zip.ZipFile;

public class ArcAsciiParser {

    public static final int FILE_GRANULARITY = 5; // degrees
    public static final int HEADER_LINES = 6;
    public static final String SRTM_FOLDER = "/home/droland/Desktop/Ghostrunner/local_srtm/";
    public static final String SRTM_SERVER_DUMMY = "/home/droland/Desktop/Ghostrunner/remote_srtm/"; //TODO only used as dummy, use srtm fileserver instead
    public static final double DATA_RESOLUTION = 0.00083333333333333; // degrees
    public static final int CELLPART_COUNT = 6000;
    public static final int ARRAY_LENGTH = 60; // meters, must be smaller or equal CELLPART_COUNT
    public static final short NO_DATA_VALUE = -9999;

    private double startLat;
    private double startLon;
    private short[][] elevationArray = null; //[lat][lon] / [row][col] pivot 0-0 : lower left

    public static void main(String args[]) {
        double lat = 4.05; // North
        double lon = 9.05; // East
        ArcAsciiParser parser = new ArcAsciiParser();
        try {
            short elevation = parser.getElevation(lat, lon);
            System.out.println(elevation);
        } catch (NoElevationException e) {
            e.printStackTrace();
        }
    }

    private void initElevationArray(double lat, double lon) {
        elevationArray = new short[ARRAY_LENGTH][ARRAY_LENGTH];
        startLat = lat - calcCellPartRest(lat) - ARRAY_LENGTH / 2 * DATA_RESOLUTION;
        startLon = lon - calcCellPartRest(lon) - ARRAY_LENGTH / 2 * DATA_RESOLUTION;
        int startRow = HEADER_LINES + CELLPART_COUNT - calcCellPart(lat + ARRAY_LENGTH / 2 * DATA_RESOLUTION);
        int startCol = calcCellPart(startLon);

        // load necessary files
        boolean useNextLat = calcFilePart(startLat) != calcFilePart(startLat + ARRAY_LENGTH * DATA_RESOLUTION);
        boolean useNextLon = calcFilePart(startLon) != calcFilePart(startLon + ARRAY_LENGTH * DATA_RESOLUTION);
        BufferedReader srtm1 = openSrtmFile(calcSrtmFileName(startLat, startLon));
        BufferedReader srtm2 = null;
        BufferedReader srtm3 = null;
        BufferedReader srtm4 = null;
        if (useNextLon) {
            srtm2 = openSrtmFile(calcSrtmFileName(startLat, startLon + FILE_GRANULARITY));
        }
        if (useNextLat) {
            srtm3 = srtm1;
            srtm1 = openSrtmFile(calcSrtmFileName(startLat + FILE_GRANULARITY, startLon));
        }
        if (useNextLat && useNextLon) {
            srtm4 = srtm2;
            srtm2 = openSrtmFile(calcSrtmFileName(startLat + FILE_GRANULARITY, startLon + FILE_GRANULARITY));
        }
        fillElevationArray(srtm1, srtm2, srtm3, srtm4, startRow, startCol);
    }

    private void fillElevationArray(BufferedReader srtm1, BufferedReader srtm2, BufferedReader srtm3, BufferedReader srtm4, int startRow, int startCol) {
        try {
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

            for (int row = 1; row <= ARRAY_LENGTH; row++) {
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
                for (int col = 0; col < ARRAY_LENGTH; col++) {
                    if (startCol + col < CELLPART_COUNT) { // must be < instead of <=, because file has too many entries, last one is always skipped TODO change for custom files?
                        elevationArray[ARRAY_LENGTH - row][col] = line1 == null ? NO_DATA_VALUE : Short.parseShort(line1[startCol + col]);
                    } else {
                        elevationArray[ARRAY_LENGTH - row][col] = line2 == null ? NO_DATA_VALUE : Short.parseShort(line2[startCol + col - CELLPART_COUNT]);
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
        if (lat < startLat || lat > startLat + ARRAY_LENGTH * DATA_RESOLUTION) {
            return false;
        } else if (lon < startLon || lon > startLon + ARRAY_LENGTH * DATA_RESOLUTION) {
            return false;
        } else {
            return true;
        }
    }

    public short getElevation(double lat, double lon) throws NoElevationException {
        if (elevationArray == null || !coordsInArrayRange(lat, lon)) {
            initElevationArray(lat, lon);
        }
        return getInterpolatedElevation(lat, lon, 50);
    }

    public short getInterpolatedElevation(double lat, double lon, int accuracy) throws NoElevationException {
        int latIndex = calcLatIndex(lat);
        int lonIndex = calcLonIndex(lon);
        short height;
        double distance;
        double weightedHeights = 0;
        double weightings = 0;
        boolean heightFound = false;
        for (int row = latIndex - accuracy; row <= latIndex + 1 + accuracy && row < ARRAY_LENGTH; row++) {
            for (int col = lonIndex - accuracy; col <= lonIndex + 1 && col < ARRAY_LENGTH + accuracy; col++) {
                height = elevationArray[row][col];
                if (height != NO_DATA_VALUE) {
                    heightFound = true;
                    distance = distance(calcLatDistance(lat, row), calcLonDistance(lon, row));
                    weightedHeights += 1 / distance * height;
                    weightings += 1 / distance;
                }
            }
        }
        if (heightFound == false) {
            throw new NoElevationException("No Elevation was found for these coordinates!");
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
        int cellPart = (int) (calcFilePartRest(coordPart) / DATA_RESOLUTION);
        return cellPart;
    }

    private double calcCellPartRest(double coordPart) {
        double cellPartRest = calcFilePartRest(coordPart) - calcCellPart(coordPart) * DATA_RESOLUTION;
        return cellPartRest;
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

    private File loadSrtmFile(String srtmFileName) {
        File srtmFile = new File(SRTM_FOLDER + srtmFileName);
        if (!srtmFile.exists()) {
            try {
                Files.copy(new File(SRTM_SERVER_DUMMY + srtmFileName).toPath(), new File(SRTM_FOLDER + srtmFileName).toPath());
            } catch (IOException e) {
                srtmFile = null;
            }
        }
        return srtmFile;
        //TODO remove dummy and implement loading a specific srtmFile from the server
    }

    private BufferedReader openSrtmFile(String srtmFileName) {
        try {
            ZipFile srtmFile = new ZipFile(loadSrtmFile(srtmFileName));
            return new BufferedReader(new InputStreamReader(srtmFile.getInputStream(srtmFile.entries().nextElement())));
        } catch (Exception e) {
            return null;
        }
        //TODO adapt to new loadsrtmfile?
    }

    private String calcSrtmFileName(double lat, double lon) {
        return FILE_GRANULARITY + "x" + FILE_GRANULARITY + "_lat" + calcFilePart(lat) + "_lon" + calcFilePart(lon);
    }

}