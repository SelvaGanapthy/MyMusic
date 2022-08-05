package com.trickyandroid.playmusic.view.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.trickyandroid.playmusic.R
import com.trickyandroid.playmusic.app.AppController
import com.trickyandroid.playmusic.models.SongInfoModel

class FmSwipeAdapter(var context: Context, var dataList: ArrayList<SongInfoModel>) : RecyclerView.Adapter<FmSwipeAdapter.ViewHolder>() {

    private var view: View? = null
    private var filterList: ArrayList<SongInfoModel> = dataList

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        view = LayoutInflater.from(context).inflate(R.layout.songs_adapter, parent, false)
        return ViewHolder(view!!)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        if (!dataList.isEmpty()) {
            val model: SongInfoModel = filterList.get(p1)
            p0.tvMovieName?.text=model.getSongName()
            p0.tvArtist?.text=model.getSongMoviename()
            Glide.with(context).load(model.getSongImgPath())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(p0.ivSong!!)
            p0.cardView?.setOnClickListener{
                    AppController.onlineRadioActivity?.startRadio(model)
                }

        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvArtist: TextView? = null
        var tvMovieName: TextView? = null
        var cardView: CardView? = null
        var ivSong: ImageView? = null
        var lineCode: View? = null
        var tvSongTime: TextView? = null

        init {
            tvMovieName = itemView.findViewById<View>(R.id.tvMainName) as TextView
            tvArtist = itemView.findViewById<View>(R.id.tvSubName) as TextView
            tvSongTime = itemView.findViewById<View>(R.id.tvSongTime) as TextView
            tvSongTime?.visibility = View.GONE
            cardView = itemView.findViewById<View>(R.id.cardView) as CardView
            cardView?.setBackgroundResource(R.drawable.card_bg1)
            ivSong = itemView.findViewById<View>(R.id.ivSong) as ImageView
            lineCode = itemView.findViewById<View>(R.id.lineCode) as View
            lineCode?.setBackgroundColor(Color.TRANSPARENT)
        }
    }

}