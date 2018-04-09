package nl.bittrail.idlogitimer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{

    ImageButton btnNewSubject,
            btnRedrawLayout,
            btnSaveMeasurements;
    Switch switchLockUI;
    ListView lvSubjects;
    Handler handler;
    long handlerDelay = 200;
    ArrayList<Subject> subjects;
    SubjectsAdapter subjectsAdapter;

    static final String BUNDLE_KEY_SUBJECTS = "SUBJECTS";
    static final String BUNDLE_KEY_NR_OF_SUBJECTS = "NR_OF_SUBJECTS";


    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
//        state.putInt(BUNDLE_KEY_NR_OF_SUBJECTS, nrOfSubjects);
        state.putParcelableArrayList(BUNDLE_KEY_SUBJECTS, subjects);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.menuAbout:
                intent = new Intent(this, SplashActivity.class);
                intent.putExtra(SplashActivity.EXTRA_KEY_USE_DURATION, false);
                this.startActivity(intent);
                return true;
            case R.id.menuLockUI:
                Switch swLockUI = findViewById(R.id.switchLockUI);
                swLockUI.setChecked(true);
                return true;
            case R.id.menuSave:
                handleSaveMeasurement();
                return true;
            case R.id.menuSettings:
                intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();

        initSubjectStuff(savedInstanceState);

        ImageButton btnDbg = (ImageButton)findViewById(R.id.btnDbg);
        btnDbg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String test = "iets";
            }
        });

        btnRedrawLayout = (ImageButton)findViewById(R.id.btnRedrawLayout);
        btnRedrawLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redrawLayout();
            }
        });

        btnSaveMeasurements = (ImageButton)findViewById(R.id.btnSaveMeasurements);
        btnSaveMeasurements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSaveMeasurement();
            }
        });

        switchLockUI = (Switch)findViewById(R.id.switchLockUI);
        switchLockUI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(this.getClass().getName(),"bla");
                for (int i = 0; i < lvSubjects.getCount();i++) {
                    View vSubject = lvSubjects.getChildAt(i);
                    EditText etSubjName = (EditText)vSubject.findViewById(R.id.txtSubjectName);
                    etSubjName.setEnabled(!isChecked);
                    ListView lvMeasurements = vSubject.findViewById(R.id.lvMeasurements);
                    for (int j = 0; j < lvMeasurements.getCount(); j++) {
                        View vMeasurement = lvMeasurements.getChildAt(j);
                        EditText etMeasName = (EditText)vMeasurement.findViewById(R.id.txtMeasurementName);
                        etMeasName.setEnabled(!isChecked);
                    }
                }
            }
        });

        handler = new Handler();
//        handler.postDelayed(runnable,1000);
    }


    private void redrawLayout() {
        // TODO: hack to resize listview...probably not the proper way to dynamically handle layout...

//        int count = subjectsAdapter.getCount();
//        int itemsHeight;
        int totalHeight = 0;

        for (int i = 0; i < lvSubjects.getCount();i++) {
            View v = lvSubjects.getChildAt(i);
            if (v != null) {
                totalHeight += lvSubjects.getChildAt(i).getHeight();
            }

        }
//        View oneChild = lvSubjects.getChildAt(0);
//        if( oneChild == null){
//            return;
//        }
//
//        itemsHeight = oneChild.getHeight();

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)lvSubjects.getLayoutParams();
//        params.height = (itemsHeight * count);
        params.height = totalHeight;
        lvSubjects.setLayoutParams(params);
    }

    private boolean saveMeasurements(String filename) {

        if (!Util.IsExternalStorageWritable()) {
            Toast.makeText(this, "External storage not available...", Toast.LENGTH_LONG).show();
            return false;
        }

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),filename);

        File dirPart = file.getParentFile();
        if (dirPart.exists() == false) {
            if (!dirPart.mkdirs()) {
                Log.e(this.getClass().getSimpleName(), "saveMeasurements - unable to create dir " + dirPart.getPath());
            }
        }

        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

        try {
            String separator = ";";
            String header = "SUBJECT" + separator
                    + "MEASUREMENT" + separator
                    + "CREATED" + separator
                    + "ELAPSED" + "\r\n";
            outputStreamWriter.write(header);

            for (int i = 0; i < subjectsAdapter.getCount(); i++) {
                Subject s = subjectsAdapter.getItem(i);
                for (Measurement m : s.getMeasurements()) {
                    String row = s.getName() + separator
                            + m.getName() + separator
                            + m.getCreatedFormatted() + separator
                            + m.getElapsedTimeFormatted() + "\r\n";

                    outputStreamWriter.write(row);
                }
            }
            outputStreamWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private Runnable runnable = new Runnable() {

        public void run() {
            if (lvSubjects != null) {
                for (int cnt = 0; cnt < lvSubjects.getChildCount(); cnt++) {
                    View sv = lvSubjects.getChildAt(cnt);
                    if (sv != null) {
                        TextView tvTotalElapsed = (TextView) sv.findViewById(R.id.lblTotalElapsed);
                        Subject subject = (Subject) lvSubjects.getAdapter().getItem(cnt);
                        tvTotalElapsed.setText(subject.getTotalElapsedFormatted());
                    }
                }
            }
            handler.postDelayed(this, handlerDelay);
        }

    };

    private boolean requestPermissions() {

        int requestCode = 0;
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED ) {
            //if you dont have required permissions ask for it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        }

        // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},requestCode);
        return true;
    }

    private void showFilenameDialog(String suggestedFilename) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.SaveFileDialog_title);

        final EditText input = new EditText(this);
        input.setText(suggestedFilename);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String filename = input.getText().toString();
                saveMeasurements(filename);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void initSubjectStuff(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //nrOfSubjects = savedInstanceState.getInt(this.BUNDLE_KEY_NR_OF_SUBJECTS);
            subjects = savedInstanceState.getParcelableArrayList(this.BUNDLE_KEY_SUBJECTS);
        }
        else {
            subjects = new ArrayList<Subject>();
        }

        subjectsAdapter = new SubjectsAdapter(this, subjects);
//        subjectsAdapter = new SubjectsAdapter(this, new ArrayList<Subject>());

        lvSubjects = (ListView)findViewById(R.id.lvSubjects);
        lvSubjects.setAdapter(subjectsAdapter);

        btnNewSubject = (ImageButton)findViewById(R.id.btnNewSubject);
        btnNewSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int subjectID = ApplicationController.Companion.getNextSubjectID();
                Subject newSubject = new Subject(subjectID, "Subject " + subjectsAdapter.getCount(), getApplicationContext());
                subjectsAdapter.add(newSubject);

                ///////////////////////////////////
                // TODO: hack to resize listview...probably not the proper way to dynamically handle layout...

                int count = subjectsAdapter.getCount();
                int itemsHeight;

                View oneChild = lvSubjects.getChildAt(0);
                if( oneChild == null){
                    return;
                }

                itemsHeight = oneChild.getHeight();

                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)lvSubjects.getLayoutParams();
                params.height = (itemsHeight * count);
                lvSubjects.setLayoutParams(params);
            }
        });
    }

    private void handleSaveMeasurement() {
        SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-MM-dd_HHmm");
        Date currDate = new Date();
        String strDate = simpleDateFormat.format(currDate);
        String suggestedFilename = "measurements_" + strDate + ".csv";

        showFilenameDialog(suggestedFilename);
    }

    private void dbg() {
        String t1 = "t1";
        String t2 = "t2";
        String res = t1 + t2;


    }
}
