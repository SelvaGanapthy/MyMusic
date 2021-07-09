package com.trickyandroid.playmusic.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trickyandroid.playmusic.R
import com.trickyandroid.playmusic.models.SongInfoModel
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.trickyandroid.playmusic.view.activitys.MainActivity
import com.trickyandroid.playmusic.anim.AnimationUtils
import com.trickyandroid.playmusic.app.AppController


class SongsAdapter(var context: Context, var dataList: ArrayList<SongInfoModel>) : RecyclerView.Adapter<SongsAdapter.ViewHolder>() {


    var view: View? = null
    var filterList: ArrayList<SongInfoModel> = dataList


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(context).inflate(R.layout.songs_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val model: SongInfoModel = filterList[position]
            if (model.getSongImgPath() != null) {

                try {
//                    Picasso.get().load(Uri.parse("file://" + model.getSongImgPath()))
//                            .error(R.drawable.default_album_bg).into(holder.ivSong)

                    Glide.with(context)
                            .load(Uri.parse( model.getSongImgPath()))
                            .error(R.drawable.default_album_bg)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .into(holder?.ivSong!!)


                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                holder.ivSong?.setImageResource(R.drawable.default_album_bg)
            }

            holder.tvMainName?.text = model.getSongName()
            holder.tvSubName?.text = model.getSongComposer()
            holder.tvSongTime?.text = model.getSongTime()

            holder.cardView?.setOnClickListener(object : View.OnClickListener {
                @SuppressLint("NewApi")
                override fun onClick(p0: View?) {

                    MainActivity.SongsInfoList = AppController.albumsTab?.AlbumsList!!
                    AppController.mainActivity?.SongPlay(model.getAlbumnewId())
                }
            })

//            holder.tvArtistName?.text = model.getSongComposer()

            AnimationUtils.animateSunblind(holder, true)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }
    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()

                if (charString.isEmpty()) {

                    filterList = dataList
                } else {

                    val filteredList1 = java.util.ArrayList<SongInfoModel>()

                    for (dataModel in dataList) {

                        if (dataModel.getSongName().toLowerCase().contains(charString) || dataModel.getSongMoviename().toLowerCase().contains(charString)) {

                            filteredList1.add(dataModel)
                        }
                    }

                    filterList = filteredList1
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                filterList = filterResults.values as java.util.ArrayList<SongInfoModel>
                notifyDataSetChanged()
            }
        }
    }


    class ViewHolder(item: View?) : RecyclerView.ViewHolder(item!!) {

        var tvMainName: TextView? = null
        var tvSubName: TextView? = null
        var tvSongTime: TextView? = null
        var cardView: CardView? = null
        var ivSong: ImageView? = null
        var lineCode: View? = null

        init {
            ivSong = itemView.findViewById<View>(R.id.ivSong) as ImageView
            cardView = itemView.findViewById<View>(R.id.cardView) as CardView
            cardView?.setBackgroundResource(R.drawable.card_bg)
            lineCode = itemView.findViewById(R.id.lineCode) as View
            lineCode?.setBackgroundColor(Color.TRANSPARENT)
            tvMainName = itemView.findViewById<View>(R.id.tvMainName) as TextView
            tvSubName = itemView.findViewById<View>(R.id.tvSubName) as TextView
            tvSongTime = itemView.findViewById<View>(R.id.tvSongTime) as TextView

        }

    }

}