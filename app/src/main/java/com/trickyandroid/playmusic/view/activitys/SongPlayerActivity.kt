package com.trickyandroid.playmusic.view.activitys

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.trickyandroid.playmusic.R
import com.trickyandroid.playmusic.app.AppController
import com.trickyandroid.playmusic.utils.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.trickyandroid.playmusic.databinding.ActivitySongPlayerBinding
import com.trickyandroid.playmusic.viewmodel.MainViewModel
import com.trickyandroid.playmusic.viewmodel.SongPlayerViewModel
import com.trickyandroid.playmusic.viewmodel.ViewModelFactory

class SongPlayerActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, Observer,
    LifecycleOwner {

    val viewModel = SongPlayerViewModel()

    companion object {
        var handler: Handler = Handler()
        var runnable: Runnable? = null
        var mainViewModel:MainViewModel?=null

        fun updateProgressBar() {
            handler.postDelayed(runnable!!, 100)
        }

        var mmr: MediaMetadataRetriever = MediaMetadataRetriever()

        @SuppressLint("StaticFieldLeak")
        var activitySongPlayerBinding: ActivitySongPlayerBinding? = null

        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
        var utils: Utilities? = null

        //        var songComposer: String? = null
        //        var MovieName: String? = null
        var movieYear: String? = null
        var songName: String? = null
//        var songComposer: String? = null

        fun loadView(v: View): Bitmap {
            val b = Bitmap.createBitmap(v.width, v.height - 80, Bitmap.Config.ARGB_8888)
            val c = Canvas(b)
            v.layout(0, 0, v.width, v.height)
            v.draw(c)
            return b
        }

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        fun songDetails(index: Int) {
            try {
                if (activitySongPlayerBinding == null)
                    return

                if (MainActivity.exoPlayer?.playWhenReady!!) {
                    if (activitySongPlayerBinding?.fabPlaynPause != null && !activitySongPlayerBinding!!.fabPlaynPause.toggle())
                        activitySongPlayerBinding!!.fabPlaynPause.toggle()
                } else if (MainActivity.exoPlayer != null && (!MainActivity.exoPlayer?.playWhenReady!!)) {
                    if (activitySongPlayerBinding!!.fabPlaynPause != null && activitySongPlayerBinding!!.fabPlaynPause.toggle())
                        activitySongPlayerBinding!!.fabPlaynPause.toggle()
                }

                mmr.setDataSource(MainActivity.SongsInfoList[index].getSongPath())
                movieYear = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)
                songName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)

                activitySongPlayerBinding!!.tvSongName.text =  if (!songName.isNullOrEmpty())
                    "$songName Song " else Constants.UNKNOWN

                activitySongPlayerBinding!!.tvSongYear.text= if (!movieYear.isNullOrEmpty()) " ${movieYear!!}"
                else Constants.UNKNOWN

                activitySongPlayerBinding!!.tvMovieName.text = if (!MainActivity.SongsInfoList[index].getSongMoviename().isNullOrEmpty()) " ${MainActivity.SongsInfoList[index].getSongMoviename()}"
                else Constants.UNKNOWN

                activitySongPlayerBinding!!.tvSongArtist.text =  if (!MainActivity.SongsInfoList[index].getSongComposer().isNullOrEmpty()) " ${MainActivity.SongsInfoList[index].getSongComposer()}"
                else Constants.UNKNOWN

                activitySongPlayerBinding!!.imvSongImage.setImageResource(R.drawable.default_album_bg)

                val data = mmr.embeddedPicture

                if (data?.isNotEmpty()!!) {
                    val bitmap: Bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                    activitySongPlayerBinding!!.imvSongImage.setImageBitmap(bitmap)
                    activitySongPlayerBinding!!.imvSongImage.adjustViewBounds = true
                    val bler = BlurBuilder()
                    activitySongPlayerBinding!!.layoutSongPlayerActivity.background = BitmapDrawable(Resources.getSystem(), bler.blur(context!!, bitmap))

                } else {
                    activitySongPlayerBinding!!.imvSongImage.setImageResource(R.drawable.default_album_bg)
                }

                if (AppController.equalizerActivity != null) {
//                EqualizerActivity.getInstance().setupVisualizerFxAndUI()
                    AppController.equalizerActivity?.setupVisualizerFxAndUI()
                    EqualizerActivity.mVisualizer!!.enabled = true
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel=ViewModelProvider(this, ViewModelFactory.getInstance())[MainViewModel::class.java]
        activitySongPlayerBinding = DataBindingUtil.setContentView(this, R.layout.activity_song_player)
        activitySongPlayerBinding!!.viewModel = viewModel
        viewModel.addObserver(this)
        lifecycle.addObserver(viewModel)

        AppController.songPlayerActivity = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activitySongPlayerBinding!!.toolBar.title = ""
            setSupportActionBar(activitySongPlayerBinding!!.toolBar)
        }
        initialize()
        songDetails(MainActivity.currentSongIndex)
        initSeekbarRunnable()
        activitySongPlayerBinding!!.songSeekBar.setOnSeekBarChangeListener(this)
    }

    fun initialize(){
        MainActivity.songCurrentDurationLabel = findViewById<TextView>(R.id.tvCurrentTime) as TextView
        MainActivity.songTotalDurationLabel = findViewById<TextView>(R.id.tvTotalTime) as TextView
        context = this
        utils = Utilities()
        activitySongPlayerBinding!!.tvSongName.isSelected = true
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun snapShot(): Unit {

        val alert = AlertDialog.Builder(this)
        val a = alert.create()
        val view1 = LayoutInflater.from(this.applicationContext).inflate(R.layout.layout_alert_snapshot, null)
        val relativeAlertSnapshot = view1.findViewById<View>(R.id.linearAlertSnapshot) as RelativeLayout
        val tvMovieName = view1.findViewById<View>(R.id.tvMovieName) as TextView
        val tvSongName = view1.findViewById<View>(R.id.tvSongName) as TextView
        val imvSongImage1 = view1.findViewById<View>(R.id.imvSongImage) as ImageView
        val cvCancel = view1.findViewById<View>(R.id.cvCancel) as CardView
        val cvShare = view1.findViewById<View>(R.id.cvShare) as CardView
        val model = MainActivity.SongsInfoList[MainActivity.currentSongIndex]
        val bler = BlurBuilder()
        val b = BitmapFactory.decodeResource(context!!.resources, R.drawable.default_album_bg)
        relativeAlertSnapshot.background =
            BitmapDrawable(context!!.resources, bler.blur(SongPlayerActivity?.context!!, b))

        try {
            tvMovieName.text = if (!model.getSongMoviename().isNullOrEmpty()) model.getSongMoviename() else Constants.UNKNOWN
            tvSongName.text = if (!model.getSongName().isNullOrEmpty()) model.getSongName() else Constants.UNKNOWN

            if (model.getSongImgPath().length != 0 && model.getSongImgPath() != null) {
                imvSongImage1.setImageURI(Uri.parse(model.getSongImgPath()))
                val bitmap = BitmapFactory.decodeFile(model.getSongImgPath())
                relativeAlertSnapshot.background = BitmapDrawable(context!!.resources, bler.blur(SongPlayerActivity?.context!!, bitmap))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        a.setView(view1)
        a.setCancelable(false)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            a.window!!.setType(WindowManager.LayoutParams.TYPE_TOAST)
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M || Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
            a.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            a.window!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        }

        a.window!!.attributes.windowAnimations = R.style.dialog_animation
        val window = a.window
        window!!.setGravity(Gravity.CENTER)
        a.show()

        cvCancel.setOnClickListener {
            a.cancel()
            a.dismiss()
        }

        cvShare.setOnClickListener {
            saveFile(loadView(view1))
            a.cancel()
            a.dismiss()
        }
    }

    fun saveFile(bitmap: Bitmap): Unit {

        val extr = Environment.getExternalStorageDirectory().absolutePath + File.separator + "/Pictures"
        val fileName = SimpleDateFormat("yyyyMMddhhmm'_bitmap.jpg'", Locale.US).format(Date())
        val myPath = File(extr, fileName)
        var fos: FileOutputStream? = null

        try {
            fos = FileOutputStream(myPath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Screen", "screen")

            /*Share the images to Shared Apps*/
            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/jpeg"
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse(myPath.toString()))
            startActivity(Intent.createChooser(share, "Share Image"))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun initTotalTime() {
        MainActivity.songTotalDurationLabel = findViewById<TextView>(R.id.tvTotalTime) as TextView
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_songplayer, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.menuSetRingtone -> try {
                setTone(this@SongPlayerActivity, MainActivity.SongsInfoList[MainActivity.currentSongIndex].getSongPath())
            } catch (e: Exception) {
                e.printStackTrace()
            }

            R.id.menuSongShare -> {
                try {
                    val uri = Uri.parse(MainActivity.SongsInfoList[MainActivity.currentSongIndex].getSongPath())
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "audio/*"
                    share.putExtra(Intent.EXTRA_STREAM, uri)
                    startActivity(Intent.createChooser(share, "Share Sound File"))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            R.id.menuSongRecord -> {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        RecordAlertDialog(this@SongPlayerActivity).showRecordDialog(StringBuilder("Song Recording"))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun setTone(context: Context, path: String) {

        if (path.isNullOrEmpty()) {
            return
        }

        val file = File(path)
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DATA, file.absolutePath)
        val filterName: String = path.substring(path.lastIndexOf("/") + 1)
        contentValues.put(MediaStore.MediaColumns.TITLE, filterName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
        contentValues.put(MediaStore.MediaColumns.SIZE, file.length())
        contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true)
        var uri: Uri = MediaStore.Audio.Media.getContentUriForPath(path)!!

        var cursor: Cursor = context.contentResolver!!.query(uri, null, MediaStore.MediaColumns.DATA + "=?", arrayOf(path), null)!!
        if (cursor != null && cursor.moveToFirst() && cursor.count > 0) {
            var id: String = cursor.getString(0)
            contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true)
            context.contentResolver!!.update(uri, contentValues, MediaStore.MediaColumns.DATA + "=?", arrayOf(path))
            val newuri: Uri = ContentUris.withAppendedId(uri, id.toLong())
            try {

                if (checkSystemWritePermission()) {
                    RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newuri)
                    Toast.makeText(context, "Set as Ringtone Successfully.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Allow modify system settings ==> ON ", Toast.LENGTH_SHORT).show()

                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
            cursor.close()
        }
    }


    private fun checkSystemWritePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(context))
                return true
            else
                openAndroidPermissionsMenu()
        } else {
            return true
        }
        return false
    }

    private fun openAndroidPermissionsMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data=Uri.parse("package:" + context?.packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun update(o: Observable?, arg: Any?) {
        if (arg is View) {

            when (arg.id) {

                R.id.imvPlayrPause, R.id.fabPlaynPause -> AppController.mainActivity?.PlayorPause()

                R.id.imvBackward -> {

                    AppController.mainActivity?.songBackward()
                    MainActivity.songTotalDurationLabel = findViewById<TextView>(R.id.tvTotalTime) as TextView
                    //Error pls
                    if (MainActivity.SongsInfoList[MainActivity.currentSongIndex].getSongImgPath() != "null") {
                        activitySongPlayerBinding!!.imvSongImage.setImageURI(Uri.parse(MainActivity.SongsInfoList[MainActivity.currentSongIndex].getSongImgPath()))
                    } else {
                        activitySongPlayerBinding!!.imvSongImage.setImageResource(R.drawable.default_album_bg)
                    }
                }

                R.id.imvForward -> {

                    AppController.mainActivity?.songForward()
                    MainActivity.songTotalDurationLabel = findViewById<TextView>(R.id.tvTotalTime) as TextView
                    //Error pls
                    if (MainActivity.SongsInfoList[MainActivity.currentSongIndex].getSongImgPath() != "null") {
                        activitySongPlayerBinding!!.imvSongImage.setImageURI(Uri.parse(MainActivity.SongsInfoList[MainActivity.currentSongIndex].getSongImgPath()))
                    } else {
                        activitySongPlayerBinding!!.imvSongImage.setImageResource(R.drawable.default_album_bg)
                    }

                }

                R.id.imvRepeat -> AppController.mainActivity?.songRepeat()

                R.id.imvShuffle -> AppController.mainActivity?.songShuffle()

                R.id.imvSongImage -> {
                }

                R.id.imvSnapShot -> snapShot()

                R.id.imvVolume -> {

                    val audioManager: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_SHOW_UI)
                }

                R.id.imvEqualizer -> {
                    startActivity(Intent(this@SongPlayerActivity, EqualizerActivity::class.java))
                }

                R.id.imvGoBack -> onBackPressed()

            }

        }
    }

    override fun onDestroy() {
        AppController.songPlayerActivity=null
        super.onDestroy()
    }


    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
//        MainActivity.mHandler.removeCallbacks(MainActivity.mUpdateTimeTask)
//        handler.removeCallbacks(runnable!!)
//        updateProgressBar()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        try {

           handler.removeCallbacks(runnable!!)
            val totalDuration = MainActivity.exoPlayer?.duration!!.toInt()
            val currentPosition = utils?.progressToTimer(seekBar.progress, totalDuration)!!

            MainActivity.mp3Playerservice?.setProgress(currentPosition.toLong())

            updateProgressBar()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun initSeekbarRunnable()
    {
        runnable = object : Runnable {
            override fun run() {
                try {
                    if (mainViewModel?.getBindingsUdpating()?.value != null) {
                        val totalDuration =
                            mainViewModel!!.getBindingsUdpating().value?.getService()!!.getExoplayer().duration.toLong()
                        val currentDuration =
                            mainViewModel!!.getBindingsUdpating().value?.getService()!!.getExoplayer().currentPosition
                        // Displaying Total Duration time
                        activitySongPlayerBinding!!.tvTotalTime.text =
                            " ${MainActivity.utils?.milliSecondsToTimer(totalDuration)}"

                        // Displaying time completed playing
                        activitySongPlayerBinding!!.tvCurrentTime.text =
                            " ${MainActivity.utils?.milliSecondsToTimer(currentDuration)}"

                        activitySongPlayerBinding!!.songSeekBar.progress =
                            mainViewModel!!.getBindingsUdpating().value?.getService()!!.getProgress()

                        //Running this thread after 100 milliseconds
                        handler.postDelayed(this, 100)

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        handler.postDelayed(runnable!!, 100)


    }

}
