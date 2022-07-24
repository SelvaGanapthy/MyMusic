package com.trickyandroid.playmusic.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.trickyandroid.playmusic.R
import com.trickyandroid.playmusic.view.activitys.MainActivity
import com.trickyandroid.playmusic.anim.AnimationUtils
import com.trickyandroid.playmusic.app.AppController
import com.trickyandroid.playmusic.models.SongInfoModel
import java.lang.Exception
import java.util.ArrayList

class ArtistsAdapter(var context: Context, var dataList: ArrayList<SongInfoModel>) : RecyclerView.Adapter<ArtistsAdapter.ViewHolder>() {

    var view: View? = null
    var filterList: ArrayList<SongInfoModel> = dataList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(context).inflate(R.layout.songs_adapter, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        if (dataList.isEmpty()) {

            holder.tvArtistName?.text="\t\t\t\t\t\t\t\t    No Data Found!"
            holder.tvSongName?.visibility = View.GONE
            holder.tvSongTime?.visibility = View.GONE
            holder.ivSong?.visibility = View.GONE


        } else {


            try {

                holder.tvSongName?.visibility = View.VISIBLE
                holder.tvSongTime?.visibility = View.VISIBLE
                holder.ivSong?.visibility = View.VISIBLE

                val model: SongInfoModel = filterList[position]

                if (model.getSongPath().isNotEmpty()) {
//                    Picasso.get().load(Uri.parse("file://" + model.getSongImgPath()))
//                            .error(R.drawable.default_album_bg).into(holder.ivSong)

                    Glide.with(context)
//                            .load(Uri.parse("file://" + model.getSongImgPath()))
                            .load(Uri.parse(model.getSongImgPath()))
                            .error(R.drawable.default_album_bg)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .into(holder.ivSong!!)


                } else {
                    holder.ivSong?.setImageResource(R.drawable.default_album_bg)
                }

                holder.tvSongName?.text = model.getSongName()
                holder.tvSongTime?.text = model.getSongTime()
                holder.tvArtistName?.text = model.getSongComposer()

                holder.cardView?.setOnClickListener {
                    MainActivity.SongsInfoList = dataList
                    AppController.mainActivity?.SongPlay(model.getArtistId())
                }

                AnimationUtils.animateSunblind(holder, true)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

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
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()

                if (charString.isEmpty()) {

                    filterList = dataList
                } else {

                    val filteredList1 = ArrayList<SongInfoModel>()

                    for (dataModel in dataList) {

                        if (dataModel.getSongName().toLowerCase().contains(charString) || dataModel.getSongComposer().toLowerCase().contains(charString)) {

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

        var tvSongName: TextView? = null
        var tvArtistName: TextView? = null
        var tvSongTime: TextView? = null
        var cardView: CardView? = null
        var ivSong: ImageView? = null
        var lineCode: View? = null

        init {
            tvArtistName = itemView?.findViewById<View>(R.id.tvMainName) as TextView
            tvSongName = itemView.findViewById<View>(R.id.tvSubName) as TextView
            tvSongTime = itemView.findViewById<View>(R.id.tvSongTime) as TextView
            cardView = itemView.findViewById<View>(R.id.cardView) as CardView
            cardView?.setBackgroundResource(R.drawable.card_bg1)
            ivSong = itemView.findViewById<View>(R.id.ivSong) as ImageView
            lineCode = itemView.findViewById<View>(R.id.lineCode) as View
            lineCode?.setBackgroundColor(Color.TRANSPARENT)

        }

    }
}