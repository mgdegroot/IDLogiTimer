package nl.bittrail.idlogitimer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by marcel on 3/21/18.
 */

public class SubjectsAdapter extends ArrayAdapter<Subject> {


    public SubjectsAdapter(Context context, ArrayList<Subject> subjects) {
        super(context, 0, subjects);
    }

    public void testie() {
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final Subject subject = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_subject, parent, false);

        }
        final EditText txtSubjectName = (EditText)convertView.findViewById(R.id.txtSubjectName);
        final ImageButton btnNewMeasurement = (ImageButton)convertView.findViewById(R.id.btnNewMeasurement);
        final TextView lblTotalElapsed = (TextView)convertView.findViewById(R.id.lblTotalElapsed);
        final ImageButton btnSubjectDelete = (ImageButton)convertView.findViewById(R.id.btnSubjectDelete);
        final ListView lvMeasurements = (ListView)convertView.findViewById(R.id.lvMeasurements);


        txtSubjectName.setText(subject.getName());

        txtSubjectName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    subject.setName(v.getText().toString());
                    v.clearFocus();

                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

//                    return true;
                }
                return false;
            }

        });

        txtSubjectName.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                subject.setName(((EditText)v).getText().toString());
            }
        });


        lvMeasurements.setAdapter(subject.getMeasurementsAdapter());

        txtSubjectName.setTag(position);
        btnNewMeasurement.setTag(position);
        btnSubjectDelete.setTag(position);
        lblTotalElapsed.setTag(position);
        lblTotalElapsed.setText(subject.getTotalElapsedFormatted());

        btnNewMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int nextMeasurementID = subject.getNextMeasurementID();
                Measurement newMeasurement = new Measurement(nextMeasurementID, "Measurement " + nextMeasurementID);

                newMeasurement.setMeasurementListener(new Measurement.IMeasurementListener() {
                    @Override
                    public void recalculate() {

                    }

                    @Override
                    public void onMeasurementDeleted(Measurement m) {
                        Log.d(this.getClass().getName(), "Measurement " + m.getName() + " deleted");
                        resize_lvMeasurements(subject, lvMeasurements);
                    }

                    @Override
                    public void onTotalIncremented(Measurement m) {
                        Log.d(this.getClass().getName(), "Measurement " + m.getName() + "incremented: " + m.getElapsedTimeFormatted());
                        lblTotalElapsed.setText(subject.getTotalElapsedFormatted());
                    }
                });

                subject.getMeasurementsAdapter().add(newMeasurement);

                resize_lvMeasurements(subject, lvMeasurements);
            }
        });

        btnSubjectDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int)v.getTag();
                Subject subject = getItem(position);
                remove(subject);
            }
        });



        return convertView;
    }

    private void resize_lvMeasurements(Subject subject, ListView lvMeasurements) {
        // TODO: fix hack to re-arrange GUI -->


        int count = subject.getMeasurementsAdapter().getCount();
        int itemsHeight;

        View oneChild = lvMeasurements.getChildAt(0);
        if( oneChild == null){
            return;
        }

        itemsHeight = oneChild.getHeight();

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)lvMeasurements.getLayoutParams();
        params.height = (itemsHeight * count);
        lvMeasurements.setLayoutParams(params);
    }
}