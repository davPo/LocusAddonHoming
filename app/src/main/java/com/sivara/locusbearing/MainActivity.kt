package com.sivara.locusbearing

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bahadirarslan.jeodezi.Coordinate
import com.sivara.locusbearing.LocusCalls.pickLocation
import com.sivara.locusbearing.databinding.ActivityMainBinding

import locus.api.android.ActionBasics
import locus.api.android.features.sendToApp.SendMode
import locus.api.android.features.sendToApp.SendToAppHelper
import locus.api.android.features.sendToApp.tracks.SendTrack
import locus.api.android.objects.LocusVersion
import locus.api.android.objects.VersionCode
import locus.api.android.utils.IntentHelper
import locus.api.android.utils.LocusConst
import locus.api.android.utils.LocusUtils
import locus.api.android.utils.exceptions.RequiredVersionMissingException
import locus.api.objects.extra.GeoDataExtra
import locus.api.objects.extra.Location
import locus.api.objects.geoData.Point
import locus.api.objects.geoData.Track
import locus.api.objects.styles.GeoDataStyle
import locus.api.utils.Logger

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    var bearingIn: Double = 0.0
    var vectorlengthIn : Double = 1.0
    var currentlocation : Location = Location(0.0, 0.0)
    var projected : Location = Location(0.0, 0.0)

    val vector_km: DoubleArray = doubleArrayOf(1.0, 10.0, 50.0)
    val red_flag_icon = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAABmJLR0QA/wD/AP+gvaeTAAAA6UlEQVRoge3YsQ3CQAyF4XeIcbIB0JElUmcP2Is0EWGMzGIqS4gK7my/K/z1SO8XSS4KkFL6yTjNMk6zsHd8O7AHhNkHyD5A5IJNzriy96iaf+CEgkcvIS2XUBchFvcANcTyJqaEeDyFQkM8H6MhIRHngGtI5EHmEsI4iU1DmK8SJiE9vAs1hfQQoKpCegqocmQP+PCC4F42LP/8qIeAquGKGdA0XDECTIaryADT4SoiwGW48gxwHa48AkKGK8uA0OHKIoAyXLUEUIermoAVwK08sVqPcZXfRp1kAFsGsGUAWwawZUBKiesN6tVxvhMjpPkAAAAASUVORK5CYII="
    val green_flag_icon = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAABmJLR0QA/wD/AP+gvaeTAAABGklEQVRoge2WPw4BURCH5+06gnAAvUKrESGcQUHjGkLiGgpOgSzFJvYYzjKqaVT7/sz7kcxXb/F9efveDJFhGK2Yr7Y8X20Z7fFNgRaIxbX9cLgbMRFRd9BrCseHx+b+0NNqj/cJOOIxM1XT8/I1uyxmGlI+BP9CvxISfQfQIckuMSok+SuUO0TtGc0Voj4HtEOyDTKtkOyTOHUIbJVIFQLfhWJD4AFCaMjPBITSQQsITK4pHB+em5vXlgsPCBUXYAGx4kL2gFTiQraA1OKCeoCWuKAWoC0uJA/IJS4kC8gtLkQHoMSF4AC0uOAdwMx1WZb7an2tNYR8aR3Qf4+IiKg6niZqNgH8/TZqAWgsAI0FoLEANBZgGAaWDyburZhYAyl2AAAAAElFTkSuQmCC"
    val empty_flag_icon = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAABmJLR0QA/wD/AP+gvaeTAAABmElEQVRoge3YMWsUQRiH8V+CCAYiWBiLpLKyFKzt7FLnK6QwhaWRNCnTWtqmsLjaLl8hhaBFKlNZmMJABEU0JsUy8CKB283d7s4c88AVd8wO/4e5eXfeoR2bWG45NmuucIo3WBs5y0xchc9vTPACS2OGug1J4gMuw/dPeIn740XrRgoO69jHt/D7Bd7h6SjpOhBFEnexhSP8C2OOsY17QwZsy00ikSc4wPcw9hxv8bj3dB2YJpJY1azGx/DMpWbVtnCnr4BtaSsSeabZNz/D8181K7cx13QduI1I4gFe4YsMSvgsIollTfAJ/oQ5T/BaI9w78xCJjFbC5y2SGLyE9yUSGaSEDyGS6LWEDykSeY73miqXMpxiFw/jwIXoMbpQ/1odSJv9XIGbPZbf2LwVU37TC/EszP1DIS/EeET5G+Ys5oiypgla7KGx6GN88Y1V0a3utJPrypAB2zLtOugXDhV0HfT/Bd1n7Cjwgm5hrkz38GjkLDMxVj/SmoXpR6pIblSR3KgiuVFFcqOK5EYVyY0qkhtVpNIT1xWNxnaPT91sAAAAAElFTkSuQmCC"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        binding.buttonSend.setOnClickListener{
            if (getUserValues()) pickLocation(this)
        }

        binding.seekBarLen.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                vectorlengthIn = vector_km.get(p1)
                binding.textViewVector.text = (vectorlengthIn.toString() + " km")
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        // finally check intent that started this sample
        //MainIntentHandler.handleStartIntent(this, intent)
       intentHandler(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Check if input c is in between min a and max b and
    // returns corresponding boolean
    private fun isInRange(a: Double, b: Double, value: Double): Boolean {
        return if (b > a) value in a..b else value in b..a
    }

    private fun getUserValues(): Boolean {
        val tmp = binding.editTextBearing.getText().toString().toDouble()
        if (isInRange(0.0,360.0,tmp)) {
            bearingIn = tmp
            Logger.logD("App", "distance:"+vectorlengthIn+" angle:"+ bearingIn)
            return true
        }
        else {
            Toast.makeText(this, "Bearing not in range 0..360", Toast.LENGTH_SHORT).show()
            return false
        }

    }

    fun intentHandler(intent: Intent?) {
        // check intent
        Logger.logD("App", "received intent: $intent")
        if (intent == null) {
            return
        }
        if (IntentHelper.isIntentReceiveLocation(intent)) {
            // at this moment we check if returned intent contains location we previously
            // requested from Locus
            val pt = IntentHelper.getPointFromIntent(this, intent)
            if (pt != null) {
                currentlocation = pt.location
                val destination = Bearing.calculate(pt.location.latitude , pt.location.longitude , bearingIn , vectorlengthIn)
                projected = Location(destination.lat , destination.lon)
                callSendOneTrack(this)
                Logger.logD("App", "origin:"+currentlocation+" projete:"+ projected)
              } else {
                Logger.logW("App", "request PickLocation, canceled")
            }
        }
    }


    /**
     * Send (display) single track on the Locus Map map.
     *
     * @param ctx current context
     * @throws RequiredVersionMissingException exception in case of missing required app version
     */
    @Throws(RequiredVersionMissingException::class)
    fun callSendOneTrack(ctx: Context) {
        // prepare data track
        val track = generateTrack(currentlocation, projected,"test")

        // get file to share
        val file = SendToAppHelper.getCacheFile(ctx)
        // prepare file Uri: share via FileProvider. You don't need WRITE_EXTERNAL_STORAGE permission for this!
        val uri = FileProvider.getUriForFile(ctx, ctx.getString(R.string.file_provider_authority), file)

        // send data the app. 'SendMode' define core behavior how Locus app handle received data
        val sendResult = SendTrack(SendMode.Basic(), track)
            .sendOverFile(ctx, cacheFile = file, cacheFileUri = uri)
        Logger.logD("App", "callSendOneTrack(), " +
                "send:" + sendResult)
    }

    /**
     * Generate fictive track from defined start location.
     *
     * @param startLat start latitude
     * @param startLon start longitude
     * @return generated track
     */
    private fun generateTrack(origin: Location, destination: Location, info : String): Track {
        val track = Track()
        track.name = info
        track.addParameter(GeoDataExtra.PAR_DESCRIPTION, "bla bla ...")



        val style = GeoDataStyle()
        style.setLineStyle(Color.RED, 3.0f)
        track.styleNormal = style

        // generate points
        val locs = ArrayList<Location>()
        locs.add(origin)
        locs.add(destination)

        track.points = locs

        // set some points as highlighted wpts
        val pts = ArrayList<Point>()
        val pt = Point("loc", origin)

        style.setIconStyle(red_flag_icon, 2.0f)
        pt.styleNormal = style

        pts.add(pt)
        track.waypoints = pts
        return track
    }

}


