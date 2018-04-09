package nl.bittrail.idlogitimer

import android.app.Application

/**
 * Created by blaatenator on 3/23/18.
 */
class ApplicationController : Application() {

    companion object {
        var theNextMeasurementID = 0
        var theNextSubjectID = 0

        fun getNextSubjectID() : Int {
            theNextSubjectID++
            return theNextSubjectID
        }
    }


}