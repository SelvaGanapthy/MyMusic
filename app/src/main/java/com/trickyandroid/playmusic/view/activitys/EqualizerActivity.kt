package com.trickyandroid.playmusic.view.activitys

import android.graphics.Color
import android.media.AudioManager
import android.media.audiofx.Equalizer
import android.media.audiofx.Visualizer
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.trickyandroid.playmusic.R
import com.trickyandroid.playmusic.app.AppController
import com.trickyandroid.playmusic.utils.VisualizerView

class EqualizerActivity : AppCompatActivity() {

    var mEqualizer: Equalizer? = null
    var mLinearLayout: LinearLayout? = null
    var linearSeekBarLayout: LinearLayout? = null

    companion object {
        var mVisualizer: Visualizer? = null
        var mVisualizerView: VisualizerView? = null
        var equalizerActivity: EqualizerActivity? = null

        fun getInstance(): EqualizerActivity {
            if (equalizerActivity == null)
                equalizerActivity = EqualizerActivity()
            return equalizerActivity!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equalizer)
        AppController.equalizerActivity = this
        mLinearLayout = findViewById<View>(R.id.mLinearLayout) as LinearLayout
        linearSeekBarLayout = findViewById<View>(R.id.linearSeekBarLayout) as LinearLayout

        //        set the device's volume control the audio stream we'll be playing
        volumeControlStream = AudioManager.STREAM_MUSIC

        //        create the equlizer with default priority of 0  & attach to media player

        mEqualizer = Equalizer(0, MainActivity.exoPlayer!!.audioSessionId)
        mEqualizer?.enabled = true

        //        set up visualizer and equalizer bars
        setupVisualizerFxAndUI()
        SetupEqualizerFxAndUI()

        // Enable the visualizer
        // Make sure the visualizer is enabled only when you actually want to
        // receive data, and
        // when it makes sense to receive data.
        mVisualizer?.enabled = true

    }


