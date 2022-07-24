package com.trickyandroid.playmusic.view.adapters

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.trickyandroid.playmusic.R
import com.trickyandroid.playmusic.anim.AnimationUtils
import com.trickyandroid.playmusic.app.AppController
import com.trickyandroid.playmusic.models.SongInfoModel

class AlbumsAdapter(var context: Context, var dataList: ArrayList<SongInfoModel>) : RecyclerView.Adapter<AlbumsAdapter.ViewHolder>() {


    var view: View? = null
    var filterList: ArrayList<SongInfoModel> = dataList


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(context).inflate(R.layout.songs_adapter, parent, false)
        return ViewHolder(view)
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        if (dataList.isEmpty()) {
            holder.tvMovieName?.text="\t\t\t\t\t\t\t\t    No Data Found!"
            holder.tvArtist?.visibility = View.GONE
            holder.tvSongTime?.visibility = View.GONE
            holder.ivSong?.visibility = View.GONE

        } else {

            try {

                holder.tvSongTime?.visibility = View.VISIBLE
                holder.ivSong?.visibility = View.VISIBLE
                holder.tvArtist?.visibility = View.VISIBLE

                val model: SongInfoModel = filterList[position]
                holder.tvSongTime?.visibility = View.GONE
                if (model.getSongImgPath().isNotEmpty()) {
                  Glide.with(context)
                            .load(Uri.parse(model.getSongImgPath()))
                            .error(R.drawable.default_album_bg)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .into(holder.ivSong!!)

                } else {
                    holder.ivSong?.setImageResource(R.drawable.default_album_bg)
                }


                holder.cardView?.setOnClickListener {
                    AppController.albumsTab?.loadMovieSongList(model.getSongMoviename())
                }

                holder.tvMovieName?.text = model.getSongMoviename()
                holder.tvArtist?.text = model.getSongComposer()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        AnimationUtils.animateSunblind(holder, true)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }


    override fun getItemCount(): Int = if (dataList.size == 0) 1 else filterList.size


    fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()

                if (charString.isEmpty()) {

                    filterList = dataList
                } else {

                    val filteredList1 = java.util.ArrayList<SongInfoModel>()

                    for (dataModel in dataList) {

                        if (dataModel.getSongMoviename().toLowerCase().contains(charString)) {
                            filteredList1.add(dataModel)
                        }
                    }

                    filterList = filteredList1
                }

                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filterList = filterResults.values as ArrayList<SongInfoModel>
                notifyDataSetChanged()
            }
        }
    }


    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var tvArtist: TextView? = null
        var tvMovieName: TextView? = null
        var cardView: CardView? = null
        var ivSong: ImageView? = null
        var lineCode: View? = null
        var tvSongTime: TextView? = null

        init {
            tvMovieName = itemView?.findViewById<View>(R.id.tvMainName) as TextView
            tvArtist = itemView.findViewById<View>(R.id.tvSubName) as TextView
            tvSongTime = itemView.findViewById<View>(R.id.tvSongTime) as TextView
            cardView = itemView.findViewById<View>(R.id.cardView) as CardView
            cardView?.setBackgroundResource(R.drawable.card_bg1)
            ivSong = itemView.findViewById<View>(R.id.ivSong) as ImageView
            lineCode = itemView.findViewById<View>(R.id.lineCode) as View
            lineCode?.setBackgroundColor(Color.TRANSPARENT)

        }

    }

}