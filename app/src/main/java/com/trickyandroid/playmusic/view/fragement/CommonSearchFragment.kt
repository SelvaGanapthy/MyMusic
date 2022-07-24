package com.trickyandroid.playmusic.view.fragement

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.trickyandroid.playmusic.R
import com.trickyandroid.playmusic.app.AppController
import com.trickyandroid.playmusic.models.SongInfoModel
import com.trickyandroid.playmusic.utils.CommonUtilities
import com.trickyandroid.playmusic.view.adapters.Common_Registration_Adapter
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class CommonSearchFragment : Fragment(R.layout.fragment_search_common) {

    private var flag = 0
    private var from = ""
    private var contxt: Context? = null
    private var regSearchEditText: EditText? = null
    private var regListView: ListView? = null
    private var drawerClose: ImageView? = null
    private var common_Registration_Adapter: Common_Registration_Adapter? = null
    private var commonRightMenuFragmentListener: CommonRightMenuFragmentListener? = null
    private var tempList: ArrayList<SongInfoModel> = ArrayList()

    interface CommonRightMenuFragmentListener {
        /**
         * / ** flag  = 0 nothing selected
         * flag = 1 selected
         */
        fun onItemSelect(flag: Int, key: String?, value: String?)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        contxt = context
        if (activity is CommonRightMenuFragmentListener) {
            commonRightMenuFragmentListener = activity as CommonRightMenuFragmentListener
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        regSearchEditText = view.findViewById(R.id.reg_search_editText)
        regListView = view.findViewById<ListView>(R.id.reg_listView)
        //        timeoutLayout = view.findViewById(R.id.timeout_layout)
        drawerClose = view.findViewById<ImageView>(R.id.drawer_close)
        val bundle = this.arguments
        if (bundle != null) {
            flag = bundle.getInt("flag") // bundle.getInt(getString(R.string.flag));
            from = if (bundle.getString("from") != null) bundle.getString("from")!! else ""
//            songsList = bundle.getSerializable("artist") as ArrayList<SongInfoModel>
            when (flag) {
                0 -> tempList.addAll(AppController.mainActivity?.TracksList!!)
                1 -> tempList.addAll(AppController.mainActivity?.AlbumsList!!)
                2 -> tempList.addAll(AppController.mainActivity?.ArtistsList!!)
            }
        }

        searchFunctionlaty(regSearchEditText!!)
        common_Registration_Adapter = Common_Registration_Adapter(context, tempList,flag)
        regListView?.adapter=common_Registration_Adapter
        regListView?.setOnItemClickListener { parent, view, position, id ->
            try {
                val id: Int = when (flag) {
                    0 -> tempList[position].getTrackId()
                    1 -> tempList[position].getAlbumnewId()
                    2 -> tempList[position].getArtistId()
                    else -> tempList[position].getId()
                }
                commonRightMenuFragmentListener!!.onItemSelect(id, "key", tempList[position].getSongName())

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        super.onViewCreated(view, savedInstanceState)
    }

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        val view: View = inflater.inflate(R.layout.fragment_search_common, container, false)
//        regSearchEditText = view.findViewById(R.id.regSearchEditText)
//        regListView = view.findViewById<ListView>(R.id.regListView)
//        //        timeoutLayout = view.findViewById(R.id.timeout_layout);
//        drawerClose = view.findViewById<ImageView>(R.id.drawerClose)
//        val bundle = this.arguments
//        if (bundle != null) {
//            flag = bundle.getInt("flag") // bundle.getInt(getString(R.string.flag));
//            from = if (bundle.getString("from") != null) bundle.getString("from")!! else ""
////            songsList = bundle.getSerializable("artist") as ArrayList<SongInfoModel>
//             when (flag) {
//                0 -> tempList.addAll(AppController.mainActivity?.TracksList!!)
//                1 -> tempList.addAll(AppController.mainActivity?.AlbumsList!!)
//                2 -> tempList.addAll(AppController.mainActivity?.ArtistsList!!)
//            }
//        }
//
//        searchFunctionlaty(regSearchEditText!!)
//        common_Registration_Adapter = Common_Registration_Adapter(context, tempList,flag)
//        regListView?.setAdapter(common_Registration_Adapter)
//        regListView?.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
//            try {
//                val id: Int = when (flag) {
//                    0 -> tempList.get(position).getTrackId()
//                    1 -> tempList.get(position).getAlbumnewId()
//                    2 -> tempList.get(position).getArtistId()
//                    else -> tempList.get(position).getId()
//                }
//                commonRightMenuFragmentListener!!.onItemSelect(id, "key", tempList.get(position).getSongName())
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        })
//
//        return view
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?= super.onCreateView(inflater, container, savedInstanceState)


    private fun searchFunctionlaty(search_editText: EditText) {
        search_editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val text = search_editText.text.toString().toLowerCase(Locale.getDefault())
//                searchFlag = 0
                if (common_Registration_Adapter != null) {
                    common_Registration_Adapter?.filter(text)
                    regListView!!.setSelection(0)
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CommonUtilities.hideSoftKeyboard(requireActivity())
    }

}