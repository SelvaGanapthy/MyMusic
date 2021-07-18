package com.trickyandroid.playmusic.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.trickyandroid.playmusic.R
import com.trickyandroid.playmusic.view.activitys.MainActivity
import com.trickyandroid.playmusic.anim.AnimationUtils
import com.trickyandroid.playmusic.app.AppController
import com.trickyandroid.playmusic.models.SongInfoModel
import java.util.ArrayList

class TracksAdapter(var context: Context, var dataList: ArrayList<SongInfoModel>) : RecyclerView.Adapter<TracksAdapter.ViewHolder>() {
    var view: View? = null
    var filterList: ArrayList<SongInfoModel> = dataList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(context).inflate(R.layout.songs_adapter, parent, false);
        return ViewHolder(view)
    }

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        if (dataList.isEmpty()) {
            holder.tvSongName?.setText("\t\t\t\t\t\t\t\t    No Data Found!")
            holder.tvArtistName?.visibility = View.GONE
            holder.tvSongTime?.visibility = View.GONE
            holder.ivSong?.visibility = View.GONE

        } else {
            try {

                holder.tvArtistName?.visibility = View.VISIBLE
                holder.tvSongTime?.visibility = View.VISIBLE
                holder.ivSong?.visibility = View.VISIBLE

                val model: SongInfoModel = filterList.get(position)
                holder.ivSong?.setImageResource(R.drawable.default_album_bg)

                /*  val art: ByteArray? = MainActivity.getAlbumArt(model.getSongPath())
                  if (art != null) {
                      Log.i("art", art.toString())
                      try {
  //                    Picasso.get().load(Uri.parse("file://" + model.getSongImgPath()))
  //                            .error(R.drawable.default_album_bg).into(holder.ivSong)

                          Glide.with(context)
                                  .load(art)
                                  .error(R.drawable.default_album_bg)
                                  .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                  .into(holder?.ivSong!!)


                      } catch (e: Exception) {
                          e.printStackTrace()
                      }

                  } else {
                      holder.ivSong?.setImageResource(R.drawable.default_album_bg)
                  }*/

                holder.tvSongName?.text = model.getSongName()
                holder.tvArtistName?.text = model.getSongComposer()
                holder.tvSongTime?.text = model.getSongTime()

//                holder.cardView?.setOnLongClickListener {
//                    var contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, filterList[position].getSongFileId().toLong())
//
//                    val file = File(filterList.get(position).getSongImgPath())
//                    val deleted: Boolean = file.delete()
//
//                    if (deleted) {
//
//                        filterList.remove(position)
//                        notifyItemRemoved(position)
//                        notifyItemRangeChanged(position ,filterList.size)
//                    }
//                }

                holder.cardView?.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        MainActivity.SongsInfoList = dataList
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            AppController.mainActivity?.SongPlay(model.getTrackId())
                        }
                    }
                })

                holder.cardView?.setOnLongClickListener(object : View.OnLongClickListener {
                    override fun onLongClick(v: View?): Boolean {
                        if (MainActivity.songFileDeletePermanent(filterList[position].getId())) {
                            filterList.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, filterList.size)
                            Toast.makeText(context,"Song Permanently deleted",Toast.LENGTH_SHORT).show()
                        }
                        return true
                    }
                })

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


    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var tvSongName: TextView? = null
        var tvArtistName: TextView? = null
        var tvSongTime: TextView? = null
        var cardView: CardView? = null
        var ivSong: ImageView? = null
        var lineCode: View? = null

        init {
            tvSongName = itemView?.findViewById<View>(R.id.tvMainName) as TextView
            tvArtistName = itemView.findViewById<View>(R.id.tvSubName) as TextView
            tvSongTime = itemView.findViewById<View>(R.id.tvSongTime) as TextView
            cardView = itemView.findViewById<View>(R.id.cardView) as CardView
            cardView?.setBackgroundResource(R.drawable.card_bg1)
            ivSong = itemView.findViewById<View>(R.id.ivSong) as ImageView
            lineCode = itemView.findViewById<View>(R.id.lineCode) as View
            lineCode?.setBackgroundColor(Color.TRANSPARENT)
        }
    }


//    fun getAlbumArt(uri: String): ByteArray? {
//        val retriever: MediaMetadataRetriever = MediaMetadataRetriever()
//        retriever.setDataSource(uri)
//        val art: ByteArray? = retriever.embeddedPicture
//        retriever.release()
//        return art
//    }
}