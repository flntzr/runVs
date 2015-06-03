package winzer.gh0strunner.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import org.joda.time.Duration;
import winzer.gh0strunner.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by droland on 6/2/15.
 */
public class RunUI extends View {

    int distance;
    int distancePassed;
    double advancement;
    long duration;
    double[] ghostAdvancements;
    int position;

    public void updateUI(int distance, double distancePassed, double advancement, long duration, double[] ghostAdvancements, int position) {
        this.distance = distance;
        this.distancePassed = (int) distancePassed;
        this.advancement = advancement;
        this.duration = duration;
        this.ghostAdvancements = ghostAdvancements;
        this.position = position;
        invalidate();
        requestLayout();
    }

    public RunUI(Context context) {
        super(context);
        init();
    }

    public RunUI(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RunUI(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    //for text aligning
    private Rect textBounds;

    private Paint passedDistancePaint;
    private Paint trackPaint;
    private Paint finishLinePaint;
    private Paint userPaint;
    private Paint ghostPaint;
    private Paint posPaint;
    private Paint advancementPaint;
    private Paint textPaint;

    private RectF trackBounds;
    private RectF finishLine;

    DateFormat format;

    private float trackPadding;
    private float trackRadius;
    private float ghostRadius;
    private float userRadius;
    private float trackWidth;
    private float finishLineLength;
    private float finishLineWidth;
    private int trackColor;
    private int userColor;
    private int ghostColor;
    private int textColor;
    private float posOffset;
    private float posSize;
    private float advancementOffset;
    private float advancementSize;
    private float textSize;

    private void init() {
        format = new SimpleDateFormat("mm:ss");
        textBounds = new Rect();

        trackBounds = new RectF();
        finishLine = new RectF();

        trackColor = getResources().getColor(R.color.run_ui_track);
        userColor = getResources().getColor(R.color.highlight1);
        ghostColor = getResources().getColor(R.color.run_ui_track);
        textColor = getResources().getColor(R.color.run_ui_track);

        userPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        userPaint.setStyle(Paint.Style.FILL);
        userPaint.setColor(userColor);

        ghostPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ghostPaint.setStyle(Paint.Style.FILL);
        ghostPaint.setColor(ghostColor);

        trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackPaint.setColor(trackColor);
        trackPaint.setStyle(Paint.Style.STROKE);

        finishLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        finishLinePaint.setColor(trackColor);
        finishLinePaint.setStyle(Paint.Style.FILL);

        passedDistancePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        passedDistancePaint.setColor(userColor);
        passedDistancePaint.setStyle(Paint.Style.STROKE);

        posPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        posPaint.setTextAlign(Paint.Align.CENTER);
        posPaint.setColor(textColor);

        advancementPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        advancementPaint.setTextAlign(Paint.Align.CENTER);
        advancementPaint.setColor(textColor);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setColor(textColor);
    }

    private float getXPosition(double advancement) {
        return (float) (getWidth()/2 + Math.sin(2*advancement*Math.PI) * trackRadius);
    }

    private float getYPosition(double advancement) {
        return (float) (getWidth()/2 - Math.cos(2*advancement*Math.PI) * trackRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw track
        int trackAdvancement = (int) (advancement * 360);
        canvas.drawArc(trackBounds, -90 + trackAdvancement, 360 - trackAdvancement, false, trackPaint);
        canvas.drawArc(trackBounds, -90, trackAdvancement, false, passedDistancePaint);
        canvas.drawRect(finishLine, trackPaint);

        //draw ghosts
        if (ghostAdvancements != null) {
            for (double ghostAdvancement : ghostAdvancements) {
                canvas.drawCircle(getXPosition(ghostAdvancement), getYPosition(ghostAdvancement), ghostRadius, ghostPaint);
            }
        }

        //draw user
        canvas.drawCircle(getXPosition(advancement), getYPosition(advancement), userRadius, userPaint);

        String advancementString = ((int) advancement * 100) + "%";
        advancementPaint.getTextBounds(advancementString, 0, advancementString.length(), textBounds);
        canvas.drawText(advancementString, getWidth()/2, getWidth()/2 - textBounds.exactCenterY() - advancementOffset, advancementPaint);

        String posString = ghostAdvancements == null || ghostAdvancements.length == 0 ? "" : position + "/" + (ghostAdvancements.length + 1);
        posPaint.getTextBounds(posString, 0, posString.length(), textBounds);
        canvas.drawText(posString, getWidth()/2, getWidth()/2 - textBounds.exactCenterY() + posOffset, posPaint);

        canvas.drawText("Time:", trackPadding, getBottom() - textSize * 3, textPaint);
        int minutes = (int) duration / 60000;
        int seconds = (int) duration / 1000 - minutes * 60;
        String minuteString = String.format("%02d", minutes);
        String secondString = String.format("%02d", seconds);
        canvas.drawText(minuteString + ":" + secondString, trackRadius, getBottom() - textSize * 3, textPaint);
        canvas.drawText("Distance:", trackPadding, getBottom() - textSize * 2, textPaint);
        canvas.drawText(distancePassed + "m", trackRadius, getBottom() - textSize * 2, textPaint);
        canvas.drawText("Distance left:", trackPadding, getBottom() - textSize, textPaint);
        canvas.drawText((distance - distancePassed) + "m", trackRadius, getBottom() - textSize, textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = (int) (width * 1.2);

        trackRadius = width * 0.4f;
        trackPadding = (width - 2 * trackRadius) / 2;
        trackWidth = trackRadius * 0.05f;
        finishLineLength = trackPadding;
        finishLineWidth = trackWidth / 4;
        ghostRadius = finishLineLength / 4;
        userRadius = finishLineLength / 3;
        advancementSize = trackRadius / 2;
        posOffset = advancementSize / 2;
        posSize = advancementSize / 2;
        advancementOffset = posSize / 2;
        textSize = posSize / 2;


        //Init FinishLine
        finishLine.set(width / 2 - finishLineWidth / 2, trackPadding - finishLineLength / 2, width / 2 + finishLineWidth / 2, trackPadding + finishLineLength / 2);

        //Init TotalTrack
        trackBounds.set(trackPadding, trackPadding, trackPadding + trackRadius*2, trackPadding + trackRadius*2);
        trackPaint.setStrokeWidth(trackWidth);

        //Init PassedDistance
        trackBounds.set(trackPadding, trackPadding, trackPadding + trackRadius*2, trackPadding + trackRadius*2);
        passedDistancePaint.setStrokeWidth(trackWidth);

        //Init PosTextSize and advancementTextSize
        posPaint.setTextSize(posSize);
        advancementPaint.setTextSize(advancementSize);

        //Init TextSize
        textPaint.setTextSize(textSize);

        setMeasuredDimension(width, height);
    }

}