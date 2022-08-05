package com.trickyandroid.playmusic.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object CommonUtilities {
    private var dialog: ProgressDialog? = null

    fun hideSoftKeyboard(context: Activity) {
        try {
            val imm: InputMethodManager =  context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(context.window.decorView.windowToken, 0)
        } catch (e: Exception) {
//            ExceptionTrack.getInstance().TrackLog(e)
        }
    }

   internal fun showSoftKeyboard(context: Activity, editText: EditText) {
        try {
            editText.postDelayed({
                val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                editText.requestFocus()
                inputMethodManager.showSoftInput(editText, 0)
            }, 100)
        } catch (e: Exception) {
//            ExceptionTrack.getInstance().TrackLog(e)
        }

    }


    fun showProgressDialog(activity: Context?, msg: String?) {
        try {
            cancelProgressDialog(activity)
            if (dialog == null) {
                dialog = ProgressDialog(activity)
                dialog?.setMessage(msg)
                dialog?.setIndeterminate(true)
                dialog?.setCancelable(false)
                //if (!((Activity) activity).isFinishing())
                dialog?.show()
            } else {
                if (!dialog?.isShowing!!) {
                   dialog = ProgressDialog(activity)
                    dialog?.setMessage(msg)
                    dialog?.setIndeterminate(true)
                    dialog?.setCancelable(false)
                    //if (!((Activity) activity).isFinishing())
                    dialog?.show()
                }
            }
        } catch (e: java.lang.Exception) {
//            ExceptionTrack.getInstance().TrackLog(e)
        }
    }

    /**
     * This method is used for canceling the progress Dialog.*
     *
     * @param ctxt
     */
    private fun cancelProgressDialog(context: Context?) {
        try {
            if (dialog != null && dialog?.isShowing!!) {
                dialog?.dismiss()
                dialog = null
            }
        } catch (e: java.lang.Exception) {
//            ExceptionTrack.getInstance().TrackLog(e)
        }
    }


}