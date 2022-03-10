package com.sivara.locushoming

import android.app.Activity
import locus.api.android.ActionBasics
import locus.api.android.utils.exceptions.RequiredVersionMissingException

object LocusCalls {

    // tag for logger
    // private const val TAG = "SampleCalls"

    /**
     * Send request on a location. This open Locus "Location picker" and allow to choose
     * location from supported sources. Result will be delivered to activity as response
     *
     * @param act current activity
     * @throws RequiredVersionMissingException exception in case of missing required app version
     */
    @Throws(RequiredVersionMissingException::class)
    fun pickLocation(act: Activity) {
        ActionBasics.actionPickLocation(act)
    }

}