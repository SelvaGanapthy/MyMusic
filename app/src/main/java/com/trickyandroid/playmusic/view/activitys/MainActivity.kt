package com.trickyandroid.playmusic.view.activitys

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.*
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.*
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util.getUserAgent
import com.trickyandroid.playmusic.R
import com.trickyandroid.playmusic.app.AppController
import com.trickyandroid.playmusic.databinding.ActivityMainBinding
import com.trickyandroid.playmusic.models.SongInfoModel
import com.trickyandroid.playmusic.service.Mp3PlayerService
import com.trickyandroid.playmusic.service.Mp3Receiver
import com.trickyandroid.playmusic.utils.Utilities
import com.trickyandroid.playmusic.view.fragement.AlbumsTab
import com.trickyandroid.playmusic.view.fragement.ArtistsTab
import com.trickyandroid.playmusic.view.fragement.CommonSearchFragment
import com.trickyandroid.playmusic.view.fragement.TracksTab
import com.trickyandroid.playmusic.view.interfaces.IFragmentListener
import com.trickyandroid.playmusic.view.interfaces.ISearch
import com.trickyandroid.playmusic.viewmodel.MainViewModel
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, IFragmentListener, Observer, LifecycleOwner, CommonSearchFragment.CommonRightMenuFragmentListener {

    internal var viewModel: MainViewModel = MainViewModel()
    internal var iSearch: ArrayList<ISearch> = ArrayList()
    var filter: IntentFilter? = null
    var AlbumsList: ArrayList<SongInfoModel> = ArrayList()
    var ArtistsList: ArrayList<SongInfoModel> = ArrayList()
    var TracksList: ArrayList<SongInfoModel> = ArrayList()
    var searchText: String? = null
    private var mFragmentManager: FragmentManager? = null
    private var mFragmentTransaction: FragmentTransaction? = null

    /*Fragment*/
    var adapter1: FragmentAdapter? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var activityMainBinding: ActivityMainBinding
        var id: Int = 0
        var SongsInfoList: ArrayList<SongInfoModel> = ArrayList()
        var fmAndSongsList: ArrayList<SongInfoModel> = ArrayList()
        var curFmModel: SongInfoModel? = null

        @SuppressLint("StaticFieldLeak")
        var exoPlayer: SimpleExoPlayer? = null
        private val BANDWIDTH_METER: DefaultBandwidthMeter = DefaultBandwidthMeter()
        var uri: Uri? = null
        var currentSongIndex = 0
        var utils: Utilities? = null
        var mHandler: Handler = Handler()
        var activeTabPos = 0

        @SuppressLint("StaticFieldLeak")
        var songCurrentDurationLabel: TextView? = null

        //    PlayingSongname
        @SuppressLint("StaticFieldLeak")
        public var songTotalDurationLabel: TextView? = null
        var isShuffle = false
        var isRepeat = false

        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
        var presetPreviousId: Int = 0
        val BLANK_MESSAGE = ""

        fun updateProgressBar(): Unit {
            mHandler.postDelayed(mUpdateTimeTask, 100)
        }

        @SuppressLint("StaticFieldLeak")
        var isFmPlay: Boolean = false

        internal var mUpdateTimeTask: Runnable = object : Runnable {
            override fun run() {
                try {

                    val totalDuration = MainActivity.exoPlayer?.duration!!.toLong()
                    val currentDuration = MainActivity.exoPlayer!!.currentPosition
                    // Displaying Total Duration time
                    songTotalDurationLabel?.text = "" + MainActivity.utils?.milliSecondsToTimer(totalDuration)

                    // Displaying time completed playing
                    songCurrentDurationLabel?.text = "" + MainActivity.utils?.milliSecondsToTimer(currentDuration)

                    //
                    //                // Displaying Total Duration time
                    //                SongPlayerActivity.songTotalDurationLabel.setText("" + MainActivity.utils.milliSecondsToTimer(totalDuration));
                    //
                    //                // Displaying time completed playing
                    //                SongPlayerActivity.songCurrentDurationLabel.setText("" + MainActivity.utils.milliSecondsToTimer(currentDuration));

                    // Updating progress bar
                    val progress = MainActivity.utils?.getProgressPercentage(currentDuration, totalDuration)

                    //Log.d("Progress", ""+progress);
                    activityMainBinding.songSeekBar.progress = progress!!

                    // Running this thread after 100 milliseconds
                    mHandler.postDelayed(this, 100)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun getAlbumArt(uri: String): ByteArray? {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(uri)
            val art: ByteArray? = retriever.embeddedPicture
            retriever.release()
            return art
        }

        fun songFileDeletePermanent(id: Int): Boolean {
            val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, SongsInfoList[id].getSongFileId().toLong())
            val file = File(SongsInfoList[id].getSongImgPath())
            val deleted: Boolean = file.delete()
            if (deleted)
                context?.contentResolver?.delete(contentUri, null, null)
            return deleted
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*DataBinding*/
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityMainBinding.viewModel = viewModel
        viewModel.addObserver(this)
        lifecycle.addObserver(viewModel)

        AppController.mainActivity = this
        context = this
        activityMainBinding.regFirstDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, activityMainBinding.regRightSlidingFrameLayout)
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val params = activityMainBinding.regRightSlidingFrameLayout.getLayoutParams() as DrawerLayout.LayoutParams
        params.width = metrics.widthPixels
        activityMainBinding.regRightSlidingFrameLayout.setLayoutParams(params)
        (getSystemService(AUDIO_SERVICE) as AudioManager).registerMediaButtonEventReceiver(ComponentName(this, Mp3Receiver::class.java))
        activityMainBinding.toolbar.title = ""
        setSupportActionBar(activityMainBinding.toolbar)
        activityMainBinding.layoutSongplay.visibility = View.GONE
        activityMainBinding.tvMovieName.setSelected(true)
        songCurrentDurationLabel = findViewById<View>(R.id.tvCurrentTime) as TextView
        utils = Utilities()
        activityMainBinding.songSeekBar.setOnSeekBarChangeListener(this@MainActivity)
//        setSupportActionBar(toolbar!!)
        try {
//            activityMainBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//
//                override fun onQueryTextSubmit(query: String): Boolean {
//                    return false
//                }
//
//                override fun onQueryTextChange(newText: String): Boolean {
//                    searchText = newText
//                    adapter1?.setTextQueryChanged(newText)
//                    for (iSearchLocal in iSearch)
//                        iSearchLocal.onTextQuery(newText)
//                    return true
//                }
//            })

            if (fmAndSongsList.isEmpty()) {
                getSongsList()
            }

            SongsInfoList.addAll(fmAndSongsList)
            TracksList = ArrayList(SongsInfoList)
            AlbumsList = ArrayList(SongsInfoList)
            ArtistsList = ArrayList(SongsInfoList)

            Collections.sort(TracksList, object : Comparator<SongInfoModel> {
                override fun compare(a: SongInfoModel, b: SongInfoModel): Int {
                    return a.getSongName().compareTo(b.getSongName())
                }
            })

            for (i in 0 until TracksList.size) {
                TracksList[i].setTrackId(i)
            }

            /*Album List initalize Id*/

            Collections.sort(AlbumsList, object : Comparator<SongInfoModel> {
                override fun compare(a: SongInfoModel, b: SongInfoModel): Int {
                    return a.getSongMoviename().compareTo(b.getSongMoviename())
                }
            })

            for (j in 0 until AlbumsList.size) {
                AlbumsList.get(j).setAlbumnewId(j)
            }

            /*Artists List initalize Id*/

            Collections.sort(ArtistsList, object : Comparator<SongInfoModel> {
                override fun compare(a: SongInfoModel, b: SongInfoModel): Int {
                    return a.getSongComposer().compareTo(b.getSongComposer())
                }
            })

            for (k in 0 until ArtistsList.size) {
                ArtistsList[k].setArtistId(k)
            }

            adapter1 = FragmentAdapter(supportFragmentManager, searchText)
            activityMainBinding.viewPager.adapter = adapter1
            activityMainBinding.tabLayout.setupWithViewPager(activityMainBinding.viewPager)
            activityMainBinding.tabLayout.getTabAt(0)?.text = "TRACKS"
            activityMainBinding.tabLayout.getTabAt(1)?.text = "ALBUMS"
            activityMainBinding.tabLayout.getTabAt(2)?.text = "ARTISTS"
            activityMainBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                /**
                 * This method will be invoked when the current page is scrolled, either as part
                 * of a programmatically initiated smooth scroll or a user initiated touch scroll.
                 *
                 * @param position Position index of the first page currently being displayed.
                 * Page position+1 will be visible if positionOffset is nonzero.
                 * @param positionOffset Value from [0, 1) indicating the offset from the page at position.
                 * @param positionOffsetPixels Value in pixels indicating the offset from position.
                 */
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                }

                /**
                 * This method will be invoked when a new page becomes selected. Animation is not
                 * necessarily complete.
                 *
                 * @param position Position index of the new selected page.
                 */
                override fun onPageSelected(position: Int) {
                    activeTabPos = position
                    SongsInfoList = when (position) {
                        0 -> TracksList
                        1 -> AlbumsList
                        2 -> ArtistsList
                        else -> null!!
                    }
                }

                /**
                 * Called when the scroll state changes. Useful for discovering when the user
                 * begins dragging, when the pager is automatically settling to the current page,
                 * or when it is fully stopped/idle.
                 *
                 * @param state The new scroll state.
                 * @see ViewPager.SCROLL_STATE_IDLE
                 *
                 * @see ViewPager.SCROLL_STATE_DRAGGING
                 *
                 * @see ViewPager.SCROLL_STATE_SETTLING
                 */
                override fun onPageScrollStateChanged(state: Int) {
                }

            })


            val linearLayout: LinearLayout = activityMainBinding.tabLayout.getChildAt(0) as LinearLayout
            linearLayout.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            val drawable: GradientDrawable = GradientDrawable()
            drawable.setColor(Color.WHITE)
            drawable.setSize(2, 1)
            linearLayout.dividerPadding = 45
            linearLayout.dividerDrawable = drawable
//            filter = IntentFilter()
//            filter?.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
//            filter?.addAction(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT.toString())
//            registerReceiver(mNoisyReceive, filter)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun getSongsList(): Unit {

        id = 0
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val c = applicationContext.contentResolver.query(uri, null, MediaStore.Audio.Media.IS_MUSIC + " != 0", null, null)

        runOnUiThread(object : Runnable {
            override fun run() {
                if (c!!.moveToFirst() != false && c.moveToFirst()) {
                    do {
                        try {
                            val model = SongInfoModel()
                            val duration = c.getLong(c.getColumnIndex(MediaStore.Audio.Media.DURATION))
                            model.setId(id)
                            model.setSongFileId(c.getString(c.getColumnIndex(MediaStore.Audio.Media._ID)))
                            model.setAlbumId(c.getLong(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)))
                            model.setSongPath(c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA)))
                            model.setSongMoviename(StringFilter(c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM))))
                            model.setSongArtist(c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST)))
                            val artImgUri: Uri = Uri.parse("content://media/external/audio/albumart")
                            model.setSongImgPath(ContentUris.withAppendedId(artImgUri, c.getLong(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))).toString())
                            model.setSongName(StringFilter(c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE))))
                            model.setSongComposer(StringFilter(c.getString(c.getColumnIndex(MediaStore.Audio.Media.COMPOSER))))
                            val dur = c.getInt(c.getColumnIndex(MediaStore.Audio.Media.DURATION))
                            val size = c.getInt(c.getColumnIndex("_size"))

                            if (!(size == 0 || dur == 0)) {
                                val bitrate = size * 8 / dur
                                var duration = BLANK_MESSAGE
                                if (dur > 0) {
                                    val second = dur / 1000 % 60
                                    duration = String.format("%02d:%02d", *arrayOf<Any>(Integer.valueOf(dur / 60000), Integer.valueOf(second)))
                                }
                                model.setSongTime(duration)
                                fmAndSongsList.add(id, model)
                                id++
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } while (c.moveToNext() != false)
                    c.close()
                }

            }
        })

    }

    fun StringFilter(Name: String?): String {
        if (Name != null) {
            var sb: String = Name
            runOnUiThread(object : Runnable {
                override fun run() {
                    try {
                        for (j in 0 until sb.length) {
                            if (sb[j] == '[' || sb[j] == '-' || sb[j] == '_' || sb[j] == '(' || sb[j] == '&') {
                                sb = sb.substring(0, j - 1)
                                break
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
            return sb
        }
        return "Unknown"
    }


    /*FragmentAdapter class*/
    inner class FragmentAdapter(fm: FragmentManager, var mSearchTerm: String?) : FragmentStatePagerAdapter(fm) {

        var tabTitle = arrayOf("TRACKS", "ALBUMS", "ARTISTS")

        override fun getItem(position: Int): Fragment {

            when (position) {

                0 -> {
                    val tab1 = TracksTab()
                    val args1 = Bundle()
                    args1.putSerializable("SongsInfoList", TracksList as Serializable)
                    tab1.arguments = args1
//                    tab1.newInstances(mSearchTerm);
                    return tab1
                }

                1 -> {
                    val tab2 = AlbumsTab()
                    val args2 = Bundle()
                    args2.putSerializable("SongsInfoList", AlbumsList as Serializable)
                    tab2.arguments = args2
//                    tab2.newInstances(mSearchTerm);
                    return tab2
                }

                2 -> {
                    val tab3 = ArtistsTab()
                    val args3 = Bundle()
                    args3.putSerializable("SongsInfoList", ArtistsList as Serializable)
                    tab3.arguments = args3
//                    tab3.newInstances(mSearchTerm);
                    return tab3
                }
            }
            return null!!
        }

        override fun getPageTitle(position: Int): CharSequence {
            return tabTitle[position]
        }

        fun setTextQueryChanged(newText: String): Unit {
            mSearchTerm = newText
        }

        override fun getCount(): Int {
            return tabTitle.size
        }

    }

    override fun addiSearch(iSearch: ISearch) {
        this.iSearch.add(iSearch)
    }

    override fun removeISearch(iSearch: ISearch) {
        this.iSearch.remove(iSearch)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun SongPlay(index: Int): Unit {

        try {

            isFmPlay = false
            activityMainBinding.layoutSongplay.visibility = View.VISIBLE
            currentSongIndex = index
//            uri = Uri.parse(SongsInfoList.get(index).getSongPath().toString())

            startExoplayer(SongsInfoList.get(index).getSongPath())

            // set Progress bar values
            activityMainBinding.songSeekBar.setProgress(0)
            activityMainBinding.songSeekBar.setMax(100)
            SongPlayerActivity.songDetails(index)
            AppController.albumSongActivity?.songDetails(index)

            // Updating progress bar
            updateProgressBar()
            activityMainBinding.imvPlayrPause.setImageResource(R.drawable.ic_pause_black_24dp)
            activityMainBinding.tvSongName.setText("${SongsInfoList.get(index).getSongName()} Song .")
            activityMainBinding.tvMovieName.setText(SongsInfoList.get(currentSongIndex).getSongMoviename())

            songTotalDurationLabel = findViewById<TextView>(R.id.tvTotalTime) as TextView
            songTotalDurationLabel?.setText(SongsInfoList.get(currentSongIndex).getSongTime())

            activityMainBinding.imvSongImage.setImageURI(Uri.parse(SongsInfoList.get(index).getSongImgPath()))

            val intent = Intent(applicationContext, Mp3PlayerService::class.java)
            intent.putExtra("songTitle", SongsInfoList.get(index).getSongName())
            intent.putExtra("movieName", SongsInfoList.get(currentSongIndex).getSongMoviename())
            intent.putExtra("songPath", SongsInfoList.get(index).getSongPath())
            startService(intent)

            AppController.songPlayerActivity?.initTotalTime()
            songTotalDurationLabel?.setText(SongsInfoList.get(currentSongIndex).getSongTime())

        } catch (e: Exception) {
            e.printStackTrace()
        }


        try {
            exoPlayer?.addListener(object : Player.DefaultEventListener() {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (Player.STATE_ENDED == playbackState) {
                        if (isRepeat) {
                            // repeat is on play same song again
                            SongPlay(currentSongIndex)
                        } else if (isShuffle) {
                            // shuffle is on - play a random song
                            var rand: Random = Random()
                            currentSongIndex = rand.nextInt(SongsInfoList.size - 1 - 0 + 1) + 0
                            SongPlay(currentSongIndex)
                        } else {
                            // no repeat or shuffle ON - play next song
                            if (currentSongIndex < SongsInfoList.size - 1) {
                                currentSongIndex = currentSongIndex + 1
                                SongPlay(currentSongIndex)
                            } else {
                                // play first song
                                SongPlay(0)
                                currentSongIndex = 0
                            }
                        }
                    }
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun startExoplayer(songpath: String) {

        if (exoPlayer != null) {
            exoPlayer?.stop()
            exoPlayer?.release()
        }

        val trackSelectionFactory: AdaptiveTrackSelection.Factory = AdaptiveTrackSelection.Factory(BANDWIDTH_METER)
        val trackSelector: DefaultTrackSelector = DefaultTrackSelector(trackSelectionFactory)
        exoPlayer = ExoPlayerFactory.newSimpleInstance(applicationContext, trackSelector)

        val dataSourceFactory = DefaultDataSourceFactory(this, getUserAgent(this, javaClass.simpleName), BANDWIDTH_METER)

        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .setExtractorsFactory(DefaultExtractorsFactory())
                .createMediaSource(Uri.parse(songpath))

        exoPlayer!!.prepare(mediaSource)
        exoPlayer!!.playWhenReady = true
    }

    fun PlayorPause(): Unit {

        try {

            if (exoPlayer == null)
                return


            var intent: Intent
            if (exoPlayer?.playWhenReady!!) {
                exoPlayer?.playWhenReady = false
                activityMainBinding.imvPlayrPause.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                AppController.albumSongActivity?.imvPlayrPause?.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                try {
                    if (SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle())
                        SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle()


                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (!SongsInfoList.isEmpty()) {
                    intent = Intent(this@MainActivity.applicationContext, Mp3PlayerService::class.java)
                    intent.putExtra("songTitle", SongsInfoList.get(currentSongIndex).getSongName())
                    intent.putExtra("movieName", SongsInfoList.get(currentSongIndex).getSongMoviename())
                    intent.putExtra("songPath", SongsInfoList.get(currentSongIndex).getSongImgPath())
                    this@MainActivity.startService(intent)
                }


            } else {

                exoPlayer?.playWhenReady = true
                activityMainBinding.imvPlayrPause.setImageResource(R.drawable.ic_pause_black_24dp)
                AppController.albumSongActivity?.imvPlayrPause?.setImageResource(R.drawable.ic_pause_black_24dp)
                try {
                    if (!SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle())
                        SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (!SongsInfoList.isEmpty()) {
                    intent = Intent(this@MainActivity.applicationContext, Mp3PlayerService::class.java)
                    intent.putExtra("songTitle", SongsInfoList.get(currentSongIndex).getSongName())
                    intent.putExtra("movieName", SongsInfoList.get(currentSongIndex).getSongMoviename())
                    intent.putExtra("songPath", SongsInfoList.get(currentSongIndex).getSongImgPath())
                    this@MainActivity.startService(intent)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun songBackward(): Unit {

        try {
            if (exoPlayer == null) {
                return
            }

            if (currentSongIndex > 0) {

                MainActivity.currentSongIndex--
                SongPlay(MainActivity.currentSongIndex)
            } else {
                // play last song
                SongPlay(SongsInfoList.size - 1)
                currentSongIndex = SongsInfoList.size - 1
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun songForward(): Unit {
        try {
            if (exoPlayer == null) {
                return
            }

            if (currentSongIndex < (SongsInfoList.size - 1)) {

                MainActivity.currentSongIndex++
                SongPlay(MainActivity.currentSongIndex)

            } else {
                // play first song
                SongPlay(0)
                currentSongIndex = 0

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun songShuffle(): Unit {

        if (isShuffle) {
            isShuffle = false
            Toast.makeText(applicationContext, "Shuffle is OFF", Toast.LENGTH_SHORT).show()
            SongPlayerActivity.activitySongPlayerBinding!!.imvShuffle.setImageResource(R.drawable.ic_shuffle_white_24dp)
        } else {
            // make repeat to true
            isShuffle = true
            Toast.makeText(applicationContext, "Shuffle is ON", Toast.LENGTH_SHORT).show()
            // make shuffle to false
            isRepeat = false
            SongPlayerActivity.activitySongPlayerBinding!!.imvShuffle.setImageResource(R.drawable.ic_shuffle_orange_24dp)
            SongPlayerActivity.activitySongPlayerBinding!!.imvRepeat.setImageResource(R.drawable.ic_repeat_white_24dp)
        }

    }

    fun songRepeat(): Unit {
        if (isRepeat) {
            isRepeat = false
            Toast.makeText(applicationContext, "Repeat is OFF", Toast.LENGTH_SHORT).show()
            SongPlayerActivity.activitySongPlayerBinding!!.imvRepeat.setImageResource(R.drawable.ic_repeat_white_24dp)
        } else {
            isRepeat = true
            Toast.makeText(applicationContext, "Repeat is ON", Toast.LENGTH_SHORT).show()
            isShuffle = false
            SongPlayerActivity.activitySongPlayerBinding!!.imvRepeat.setImageResource(R.drawable.ic_repeat_one_white_24dp)
            SongPlayerActivity.activitySongPlayerBinding!!.imvShuffle.setImageResource(R.drawable.ic_shuffle_white_24dp)
        }
    }


    fun playRadio(model: SongInfoModel) {
        curFmModel = model
        isFmPlay = true
        currentSongIndex = model.getId()
        startExoplayer(model.getSongPath())
        SongsInfoList = OnlineRadioActivity.radioList
        activityMainBinding.layoutSongplay.visibility = View.GONE
        val intent = Intent(applicationContext, Mp3PlayerService::class.java)
        intent.putExtra("songTitle", "Fm")
        intent.putExtra("movieName", "Music|Talk")
        intent.putExtra("songPath", model.getSongPath())
        startService(intent)
    }


    fun SongPlay(): Unit {

        try {
            if (exoPlayer == null) {
                return
            }

            val intent: Intent
            try {
                if (!SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle())
                    SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle()

            } catch (e: Exception) {
                e.printStackTrace()
            }

            exoPlayer?.playWhenReady = true

            activityMainBinding.imvPlayrPause.setImageResource(R.drawable.ic_pause_black_24dp)

            if (!SongsInfoList.isEmpty()) {

                intent = Intent(this@MainActivity.applicationContext, Mp3PlayerService::class.java)
                intent.putExtra("songTitle", SongsInfoList.get(currentSongIndex).getSongName())
                intent.putExtra("movieName", SongsInfoList.get(currentSongIndex).getSongMoviename())
                intent.putExtra("songPath", SongsInfoList.get(currentSongIndex).getSongImgPath())
                this@MainActivity.startService(intent)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun SongPause(): Unit {
        try {
            val intent: Intent

            if (exoPlayer?.playWhenReady!!) {
                exoPlayer?.playWhenReady = false
                try {
                    if (!SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle())
                        SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                activityMainBinding.imvPlayrPause.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                if (!SongsInfoList.isEmpty()) {
                    intent = Intent(this@MainActivity.applicationContext, Mp3PlayerService::class.java)
                    intent.putExtra("songTitle", SongsInfoList.get(currentSongIndex).getSongName())
                    intent.putExtra("movieName", SongsInfoList.get(currentSongIndex).getSongMoviename())
                    intent.putExtra("songPath", SongsInfoList.get(currentSongIndex).getSongImgPath())
                    this@MainActivity.startService(intent)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        MainActivity.mHandler.removeCallbacks(mUpdateTimeTask)

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        try {

            mHandler.removeCallbacks(mUpdateTimeTask)
            var totalDuration = MainActivity.exoPlayer?.duration!!.toInt()
            var currentPosition = utils?.progressToTimer(seekBar?.progress!!, totalDuration)!!

            // forward or backward to certain seconds
            MainActivity.exoPlayer!!.seekTo(currentPosition.toLong())
            // update timer progress again
            MainActivity.updateProgressBar()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onStart() {
        EventBus.getDefault().register(this)
        super.onStart()
    }

    @Subscribe
    fun onEvent(status: String) {
        when (status) {
//            PlaybackStatus.LOADING -> {
//            }
//            PlaybackStatus.ERROR -> Toast.makeText(this, R.string.no_stream, Toast.LENGTH_SHORT).show()
        }
//        trigger.setImageResource(if (status == PlaybackStatus.PLAYING) R.drawable.ic_pause_black else R.drawable.ic_play_arrow_black)
    }

    @SuppressLint("WrongConstant")
    override fun onBackPressed() {

        if (activityMainBinding.regFirstDrawerLayout.isDrawerOpen(activityMainBinding.regRightSlidingFrameLayout)) {
            activityMainBinding.regFirstDrawerLayout.closeDrawer(activityMainBinding.regRightSlidingFrameLayout)
        } else if (exoPlayer != null) run {
            val i = Intent()
            i.action = "android.intent.action.MAIN"
            i.addCategory("android.intent.category.HOME")
            startActivity(i)
        } else {
            super.onBackPressed()
        }

        try {
            if (exoPlayer != null || exoPlayer?.playWhenReady!!) {

                activityMainBinding.layoutSongplay.visibility = View.GONE
                activityMainBinding.layoutSongplay.visibility = View.VISIBLE
            } else if (exoPlayer == null || !exoPlayer?.playWhenReady!!) {
                (this.applicationContext?.getSystemService("notification") as NotificationManager).cancelAll()
                stopService(Intent(applicationContext, Mp3PlayerService::class.java))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = getMenuInflater()
        getMenuInflater().inflate(R.menu.menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.getItemId()

        if (id == R.id.menuFmRadio) {
            startActivity(Intent(this@MainActivity, OnlineRadioActivity::class.java))
            return true
        }

        if (id == R.id.menuAppShare) {
            val i: Intent = Intent()
            i.setAction(Intent.ACTION_SEND)
            i.putExtra(Intent.EXTRA_TEXT, "I suggest this app for best Music Player : https://play.google.com/store/apps/details?id=" + context!!.packageName)
            i.setType("text/plain")
            startActivity(i)
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onSaveInstanceState(oldInstanceState: Bundle) {
        super.onSaveInstanceState(oldInstanceState)
        oldInstanceState.clear()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @SuppressLint("WrongConstant")
    override fun onDestroy() {
        (this.applicationContext?.getSystemService("notification") as NotificationManager).cancelAll()
        stopService(Intent(applicationContext, Mp3PlayerService::class.java))
        super.onDestroy()
    }

    fun initTotalsongTime() {
        songTotalDurationLabel = findViewById(R.id.tvTotalTime)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun update(o: Observable?, arg: Any?) {
        if (arg is View) {
            when (arg.id) {
                R.id.imvForward -> songForward()

                R.id.imvPlayrPause -> PlayorPause()

                R.id.imvBackward -> songBackward()

                R.id.layoutSongplay -> {
                    startActivity(Intent(this@MainActivity, SongPlayerActivity::class.java))
                }

                R.id.searchView -> {
                    val bundle = Bundle()
                    bundle.putInt("flag", activeTabPos)
                    bundle.putSerializable("artist", SongsInfoList)
                    activityMainBinding.regFirstDrawerLayout.openDrawer(activityMainBinding.regRightSlidingFrameLayout)
                    //clear backs tack when navigating from slide menu
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    mFragmentManager = supportFragmentManager
                    mFragmentTransaction = mFragmentManager?.beginTransaction()
                    val rightMenu_Fragment = CommonSearchFragment()
                    rightMenu_Fragment.setArguments(bundle)
                    mFragmentTransaction?.replace(R.id.reg_right_sliding_frameLayout, rightMenu_Fragment)
                    mFragmentTransaction?.addToBackStack(null)
                    mFragmentTransaction?.commit()
                }

            }
        }
    }

    /**
     * / ** flag  = 0 nothing selected
     * flag = 1 selected
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onItemSelect(flag: Int, key: String?, value: String?) {

            if (activityMainBinding.regFirstDrawerLayout.isDrawerOpen(activityMainBinding.regRightSlidingFrameLayout))
                activityMainBinding.regFirstDrawerLayout.closeDrawer(activityMainBinding.regRightSlidingFrameLayout)

        try {
            lifecycleScope.launch {
                delay(40)
                SongPlay(flag)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

//    fun StringUtilitys(string1: String): String {
//
//        var string: String = string1
//        val PREF_APPID: String = "radio"
//        val BLANK_MESSAGE: String = ""
//        var REMOVE_LIST = arrayOf("www.songs.pk", "www.", ".com", ".pl", ".pk", ".org", ".co.in", "~requested~", "(musictub)", ".mp3", EnvironmentCompat.MEDIA_UNKNOWN, "()", "\\[\\]")
//        var REPLACE_LIST: Array<Array<String>>? = null
//        var r0 = arrayOfNulls<Array<String>>(6)
//        r0[0] = arrayOf<String>("247", "24x7")
//        r0[1] = arrayOf<String>(PREF_APPID, "radio")
//        r0[2] = arrayOf<String>(" ", " ")
//        r0[3] = arrayOf<String>("- -", "-")
//        r0[4] = arrayOf<String>(" ", " ")
//        r0[5] = arrayOf<String>("[^a-zA-Z0-9 ()\\[\\],-]", BLANK_MESSAGE)
//        REPLACE_LIST = r0.filterNotNull().toTypedArray()
//
//        if (string == null) {
//            return string
//        }
//
//        string = string.toLowerCase(Locale.getDefault())
//        for (removeString in REMOVE_LIST) {
//            string = string.replace(removeString, BLANK_MESSAGE)
//        }
//        for (replaceString in REPLACE_LIST!!) {
//            string = string.replace(replaceString[0], replaceString[1])
//        }
////        return toTitleCase(
//        return string.trim()
////        )
//    }
//
//
//    private fun toTitleCase(input: String): String {
//        val titleCase = StringBuilder()
//        var nextTitleCase = true
//        for (c in input.toCharArray()) {
//            var c2: Char
//            if (Character.isSpaceChar(c2) || c2.toString() == "(" || "[" == c2.toString()) {
//                nextTitleCase = true
//            } else if (nextTitleCase) {
//                c2 = Character.toTitleCase(c2)
//                nextTitleCase = false
//            }
//            titleCase.append(c2)
//        }
//        return titleCase.toString()
//    }


}
