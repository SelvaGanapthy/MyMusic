package com.trickyandroid.playmusic.view.activitys

import android.annotation.SuppressLint
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import com.trickyandroid.playmusic.R
import com.trickyandroid.playmusic.view.adapters.FmSwipeAdapter
import android.net.ConnectivityManager
import android.view.View
import com.trickyandroid.playmusic.app.AppController
import com.trickyandroid.playmusic.utils.BlurBuilder

import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.trickyandroid.playmusic.models.SongInfoModel
import com.trickyandroid.playmusic.utils.CustomProgressBar


class OnlineRadioActivity : AppCompatActivity() {

    var ivRadioStop: ImageView? = null

    var layoutSongplay: LinearLayout? = null
    var imvSongImage: ImageView? = null
    var fmModel: SongInfoModel? = null
    var tvMovieName: TextView? = null
    var tvSongName: TextView? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        var imvPlayrPause: ImageView? = null

        val radioName = Array<String>(13) { "" }

        val radioImage = arrayOf(
                "https://pbs.twimg.com/profile_images/1084655957735555072/rN1VErvx.jpg",
                "data:image/png;base64,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",
                "https://www.mwallpapers.com/photos/celebrities/ilayaraja/ilayaraja-best-hd-photos-1080p-fenuhl.png?v=1576487045",
                "https://www.logolynx.com/images/logolynx/fd/fd208ae54197cb7d7e22b9c0015e6be5.png",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTD4WWgoVab5CveTN0dbWuZ5gF095bqbQoPng&usqp=CAU",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSiIu8NNxCS7PASj-Gfxb7D4Avqo3EhKcY7bg&usqp=CAU",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTlzWmE_RkQdOVgMWRilTxe5l3PNY9IV1I38Q&usqp=CAU",
                "https://i0.wp.com/www.wordzz.com/wp-content/uploads/2017/05/Lord-Shiva-with-Parvati-Ganesha-Kartik.jpg?w=540&ssl=1",
                "https://onlineradiofm.in/assets/image/radio/180/tamil-kuji.png",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS119m1X15Namn9EdLfhkiG9z8u1QERmlxpig&usqp=CAU",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ4I3CnHzaDd_LjjZn_v2wuatOp3Y4LgvNteQ&usqp=CAU",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQEnUaIfsTzq5IOhfO_8DXAsK9k4ZfC3Na3sw&usqp=CAU",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSSfqPGCX7x1YyXL7sBtMiqRo7OVPL1Ij8Q0Q&usqp=CAU"
        )

        var streamName = Array<String>(13) { "" }
        var customProgressBar: CustomProgressBar? = null
        var relativeFm: RelativeLayout? = null
        var rvRadio: RecyclerView? = null
        var tvFmName: TextView? = null
        var radioList: ArrayList<SongInfoModel> = ArrayList()
        var context: Context? = null
        var fmSwipeAdapter: FmSwipeAdapter? = null
        fun set_adapter() {
            fmSwipeAdapter = FmSwipeAdapter(context!!, radioList)
            rvRadio?.setAdapter(fmSwipeAdapter)
        }


        fun isOnline(context: Context?): Boolean {
            var result = false
            if (context != null) {
                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if (cm != null) {
                    val networkInfo = cm.activeNetworkInfo
                    if (networkInfo != null) {
                        result = networkInfo.isConnected
                    }
                }
            }
            return result
        }


