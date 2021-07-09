//package com.trickyandroid.playmusic.service
//
//import android.util.Log
//import com.google.firebase.iid.FirebaseInstanceId
//import com.google.firebase.messaging.FirebaseMessagingService
//
//class MyFirebaseIDService : FirebaseMessagingService() {
//
//    /*remove pls*/
//    override fun onNewToken(p0: String?) {
//        super.onNewToken(p0)
//
//        val refreshedToken = FirebaseInstanceId.getInstance().token
//
//        sendRegistrationToServer(refreshedToken)
//
//    }
///*pls*/
////    override fun onTokenRefresh() {
////        super.onTokenRefresh()
////
////        val refreshedToken = FirebaseInstanceId.getInstance().token
////
////        //        // Saving reg id to shared preferences
////        //        storeRegIdInPref(refreshedToken);
////
////        // sending reg id to your server
////        sendRegistrationToServer(refreshedToken)
////
////        //        // Notify UI that registration has completed, so the progress indicator can be hidden.
////        //        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
////        //        registrationComplete.putExtra("token", refreshedToken);
////        //        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
////    }
//
//
//    private fun sendRegistrationToServer(token: String?) {
//        // sending gcm token to server
//        Log.e(TAG, "sendRegistrationToServer: " + token!!)
//    }
//
//    companion object {
//
//        private val TAG = MyFirebaseIDService::class.java.simpleName
//    }
//
//    //    private void storeRegIdInPref(String token) {
//    //        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
//    //        SharedPreferences.Editor editor = pref.edit();
//    //        editor.putString("regId", token);
//    //        editor.commit();
//    //    }
//}