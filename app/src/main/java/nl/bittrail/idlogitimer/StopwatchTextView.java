package nl.bittrail.idlogitimer;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by blaatenator on 3/22/18.
 */

public class StopwatchTextView extends android.support.v7.widget.AppCompatTextView {
    long startTime = System.nanoTime();

    long elapsed = 0;
    TextView view;
    Handler handler = new Handler();
    boolean running = false;
    boolean doReset = false;

    public StopwatchTextView(Context context) {
        super(context);
    }

    public StopwatchTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StopwatchTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setView(TextView view) {
        this.view = view;
    }

    public TextView getView() {
        return this.view;
    }

    public void Start() {
        running = true;
        if (doReset) {
            startTime = System.nanoTime();
            elapsed = 0;
            doReset = false;
        }
        handler.postDelayed(updateTimerThread, 10);
    }

    public void Stop() {
        running = false;
    }

    public void Reset() {
        running = false;
        doReset = true;
        elapsed = 0;
    }

    public long getElapsedInMillis() {
        return elapsed;
    }

    public String getElapsedFormatted() {
        DateFormat format = new SimpleDateFormat("mm:ss.SS");
        return format.format(elapsed);
    }

    private void updateTime(long elapsed) {
        DateFormat format = new SimpleDateFormat("mm:ss.SS");
        String displayTime = format.format(elapsed);
        if (view != null) {
//            ((TextView)view.findViewById(R.id.lblTestStopwatch)).setText("blaaa");
//            view.setText(displayTime);
//            view.invalidate();
//            view.postInvalidate();
            //((View)view.getParent()).invalidate();
        }
    }

    private Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            elapsed = (System.nanoTime() - startTime) / 1000;
            updateTime(elapsed);
            if (running) {
                handler.postDelayed(this, 10);
            }
        }
    };
}