        @SuppressLint("NewApi")
        fun initializeMediaPlayer(id: Int) {
            customProgressBar?.show()
            customProgressBar?.setCancelable(false)
            var array = context?.resources!!.obtainTypedArray(R.array.fmimages)
            val d = array.getDrawable(id)
            val bitmap = (d as BitmapDrawable).bitmap
            var bler: BlurBuilder = BlurBuilder()
            relativeFm?.background = BitmapDrawable(bler.blur(context!!, bitmap))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_radio)
        context = this@OnlineRadioActivity
        AppController.onlineRadioActivity = this
        customProgressBar = CustomProgressBar(this@OnlineRadioActivity)
        relativeFm = findViewById<RelativeLayout>(R.id.relativeFm) as RelativeLayout
        layoutSongplay = findViewById<LinearLayout>(R.id.layoutSongplay) as LinearLayout
        tvFmName = findViewById<View>(R.id.tvFmName) as TextView
        rvRadio = findViewById<View>(R.id.rvRadio) as RecyclerView
        ivRadioStop = findViewById<View>(R.id.ivRadioStop) as ImageView
        imvPlayrPause = findViewById<View>(R.id.imvPlayrPause) as ImageView
        imvSongImage = findViewById<View>(R.id.imvSongImage) as ImageView
        tvMovieName = findViewById<View>(R.id.tvMovieName) as TextView
        tvSongName = findViewById<View>(R.id.tvSongName) as TextView
        imvPlayrPause?.setOnClickListener {
            if (MainActivity.exoPlayer != null) {
                AppController.mainActivity?.PlayorPause()
                imvPlayrPause?.setImageResource(R.drawable.ic_pause_black_24dp)
                if (!MainActivity.exoPlayer?.playWhenReady!!)
                    imvPlayrPause?.setImageResource(R.drawable.ic_play_arrow_black_24dp)
            }
        }

//        streamName[0] = "https://newsonair.gov.in/writereaddata/Bulletins_Audio/Regional/2021/Jan/Regional-Chennai-Tamil-0645-202111085238.mp3"
//        streamName[1] = "http://node-09.zeno.fm/7a3d4s28ptzuv?rj-ttl=5&rj-tok=AAABdlBws04AXCvcxDdLNpKJ4g"
        streamName[0] = "http://node-07.zeno.fm/tuvpz168ptzuv?rj-ttl=5&rj-tok=AAABdxEqlFQAws8NF-hY5g4gJw"
//        streamName[3] = "https://radioindia.net/radio/air_rainbow_chennai/icecast.audio"
        streamName[1] = "http://node-22.zeno.fm/gp5k6qs1bg0uv?rj-ttl=5&rj-tok=AAABdlFuo2YAcaAGEhkob_MowQ"
//        streamName[5] = "https://radioindia.net/radio/air_tamil_radio/icecast.audio"
        streamName[2] = "http://176.31.120.92:17266/;"
        streamName[3] = "http://195.154.217.103:8175"
        streamName[4] = "http://167.114.131.90:5750"
        streamName[5] = "http://prclive1.listenon.in:9948"
        streamName[6] = "http://19233.live.streamtheworld.com/OMRADIO_S01AAC_SC"
        streamName[7] = "http://listen.shoutcast.com/bakthi"
        streamName[8] = "http://live.tamilkuyilradio.com:8095"
        streamName[9]= "http://192.99.170.8:5756"
        streamName[10]=  "http://s5.voscast.com:7736"
        streamName[11]="http://listen.radionomy.com:80/80s90ssuperpophits"
        streamName[12]= "http://15363.live.streamtheworld.com/KABCAM_SC"

        radioName[0] = "Big Fm 92.7"
        radioName[1] = "Tamil Fm 89.4"
        radioName[2] = "Illaya Raja"
        radioName[3] = "A9RADIO"
        radioName[4] = "vMusic Latest Hits"
        radioName[5] = "RadioCity Tamil"
        radioName[6] = "Devotional Hindu OM Radio"
        radioName[7] = "Devotional Hindu Bakthi Radio"
        radioName[8] ="Tamil Kuyil"
        radioName[9] ="Tamil Abinayam"
        radioName[10] ="English FreeTalk Live"
        radioName[11] =  "Super PopHits"
        radioName[12] ="English Hits"

        if (MainActivity.isFmPlay && radioList.isNullOrEmpty())
            radioList = MainActivity.SongsInfoList
        else if (radioList.isNullOrEmpty()) {
            for (i in 0 until 13) {
                val sm = SongInfoModel()
                sm.setSongPath(streamName[i])
                sm.setSongName(radioName[i])
                sm.setSongMoviename("Talk|Music")
                sm.setSongImgPath(radioImage[i])
                sm.setId(i)
                radioList.add(i, sm)
            }
        }

        set_adapter()


        if (MainActivity.isFmPlay) {
            layoutSongplay?.visibility = View.VISIBLE
            Glide.with(this@OnlineRadioActivity)
                    .load(MainActivity.curFmModel?.getSongImgPath())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(imvSongImage!!)
            tvMovieName?.text = MainActivity.curFmModel?.getSongName()
            tvSongName?.text = MainActivity.curFmModel?.getSongMoviename()
            if (MainActivity.exoPlayer != null && !MainActivity.exoPlayer?.playWhenReady!!) {
                imvPlayrPause?.setImageResource(R.drawable.ic_play_arrow_black_24dp)
            }
        }
    }


    fun startRadio(model: SongInfoModel) {
        if (isOnline(this@OnlineRadioActivity)) {
            setRadioDetails(model)
            AppController.mainActivity?.playRadio(model)
            layoutSongplay?.visibility = View.VISIBLE

        } else {
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show()
        }
    }

    fun setRadioDetails(model: SongInfoModel) {
        Glide.with(this@OnlineRadioActivity)
                .load(model.getSongImgPath())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(imvSongImage!!)
        tvMovieName?.text = model.getSongName()
        tvSongName?.text = model.getSongMoviename()
    }

    fun goBack(v: View): Unit = onBackPressed()

    override fun onBackPressed() {
        if (MainActivity.isFmPlay) {
            val i = Intent(this, MainActivity::class.java)
            var taskStackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)
            taskStackBuilder.addParentStack(MainActivity::class.java)
            taskStackBuilder.addNextIntent(i)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(i)
        } else
            super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
    }

}
