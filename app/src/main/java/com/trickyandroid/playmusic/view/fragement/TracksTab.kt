package com.trickyandroid.playmusic.view.fragement

import android.content.Context
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
import com.trickyandroid.playmusic.view.adapters.TracksAdapter
import com.trickyandroid.playmusic.app.AppController
import com.trickyandroid.playmusic.view.interfaces.IFragmentListener
import com.trickyandroid.playmusic.view.interfaces.ISearch
import com.trickyandroid.playmusic.models.SongInfoModel

class TracksTab() : Fragment(), ISearch {
    var vi: View? = null
    var rv: RecyclerView? = null
    var adapter: TracksAdapter? = null
    var TracksList: ArrayList<SongInfoModel> = ArrayList()
    /*Search View*/
    var mIFragmentListener: IFragmentListener? = null
    var mSearchTerm: String? = null
    var swipeRefreshHotcases: SwipeRefreshLayout? = null

    companion object {
        val ARG_SEARCHTERM: String = "search_term"

        fun newInstances(searchTerm: String): TracksTab {
            var fragement: TracksTab = TracksTab()
            var bundle: Bundle = Bundle()
            bundle.putString(ARG_SEARCHTERM, searchTerm)
            fragement.arguments = bundle
            return fragement
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        try {
            AppController.tracksTab = this
            vi = inflater?.inflate(R.layout.layout_recyclerview, container, false)
            rv = vi?.findViewById<View>(R.id.rv) as RecyclerView
            this.TracksList = arguments?.getSerializable("SongsInfoList") as (ArrayList<SongInfoModel>)
            swipeRefreshHotcases = vi?.findViewById<View>(R.id.swiperRefresh) as SwipeRefreshLayout
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

            set_animator()
            set_layout_manager()
            set_adapter()


        } catch (e: Exception) {
            e.printStackTrace()
        }
        return vi
    }


    private fun set_animator(): Unit {
        var animator: DefaultItemAnimator = DefaultItemAnimator()
        animator.changeDuration = 1000
        rv?.itemAnimator = animator
    }

    private fun set_layout_manager(): Unit {
        try {
            rv?.layoutManager = LinearLayoutManager(context)
            rv?.setHasFixedSize(true)
            rv?.setItemViewCacheSize(20)
            rv?.setDrawingCacheEnabled(true);
            rv?.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun set_adapter(): Unit {
        try {

            adapter = TracksAdapter(requireContext(),TracksList)
            rv?.adapter = this.adapter

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIFragmentListener = context as (IFragmentListener);
        mIFragmentListener?.addiSearch(this@TracksTab as (ISearch));
    }

    override fun onDetach() {
        super.onDetach()
        if (null != mIFragmentListener) {
            mIFragmentListener?.removeISearch(this@TracksTab as (ISearch));
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


    override fun onTextQuery(text: String): Unit {
        adapter?.getFilter()?.filter(text)
        rv?.recycledViewPool?.clear()
        rv?.isNestedScrollingEnabled = false
        adapter?.notifyDataSetChanged()
    }


}