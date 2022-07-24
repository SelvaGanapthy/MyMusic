package com.trickyandroid.playmusic.view.fragement

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
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

class AlbumsTab : Fragment(R.layout.layout_recyclerview), ISearch {

    private var rv: RecyclerView? = null
    var albumsList: ArrayList<SongInfoModel> = ArrayList()
    private var movieNameList: ArrayList<String> = ArrayList()
    private var movieList: ArrayList<SongInfoModel> = ArrayList()
    private var swipeRefreshHotcases: SwipeRefreshLayout? = null
    private var adapter: AlbumsAdapter? = null

    /*Search View*/
    private var mIFragmentListener: IFragmentListener? = null
    private var mSearchTerm: String? = null

    companion object {

        val ARG_SEARCHTERM: String = "search_term"

        fun newInstances(searchTerm: String): AlbumsTab {
            val fragement = AlbumsTab()
            val bundle = Bundle()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        AppController.albumsTab = this
        rv = view.findViewById<View>(R.id.rv) as RecyclerView
        swipeRefreshHotcases = view.findViewById<View>(R.id.swiperRefresh) as SwipeRefreshLayout
//            requireArguments().getSerializable("SongsInfoList")
        albumsList = arguments?.getSerializable("SongsInfoList") as ArrayList<SongInfoModel>
        swipeRefreshHotcases?.setColorSchemeResources(
            R.color.swipe1,
            R.color.swipe2,
            R.color.swipe3
        )
        swipeRefreshHotcases?.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    swipeRefreshHotcases?.isRefreshing = false
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 500)
        }

        try {
            activity?.runOnUiThread {
                for (i in 0 until albumsList.size) {
                    movieNameList.add(albumsList[i].getSongMoviename())
                }

                //Set Title has Movie Names
                for (i in 0 until movieNameList.size) {
                    for (j in (i + 1) until movieNameList.size) {
                        if (movieNameList[i] == movieNameList[j]) {
                            movieNameList[j] = "*@#"
                        }
                    }
                }

                for (i in 0 until movieNameList.size) {
                    if (movieNameList[i] != "*@#") {
                        val model = SongInfoModel()
                        model.setSongMoviename(movieNameList[i])
                        model.setSongImgPath(albumsList[i].getSongImgPath())
                        model.setSongComposer(albumsList[i].getSongComposer())
                        movieList.add(model)
                    }
                }
            }

           /*Filter the MovieName & Copied movies name from original list*/
            setAnimator()
            setLayoutManager()
            setAdapter()
            super.onViewCreated(view, savedInstanceState)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setAnimator() {
        val animator = DefaultItemAnimator()
        animator.changeDuration = 1000
        rv?.itemAnimator = animator
    }

    private fun setLayoutManager() {
        try {
            rv?.layoutManager = LinearLayoutManager(context)
            rv?.setHasFixedSize(true)
            rv?.setItemViewCacheSize(20)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAdapter() {
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

   internal fun loadMovieSongList(movieName: String) {

        val movieSongList: ArrayList<SongInfoModel> = ArrayList()
        var tm: Long = 0
        try {
            movieSongList.clear()
            for (i in 0 until this.albumsList.size) {
                if (movieName == albumsList[i].getSongMoviename()) {
                    val models: SongInfoModel = albumsList[i]
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
                val i = Intent(activity, AlbumSongActivity::class.java)
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