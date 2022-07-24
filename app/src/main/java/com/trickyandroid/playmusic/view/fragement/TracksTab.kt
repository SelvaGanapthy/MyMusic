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

class TracksTab : Fragment(R.layout.layout_recyclerview), ISearch {

    private var rv: RecyclerView? = null
    private var adapter: TracksAdapter? = null
    private var tracksList: ArrayList<SongInfoModel> = ArrayList()

    /*Search View*/
    private var mIFragmentListener: IFragmentListener? = null
    private var mSearchTerm: String? = null
    private var swipeRefreshHotcases: SwipeRefreshLayout? = null

    companion object {
        val ARG_SEARCHTERM: String = "search_term"

        fun newInstances(searchTerm: String): TracksTab {
            val fragement = TracksTab()
            val bundle = Bundle()
            bundle.putString(ARG_SEARCHTERM, searchTerm)
            fragement.arguments = bundle
            return fragement
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            AppController.tracksTab = this
            rv = view.findViewById<View>(R.id.rv) as RecyclerView
            this.tracksList = arguments?.getSerializable("SongsInfoList") as (ArrayList<SongInfoModel>)
            swipeRefreshHotcases = view.findViewById<View>(R.id.swiperRefresh) as SwipeRefreshLayout
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

            setAnimator()
            setLayoutManager()
            setAdapter()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
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
//            rv?.isDrawingCacheEnabled = true;
//            rv?.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAdapter() {
        try {
            adapter = TracksAdapter(requireContext(), tracksList)
            rv?.adapter = this.adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIFragmentListener = context as (IFragmentListener)
        mIFragmentListener?.addiSearch(this@TracksTab as (ISearch))
    }

    override fun onDetach() {
        super.onDetach()
        if (null != mIFragmentListener) {
            mIFragmentListener?.removeISearch(this@TracksTab as (ISearch))
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
}