package nl.bittrail.idlogitimer;

import android.content.Context;
import android.icu.util.Measure;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by marcel on 3/21/18.
 */

public class Subject implements Parcelable {
    private String name;
    private ArrayList<Measurement> measurements;
    private MeasurementsAdapter measurementsAdapter;
    private int id = 0;

    private int nextMeasurementID = 0;

    public Subject(int ID, String name, Context context) {
        this.id = ID;
        this.name = name;
        measurements = new ArrayList<Measurement>();
        if (context != null) {
            // TODO: test whether this is correct here -->
            measurementsAdapter = new MeasurementsAdapter(context, measurements);
        }

    }

    protected Subject(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getNextMeasurementID() {
        return nextMeasurementID++;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeTypedList(measurements);
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        name = in.readString();

        measurements = in.createTypedArrayList(Measurement.CREATOR);
        // TODO: proper restore with context ? -->
        measurementsAdapter = new MeasurementsAdapter(null, measurements);

    }

    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel in) {
            return new Subject(in);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public long getTotalElapsedMillis() { return calculateTotalElapsed(); }

    public String getTotalElapsedFormatted() {
        long total = calculateTotalElapsed();
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(total),
                TimeUnit.MILLISECONDS.toMinutes(total) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(total)),
                TimeUnit.MILLISECONDS.toSeconds(total) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(total)));
    }

    public ArrayList<Measurement>getMeasurements() {
        ArrayList<Measurement>retVal = new ArrayList<Measurement>();
        for (int i = 0; i < measurementsAdapter.getCount();i++) {
            retVal.add(measurementsAdapter.getItem(i));
        }
        return retVal;
    }

    public MeasurementsAdapter getMeasurementsAdapter() {
        return measurementsAdapter;
    }

    public long calculateTotalElapsed() {
        long totalElapsedMillis = 0;

        for (int i = 0; i < measurementsAdapter.getCount();i++) {
            totalElapsedMillis += measurementsAdapter.getItem(i).getElapsedTimeInMillis();
        }

        return totalElapsedMillis;
    }
}
