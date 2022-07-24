package com.trickyandroid.playmusic.utils

 import android.content.Context
import androidx.core.os.EnvironmentCompat
import java.io.File
import java.util.*

object StringUtility {
    var REMOVE_LIST = arrayOf("www.songs.pk", "www.", ".com", ".pl", ".pk", ".org", ".co.in", "~requested~", "(musictub)", ".mp3", EnvironmentCompat.MEDIA_UNKNOWN, "()", "\\[\\]")
    var REPLACE_LIST = arrayOfNulls<Array<String>>(6)
    val PREF_APPID = "radio"
    val BLANK_MESSAGE = ""

    init {
        var r0 = arrayOfNulls<Array<String>>(6)
        r0[0] = arrayOf("247", "24x7")
        r0[1] = arrayOf(PREF_APPID, "radio")
        r0[2] = arrayOf("-", "")
        r0[3] = arrayOf("- -", "-")
        r0[4] = arrayOf("  ", " ")
        r0[5] = arrayOf("[^a-zA-Z0-9 ()\\[\\],-]", BLANK_MESSAGE)
        REPLACE_LIST = r0
    }


    fun removeSpecialChars(string: String): String {
        var string = string
        if (string == null) {
            return string;
        }

        string = string.toLowerCase(Locale.getDefault())

        for (removeString in REMOVE_LIST) {
            string = string.replace(removeString.toRegex(), BLANK_MESSAGE)
        }


        for (replaceString in this.REPLACE_LIST!!) {
            string = string.replace(replaceString!![0].toRegex(), replaceString[1])
        }


//        return toTitleCase(string.trim { it <= ' ' })


        return string
    }

    private fun toTitleCase(input: String): String {
        val titleCase: StringBuilder = StringBuilder()
        var nextTitleCase: Boolean = true
        for (c in input.toCharArray()) {
            var c2: Char = 0.toChar()
            if (Character.isSpaceChar(c2) || "(" == c2.toString() || "[" == c2.toString()) {
                nextTitleCase = true
            } else if (nextTitleCase) {
                c2 = Character.toTitleCase(c2)
                nextTitleCase = false
            }
            titleCase.append(c2)
        }
        return titleCase.toString()
    }


    fun trimCache(context: Context) {
        try {
            val dir: File = context.cacheDir
            if (dir != null && dir.isDirectory) {
                deleteDir(dir)
            }
        } catch (e: Exception) {
            // TODO: handle exception
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children: Array<String> = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }

        // The directory is now empty so delete it
        return dir!!.delete()
    }


}