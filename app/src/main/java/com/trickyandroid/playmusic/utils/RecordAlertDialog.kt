package com.trickyandroid.playmusic.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import com.trickyandroid.playmusic.R
import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.trickyandroid.playmusic.view.activitys.MainActivity
import com.trickyandroid.playmusic.view.activitys.SongPlayerActivity
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class RecordAlertDialog(var context: Context) {

    //    private val FILE_NAME = "Audio_%s"
    private val FILE_NAME = "SongRecord_"
    private val TIMER_TEXT = "%02d:%02d:%02d"

    private var closeView: ImageView? = null
    private var fileName: String? = null
    private var recordTimer: TextView? = null
    private var recorder: MediaRecorder? = null
    private var recording: Boolean = false
    private var startStopButton: TextView? = null
    private var startTime: Long = 0
    private var ivCancel: ImageView? = null
    private var ivrRecord: ImageView? = null
    private var cvCancel: CardView? = null
    private var linearAlertRecord: RelativeLayout? = null
    private var tvSongName: TextView? = null

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun showRecordDialog(RadioOrSongName: StringBuilder): Unit {

        val alert = AlertDialog.Builder(context, R.style.Custom_Dialog)
        val a = alert.create()
        val view1 = LayoutInflater.from(context).inflate(R.layout.layout_alert_songrecord, null)
        linearAlertRecord = view1.findViewById<View>(R.id.linearAlertRecord) as RelativeLayout
        startStopButton = view1.findViewById<View>(R.id.startStopRecord) as TextView
        recordTimer = view1.findViewById<View>(R.id.tvRecordTimers) as TextView
        tvSongName = view1.findViewById<View>(R.id.tvSongName) as TextView
        closeView = view1.findViewById<View>(R.id.closePopup) as ImageView
        ivrRecord = view1.findViewById<View>(R.id.ivRecord) as ImageView
        cvCancel = view1.findViewById<View>(R.id.cvCancel) as CardView
        val bler = BlurBuilder()
        val b = BitmapFactory.decodeResource(SongPlayerActivity.context!!.resources, R.drawable.default_album_bg)
        linearAlertRecord?.background = BitmapDrawable(bler.blur(SongPlayerActivity?.context!!, b))
        val model = MainActivity.SongsInfoList[MainActivity.currentSongIndex]

        try {

            if (model.getSongName() != null) {
                tvSongName?.text = model.getSongName()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


        cvCancel?.setOnClickListener { v ->
            a.dismiss()
            a.cancel()
        }

        startStopButton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (recording) {
                    recording = false
                    startStopButton?.text = "Start"
                    recordTimer?.text = "Cancel"
                    ivrRecord?.setImageResource(R.drawable.reccordstart)
                    stopRecording()
                    closeView?.isClickable = true
                    a.setCancelable(true)
                    a.dismiss()
                    a.cancel()
                    return
                }

                fileName = createFileName()
                if (startRecording(fileName!!)) {
                    recording = true
                    startStopButton?.text = "Stop"
                    ivrRecord?.setImageResource(R.drawable.reccordstart)
                    startRecording()
                    closeView?.isClickable = false
                    a.setCancelable(true)
                }
            }
        })


        a.setView(view1)
        a.setCancelable(false)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            a.window!!.setType(WindowManager.LayoutParams.TYPE_TOAST)
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M || Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
            a.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            a.window!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        }

        a.getWindow()!!.setLayout(800, ViewGroup.LayoutParams.WRAP_CONTENT)
        a.window!!.attributes.windowAnimations = R.style.dialog_animation
        val window = a.window
        window!!.setGravity(Gravity.CENTER)
        window.setLayout(800, ViewGroup.LayoutParams.WRAP_CONTENT)
        a.show()
    }


    @SuppressLint("SimpleDateFormat")
    private fun createFileName(): String {
        val date = SimpleDateFormat("ddMMMyyyy_HH-mm-ss").format(Date())
        return FILE_NAME + date
    }

    private fun getFilePath(fileName: String): String {
        return StringBuilder(Environment.getExternalStorageDirectory().absolutePath.toString()).append("/").append(fileName).append(".3gp").toString()
    }


    @SuppressLint("HandlerLeak")
    private fun startRecording() {
        this.startTime = Date().time
        val handler = RecordHandler()
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (recording) {
                    val msg = Message()
                    msg.obj = "TIMER"
                    handler.sendMessage(msg)
                    return
                }
                timer.cancel()
                timer.purge()
            }
        }, 0, 1000)
    }


    @SuppressLint("WrongConstant")
    private fun startRecording(fileName: String): Boolean {
        if (this.recorder != null) {
            this.recorder?.release()
        }
        try {
            startRecorder(3)
            return true
        } catch (e: Exception) {
            try {
                startRecorder(1)
                Toast.makeText(context, "Recording from Mic.\nPlease maintain silence & High Speaker Volume.", 1).show()
                return true
            } catch (e1: Exception) {
                Toast.makeText(context, "Unable to record.\nException:" + e1.message, 1).show()
                return false
            }

        }

    }


    @Throws(IllegalStateException::class, IOException::class)
    private fun startRecorder(source: Int) {
        recorder = MediaRecorder()
        recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder?.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        recorder?.setOutputFile(getFilePath(this.fileName!!))
        recorder?.prepare()
        recorder?.start()
    }

    @SuppressLint("WrongConstant")
    private fun stopRecording() {
        if (this.recorder != null) {
            try {
                this.recorder?.stop()
                this.recorder?.release()
                //                this.alertDialogLoader.activity.addRemoveFavourite(new RadioEntity(null, this.fileName, getFilePath(this.fileName), null, null, true), true);
                //                this.alertDialogLoader.addStationDialog.dismiss();
                Toast.makeText(this.context, "Recording Saved to My List.\n" + this.fileName, 1).show()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                this.recorder = null
            }
        }
    }


    inner class RecordHandler : Handler() {

        @SuppressLint("DefaultLocale")
        override fun handleMessage(msg: Message) {
            val message = msg.obj as String
            if (message != null && message.startsWith("TIMER")) {
                var timeDiff: Long = Date().time - startTime
                var seconds: Long = timeDiff / 1000 % 60
                var minutes: Long = timeDiff / 60000 % 60
                var hours: Long = timeDiff / 3600000

//
//                val seconds: Long = millisUntilFinished / 1000
//                val minutes: Long = seconds / 60
//                val hours: Long = minutes / 60
//                val days: Long = hours / 24
//                val time = days.toString() + " " + "days" + " :" + hours % 24 + ":" + minutes % 60 + ":" + seconds % 60
//


//                recordTimer?.setText(java.lang.String.format(TIMER_TEXT, arrayOf<Any>(java.lang.Long.valueOf(hours).toLong(), java.lang.Long.valueOf(minutes), java.lang.Long.valueOf(seconds))))


                recordTimer?.setText(java.lang.String.format(" " + hours + ":" + minutes + ":" + seconds))


            }
        }
    }


}