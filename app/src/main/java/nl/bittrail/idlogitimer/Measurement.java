package nl.bittrail.idlogitimer;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by marcel on 3/19/18.
 */

public class Measurement implements Parcelable {

    public interface IMeasurementListener {
        void recalculate();

        void onMeasurementDeleted(Measurement m);
        void onTotalIncremented(Measurement m);
    }

    private String name;
    private long elapsedTimeInMillis = 0;
    private Date created = new Date();
    private IMeasurementListener listener;


    private boolean isRunning = false;
    private int id = 0;

    public Measurement(int ID, String name) {
        this(ID, name, 0, null);
    }

    public Measurement(int ID, String name, IMeasurementListener listener) {
        this(ID, name, 0, listener);
    }

    public Measurement(int ID, String name, long startTimeInMillis, IMeasurementListener listener) {
        this.id = ID;
        this.name = name;
        this.elapsedTimeInMillis = startTimeInMillis;
        this.listener = listener;
    }

    protected Measurement(Parcel in) {
        readFromParcel(in);
    }


    public int getID() {
        return id;
    }

    public void setMeasurementListener(IMeasurementListener listener) {
        this.listener = listener;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeLong(this.elapsedTimeInMillis);
        dest.writeByte((byte) (isRunning?1:0));
        dest.writeSerializable(this.created);

        /*
        dest.writeLong(this.created.getTime()

         */

    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        elapsedTimeInMillis = in.readLong();
        isRunning = in.readByte() != 0;
        created = (Date)in.readSerializable();

        /*
        created = new Date(in.readLong())
         */
    }

    public static final Creator<Measurement> CREATOR = new Creator<Measurement>() {
        @Override
        public Measurement createFromParcel(Parcel in) {
            return new Measurement(in);
        }

        @Override
        public Measurement[] newArray(int size) {
            return new Measurement[size];
        }
    };

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public long getElapsedTimeInMillis() {
        return this.elapsedTimeInMillis;
    }

    public void setElapsedTimeInMilllis(long millis) {
        this.elapsedTimeInMillis = millis;
        listener.onTotalIncremented(this);
    }

    public String getElapsedTimeFormatted() {
        Log.d(this.getClass().getSimpleName(), "geElapsedTimeFormatted");
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(this.elapsedTimeInMillis),
                TimeUnit.MILLISECONDS.toMinutes(this.elapsedTimeInMillis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(this.elapsedTimeInMillis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(this.elapsedTimeInMillis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this.elapsedTimeInMillis)));
    }

    public String getCreatedFormatted() {
        SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm");

        return simpleDateFormat.format(this.created);
    }

    public boolean isRunning() { return this.isRunning; }

    public void setRunning(boolean value) { this.isRunning = value; }


}
