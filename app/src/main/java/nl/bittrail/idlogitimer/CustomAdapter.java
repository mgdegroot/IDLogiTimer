package nl.bittrail.idlogitimer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

//
//import android.content.Context;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//
public class CustomAdapter extends ArrayAdapter {
    public CustomAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
//
//    public void test() {
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }
}
