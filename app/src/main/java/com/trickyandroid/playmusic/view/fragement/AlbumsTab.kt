package com.trickyandroid.playmusic.view.fragement

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.trickyandroid.playmusic.R
import com.trickyandroid.playmusic.app.AppController
import com.trickyandroid.playmusic.models.SongInfoModel
import com.trickyandroid.playmusic.view.activitys.AlbumSongActivity
import com.trickyandroid.playmusic.view.adapters.AlbumsAdapter
import com.trickyandroid.playmusic.view.interfaces.IFragmentListener
import com.trickyandroid.playmusic.view.interfaces.ISearch

class AlbumsTab() : Fragment(), ISearch {

    var vi: View? = null
    var rv: RecyclerView? = null
    var AlbumsList: ArrayList<SongInfoModel> = ArrayList()
    var movieNameList: ArrayList<String> = ArrayList()
    var movieList: ArrayList<SongInfoModel> = ArrayList()
    var swipeRefreshHotcases: SwipeRefreshLayout? = null
    var adapter: AlbumsAdapter? = null

    /*Search View*/
    var mIFragmentListener: IFragmentListener? = null
    var mSearchTerm: String? = null

    companion object {

        val ARG_SEARCHTERM: String = "search_term"

        fun newInstances(searchTerm: String): TracksTab {
            var fragement: TracksTab = TracksTab()
            var bundle: Bundle = Bundle()
            bundle.putString(TracksTab.ARG_SEARCHTERM, searchTerm)
            fragement.arguments = bundle
            return fragement
        }


        private fun format(s: Long): String {
            return if (s < 10)
                "0$s"
            else
                "" + s
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        AppController.albumsTab = this;
        vi = inflater.inflate(R.layout.layout_recyclerview, container, false)
        rv = vi?.findViewById<View>(R.id.rv) as RecyclerView
        swipeRefreshHotcases = vi?.findViewById<View>(R.id.swiperRefresh) as SwipeRefreshLayout
        AlbumsList = arguments?.getSerializable("SongsInfoList") as ArrayList<SongInfoModel>
        swipeRefreshHotcases?.setColorSchemeResources(R.color.swipe1, R.color.swipe2, R.color.swipe3)
        swipeRefreshHotcases?.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    swipeRefreshHotcases?.setRefreshing(false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 500)
        }




        try {
            activity?.runOnUiThread(object : Runnable {
                override fun run() {
                    for (i in 0 until AlbumsList.size) {
                        movieNameList.add(AlbumsList.get(i).getSongMoviename())
                    }

                    //Set Title has Movie Names
                    for (i in 0 until movieNameList.size) {
                        for (j in (i + 1) until movieNameList.size) {
                            if (movieNameList.get(i).equals(movieNameList.get(j))) {
                                movieNameList.set(j, "*@#")
                            }
                        }
                    }

                    for (i in 0 until movieNameList.size) {
                        if (!movieNameList.get(i).equals("*@#")) {
                            var model: SongInfoModel = SongInfoModel()
                            model.setSongMoviename(movieNameList.get(i));
                            model.setSongImgPath(AlbumsList.get(i).getSongImgPath())
                            model.setSongComposer(AlbumsList.get(i).getSongComposer())
                            movieList.add(model)
                        }
                    }


                }
            })

            // Filter the MovieName
            // copied movies name from original list

            set_animator()
            set_layout_manager()
            set_adapter()


        } catch (e: Exception) {
            e.printStackTrace()
        }

        return vi!!
    }


    private fun set_animator(): Unit {
        var animator: DefaultItemAnimator = DefaultItemAnimator()
        animator.changeDuration = 1000
        rv?.itemAnimator = animator
    }

    private fun set_layout_manager(): Unit {
        try {
            rv?.layoutManager = LinearLayoutManager(getContext())
            rv?.setHasFixedSize(true)
//            rv?.setItemViewCacheSize(20)
//            rv?.setDrawingCacheEnabled(true)
//            rv?.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun set_adapter(): Unit {
        try {
            adapter = AlbumsAdapter(requireContext(), movieList)
            rv?.adapter = this.adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIFragmentListener = context as (IFragmentListener);
        mIFragmentListener?.addiSearch(this@AlbumsTab as ISearch);
    }

    override fun onDetach() {
        super.onDetach()
        if (null != mIFragmentListener) {
            mIFragmentListener?.removeISearch(this@AlbumsTab as ISearch)
        }
    }

    override fun onResume() {
        super.onResume()
        if (null != mSearchTerm) {
            onTextQuery(mSearchTerm!!)
        }
    }

    override fun onPause() {
        super.onPause()
    }


    override fun onTextQuery(text: String) {
        adapter?.getFilter()?.filter(text)
        rv?.recycledViewPool?.clear()
        rv?.isNestedScrollingEnabled = false
        adapter?.notifyDataSetChanged()
    }


    fun loadMovieSongList(movieName: String): Unit {

        var movieSongList: ArrayList<SongInfoModel> = ArrayList()
        var tm: Long = 0
        try {
               movieSongList.clear()
            for (i in 0 until this.AlbumsList.size) {
                if (movieName == AlbumsList[i].getSongMoviename()) {
                    var models: SongInfoModel = AlbumsList[i]
                    val arr = models.getSongTime().split(":")
                    tm += Integer.parseInt(arr[1].replace(" ", "")).toLong()
                    tm += (60 * Integer.parseInt(arr[0].replace(" ", ""))).toLong()

                    movieSongList.add(models)
                }
            }

            val mm = tm / 60
            tm %= 60
            val ss = tm

            if (movieSongList.size > 0) {
                val i: Intent = Intent(activity, AlbumSongActivity::class.java)
                i.putExtra("MovieSongList", movieSongList)
                i.putExtra("TotalTime", format(mm) + ":" + format(ss))
                startActivity(i)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSaveInstanceState(oldInstanceState: Bundle) {
        super.onSaveInstanceState(oldInstanceState)
        oldInstanceState.clear()
    }
}