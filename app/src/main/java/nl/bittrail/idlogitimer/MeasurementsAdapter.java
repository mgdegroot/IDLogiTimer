package nl.bittrail.idlogitimer;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by marcel on 3/19/18.
 */

public class MeasurementsAdapter extends ArrayAdapter<Measurement> {

    public MeasurementsAdapter(Context context, ArrayList<Measurement> measurements) {
        super(context,0, measurements);
    }

    private static class ViewHolder {
        EditText txtMeasurementName;
        ImageButton btnMeasurementStartStop;
        ImageButton btnMeasurementReset;
        CustomChronometer chronometer;
        ImageButton btnMeasurementDelete;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull final ViewGroup parent) {
        final Measurement measurement = getItem(position);

        // viewholder pattern -->
        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_measurement, parent, false);

            viewHolder.txtMeasurementName = (EditText)convertView.findViewById(R.id.txtMeasurementName);
            viewHolder.btnMeasurementStartStop = (ImageButton)convertView.findViewById(R.id.btnMeasurementStartStop);
            viewHolder.btnMeasurementReset = (ImageButton)convertView.findViewById(R.id.btnMeasurementReset);
            viewHolder.chronometer = (CustomChronometer)convertView.findViewById(R.id.chronometer);
            viewHolder.btnMeasurementDelete = (ImageButton)convertView.findViewById(R.id.btnMeasurementDelete);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }


        viewHolder.txtMeasurementName.setText(measurement.getName());

        viewHolder.txtMeasurementName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    measurement.setName(v.getText().toString());
                    v.clearFocus();

                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                    return true;
                }
                return false;
            }
        });

        viewHolder.txtMeasurementName.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                measurement.setName(((EditText)v).getText().toString());
            }
        });

        viewHolder.chronometer.setCurrentTime(measurement.getElapsedTimeInMillis());

        if (measurement.isRunning()) {
            viewHolder.chronometer.start();
            viewHolder.btnMeasurementStartStop.setImageResource(R.drawable.ic_pause_circle_outline_black_32dp);
        }
        else {
            viewHolder.chronometer.stop();
            viewHolder.btnMeasurementStartStop.setImageResource(R.drawable.ic_play_circle_outline_black_32dp);
        }

        viewHolder.btnMeasurementStartStop.setTag(position);
        viewHolder.btnMeasurementReset.setTag(position);
        viewHolder.btnMeasurementDelete.setTag(position);
        viewHolder.chronometer.setTag(position);

        viewHolder.chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                CustomChronometer cChronometer = (CustomChronometer)chronometer;
                measurement.setElapsedTimeInMilllis(cChronometer.getCurrentTime());
            }
        });
        viewHolder.chronometer.setCurrentTime(measurement.getElapsedTimeInMillis());

        viewHolder.btnMeasurementStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int)view.getTag();

                Measurement measurement = getItem(position);
                if (measurement.isRunning()) {
                    measurement.setRunning(false);
                    viewHolder.chronometer.stop();
                    measurement.setElapsedTimeInMilllis(viewHolder.chronometer.getCurrentTime());
                    viewHolder.btnMeasurementStartStop.setImageResource(R.drawable.ic_play_circle_outline_black_32dp);
                }
                else {
                    measurement.setRunning(true);
                    viewHolder.chronometer.start();
                    viewHolder.btnMeasurementStartStop.setImageResource(R.drawable.ic_pause_circle_outline_black_32dp);
                }
            }
        });

        viewHolder.btnMeasurementReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int)view.getTag();
                Measurement measurement = getItem(position);
                measurement.setRunning(false);
                viewHolder.btnMeasurementStartStop.setImageResource(R.drawable.ic_play_circle_outline_black_32dp);
                viewHolder.chronometer.reset();
                measurement.setElapsedTimeInMilllis(0);
            }
        });

        viewHolder.btnMeasurementDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int)view.getTag();
                Measurement measurement = getItem(position);
                measurement.setRunning(false);
                remove(measurement);

                // TOOD: probably not needed -->
                notifyDataSetChanged();
            }
        });



        return convertView;
    }
}