    fun SetupEqualizerFxAndUI() {

        //        TextView equalizerHeading = new TextView(EqualizerActivity.this);
        //        equalizerHeading.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //        equalizerHeading.setText("Equalizer");
        //        equalizerHeading.setTextColor(Color.parseColor("#ffffff"));
        //        equalizerHeading.setTextSize(18);
        //        equalizerHeading.setGravity(Gravity.CENTER_HORIZONTAL);
        //        linearSeekBarLayout.addView(equalizerHeading);


        //        get the level ranges to be used in setting the band level
        var numberFrequencyBands = mEqualizer?.numberOfBands

        //        get the level ranges to be used in setting the band level
        //        get lower limit of the range  in milliBels
        var lowerEqualizerBandlevel = mEqualizer?.bandLevelRange!![0]
        //        get the upper limit of the range  in milliBels
        var upperEqualizerBandlevel = mEqualizer?.bandLevelRange!![1]


        for (i in 0 until numberFrequencyBands!!) {

            //            frequency header for each seekbar
            val frequencyHeaderTextView = TextView(this@EqualizerActivity)
            frequencyHeaderTextView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            frequencyHeaderTextView.gravity = Gravity.CENTER_HORIZONTAL
            frequencyHeaderTextView.text = (mEqualizer?.getCenterFreq(i.toShort())!! / 1000).toString() + "Hz"
            frequencyHeaderTextView.setTextColor(Color.parseColor("#ccfd36"))
            //            mLinearLayout.addView(frequencyHeaderTextView);
            linearSeekBarLayout?.addView(frequencyHeaderTextView)

            //        setup Linear Layout  to contain seekBars
            var seekBarRowLayout = LinearLayout(this)

            var seekbarlayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            seekbarlayoutParams.weight = 1f
            seekbarlayoutParams.setMargins(20, 3, 20, 0)
            seekbarlayoutParams.gravity = Gravity.CENTER_HORIZONTAL
            seekBarRowLayout.orientation = LinearLayout.HORIZONTAL
            seekBarRowLayout.layoutParams = seekbarlayoutParams

            //        setup Lower Level Textview for this seekbar
            val lowerEqualizerBandLevelTextView = TextView(this)
            lowerEqualizerBandLevelTextView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lowerEqualizerBandLevelTextView.text = (lowerEqualizerBandlevel / 100).toString() + "dB"
            lowerEqualizerBandLevelTextView.setTextColor(Color.parseColor("#FFFFFF"))

            //        setup Upper Level Textview for this seekbar
            var upperEqualizerBandLevelTextView = TextView(this)
            upperEqualizerBandLevelTextView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            upperEqualizerBandLevelTextView.text = (upperEqualizerBandlevel / 100).toString() + "dB"
            upperEqualizerBandLevelTextView.setTextColor(Color.parseColor("#FFFFFF"))

            //        ***** the seekbar ***
            //               set the Layout parameters  for the seekbar
            var layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.weight = 1f
            //        create a new seekBar
            var seekBar = SeekBar(this)
            //        give the seekBar and Id
            seekBar.id = i
            seekBar.layoutParams = layoutParams
            seekBar.max = upperEqualizerBandlevel - lowerEqualizerBandlevel
            //        set the progress for this seekbar
            seekBar.progress = mEqualizer?.getBandLevel(i.toShort())!!.toInt()
            //     change progress as its changed by moving  the sliders
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
                    mEqualizer?.setBandLevel(i.toShort(), (progress + lowerEqualizerBandlevel).toShort())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }
            })


            seekBarRowLayout.addView(lowerEqualizerBandLevelTextView)
            seekBarRowLayout.addView(seekBar)
            seekBarRowLayout.addView(upperEqualizerBandLevelTextView)
            //            mLinearLayout.addView(seekBarRowLayout);
            linearSeekBarLayout?.addView(seekBarRowLayout)
        }

        equalizeSound()

    }


    fun equalizeSound() {
        var equalizerPresetNames = ArrayList<String>()
        var equalizerPresetSpinnerAdapter = ArrayAdapter(this, R.layout.spinner_item, equalizerPresetNames)
        equalizerPresetSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //        Spinner equalizerPresetSpinner=
        var equalizerPresetSpinner = findViewById<View>(R.id.spinner) as Spinner

        for (i in 0 until mEqualizer?.numberOfPresets!!) {
            equalizerPresetNames.add(mEqualizer?.getPresetName(i.toShort())!!)
        }
        equalizerPresetSpinner.adapter = equalizerPresetSpinnerAdapter




        if (MainActivity.presetPreviousId != 0) {
            equalizerPresetSpinner.setSelection(MainActivity.presetPreviousId)
        }


        equalizerPresetSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                mEqualizer?.usePreset(position.toShort())
                MainActivity.presetPreviousId = position
                var numberFrequencyBands = mEqualizer?.numberOfBands
                var lowerEqualizerBandLevel = mEqualizer?.bandLevelRange!![0]
                for (i in 0 until numberFrequencyBands!!) {
                    var seekBar = findViewById<View>(i) as SeekBar
                    seekBar.progress = mEqualizer?.getBandLevel(i.toShort())!! - lowerEqualizerBandLevel
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }

    }

    fun setupVisualizerFxAndUI() {

        // Create the Visualizer object and attach it to our media player.
        try {
            volumeControlStream = AudioManager.STREAM_MUSIC
            mVisualizerView = findViewById<View>(R.id.myvisualizerview) as VisualizerView
            mVisualizer = Visualizer(MainActivity.exoPlayer?.audioSessionId!!)
            if (mVisualizer?.enabled!!) {
                mVisualizer?.enabled = false
            }
            mVisualizer?.captureSize = Visualizer.getCaptureSizeRange()[1]

            mVisualizer?.setDataCaptureListener(
                    object : Visualizer.OnDataCaptureListener {
                        override fun onWaveFormDataCapture(visualizer: Visualizer,
                                                           bytes: ByteArray, samplingRate: Int) {
                            mVisualizerView?.updateVisualizer(bytes)
                        }

                        override fun onFftDataCapture(visualizer: Visualizer,
                                                      bytes: ByteArray, samplingRate: Int) {
                        }
                    }, Visualizer.getMaxCaptureRate() / 2, true, false)

            mVisualizer?.enabled = true

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun goBack(v: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        this.finish()
    }

}
