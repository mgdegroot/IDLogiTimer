package nl.bittrail.idlogitimer

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class SettingsActivity : SettingsFragment.OnFragmentInteractionListener, AppCompatActivity() {
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

    }
}
