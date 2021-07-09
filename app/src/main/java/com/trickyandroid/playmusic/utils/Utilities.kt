package com.trickyandroid.playmusic.utils

class Utilities {

    fun milliSecondsToTimer(milliseconds: Long): String {
        var finalTimerString: String = ""
        var secondsString: String = ""
        // Convert total duration into time
        var hours = (milliseconds / (1000 * 60 * 60)).toInt()
        var minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        var seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
//            pls
            finalTimerString = "" + hours + ":"
        }
        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds
        } else {
            secondsString = "" + seconds
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString
        // return timer string
        return finalTimerString
    }


    /**
     * Function to get Progress percentage
     *
     * @param currentDuration
     * @param totalDuration
     */

    fun getProgressPercentage(currentDuration: Long, totalDuration: Long): Int {
        var percentage: Double = 0.toDouble()
        var currentSeconds: Long = (currentDuration / 1000).toInt().toLong()
        var totalSeconds: Long = (totalDuration / 1000).toInt().toLong()
        // calculating percentage
        percentage = ((currentSeconds.toDouble()) / totalSeconds) * 100
        // return percentage
        return percentage.toInt()
    }

    /**
     * Function to change progress to timer
     *
     * @param progress      -
     * @param totalDuration returns current duration in milliseconds
     */

    fun progressToTimer(progress: Int, totalDuration: Int): Int {
        var currentDuration: Int = 0
        var totDuration = totalDuration
        totDuration = (totDuration / 1000).toInt()
        currentDuration = (((progress.toDouble()) / 100) * totDuration).toInt()
        // return current duration in milliseconds
        return currentDuration * 1000
    }

}


