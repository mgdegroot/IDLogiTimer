package nl.bittrail.idlogitimer;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.Chronometer;

/**
 * Created by marcel on 3/20/18.
 */

public class CustomChronometer extends Chronometer {


    private long timeWhenStopped = 0;

    public CustomChronometer(Context context) {
        super(context);
    }

    public CustomChronometer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomChronometer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void start() {
        setBase(SystemClock.elapsedRealtime() - timeWhenStopped);
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        timeWhenStopped = SystemClock.elapsedRealtime() - getBase();
    }

    public void reset() {
        setBase(SystemClock.elapsedRealtime());
        super.stop();
        timeWhenStopped = 0;
    }

    public long getCurrentTime() {
        timeWhenStopped = SystemClock.elapsedRealtime() - getBase();
        return timeWhenStopped;
    }

    public void setCurrentTime(long time) {
        timeWhenStopped = time;
        setBase(SystemClock.elapsedRealtime() - timeWhenStopped);
    }


}