package com.trickyandroid.playmusic.view.activitys

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle

import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.trickyandroid.playmusic.R
import com.trickyandroid.playmusic.view.adapters.SongsAdapter
import com.trickyandroid.playmusic.app.AppController
import com.trickyandroid.playmusic.models.SongInfoModel

class AlbumSongActivity : AppCompatActivity(), View.OnClickListener {

    var rv: RecyclerView? = null
    var MovieSongList: ArrayList<SongInfoModel>? = ArrayList();
    var adapter: SongsAdapter? = null;
    var tvTotalSong: TextView? = null
    var tvArtistName: TextView? = null
    var tvMovieName: TextView? = null
    var collapsingToolbar: CollapsingToolbarLayout? = null;
    var toolbarImage: ImageView? = null
    var linearMovieImg: LinearLayout? = null
    var layoutSongplay: LinearLayout? = null
    var imvSongImage: ImageView? = null
    var tvMoviename: TextView? = null
    var tvSongName: TextView? = null
    var tvTotalTime: TextView? = null
    var tvAlbumTotTime: TextView? = null
    var imvPlayrPause: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_song)
        AppController.albumSongActivity = this;
        linearMovieImg = findViewById<View>(R.id.linearMovieImg) as LinearLayout
        MovieSongList = intent.getSerializableExtra("MovieSongList") as (ArrayList<SongInfoModel>)
        tvTotalSong = findViewById<View>(R.id.tvTotalSong) as TextView
        rv = findViewById<View>(R.id.rv) as RecyclerView
        tvArtistName = findViewById<View>(R.id.tvArtistName) as TextView
//        collapsingToolbar = findViewById<CollapsingToolbarLayout>(R.id.) as CollapsingToolbarLayout

        if (MovieSongList?.get(0)?.getSongImgPath() != null && MovieSongList?.get(0)?.getSongImgPath() != "null") {
            linearMovieImg?.background = BitmapDrawable(MovieSongList?.get(0)?.getSongImgPath())
        } else {
            var array = resources.obtainTypedArray(R.array.images)
            linearMovieImg?.background = array.getDrawable(0)
        }

        tvTotalSong?.text = MovieSongList?.size.toString() + " Songs"
        tvMovieName = findViewById<TextView>(R.id.tvMovieName) as TextView
        tvArtistName?.text = MovieSongList?.get(0)?.getSongArtist()
        tvMovieName?.text = MovieSongList?.get(0)?.getSongMoviename()

        layoutSongplay = findViewById<View>(R.id.layoutSongplay) as LinearLayout
        layoutSongplay?.visibility = View.GONE
        layoutSongplay?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                startActivity(Intent(this@AlbumSongActivity, SongPlayerActivity::class.java))
            }
        })
        imvSongImage = findViewById<View>(R.id.imvSongImage) as ImageView
        tvMoviename = findViewById<View>(R.id.tvMoviename) as TextView
        tvSongName = findViewById<View>(R.id.tvSongName) as TextView
        tvTotalTime = findViewById<View>(R.id.tvTotalTime) as TextView
        tvAlbumTotTime = findViewById<View>(R.id.tvAlbumTotTime) as TextView
        tvAlbumTotTime?.text = intent.getStringExtra("TotalTime")
        imvPlayrPause = findViewById<View>(R.id.imvPlayrPause) as ImageView


        set_layout_manager()
        set_adapter()
        songDetails(MainActivity.currentSongIndex)
    }


    private fun set_layout_manager(): Unit {
        try {
            rv?.setLayoutManager(LinearLayoutManager(this@AlbumSongActivity));
            rv?.setHasFixedSize(true)
            rv?.setItemViewCacheSize(20)
            rv?.setDrawingCacheEnabled(true)
            rv?.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun set_adapter(): Unit {
        try {
            adapter = SongsAdapter(this@AlbumSongActivity, MovieSongList!!)
            rv?.setAdapter(adapter);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onClick(p0: View?) {

        when (p0?.id) {

            R.id.imvPlayrPause -> {
                AppController.mainActivity?.PlayorPause()
            }
        }
    }

    fun songDetails(index: Int): Unit {
        try {

            if (MainActivity.exoPlayer?.playWhenReady!! && !MainActivity.isFmPlay) {
                layoutSongplay?.visibility = View.VISIBLE
                imvPlayrPause?.setImageResource(R.drawable.ic_pause_black_24dp)

            } else if (MainActivity.exoPlayer != null && (!MainActivity.exoPlayer?.playWhenReady!!)) {
                imvPlayrPause?.setImageResource(R.drawable.ic_play_arrow_black_24dp)
            } else {
                layoutSongplay?.visibility = View.GONE
            }

            if (MainActivity.SongsInfoList.get(index).getSongImgPath() != null) {
//                imvSongImage?.setImageURI(Uri.parse(MainActivity.SongsInfoList.get(index).getSongImgPath()))
//            } else {
                imvSongImage?.setImageResource(R.drawable.default_album_bg)
            }

            tvMoviename?.text = MainActivity.SongsInfoList.get(index).getSongMoviename()
            tvSongName?.text = MainActivity.SongsInfoList.get(index).getSongName()
            tvTotalTime?.text = MainActivity.SongsInfoList.get(index).getSongTime()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun goBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
    }

}
