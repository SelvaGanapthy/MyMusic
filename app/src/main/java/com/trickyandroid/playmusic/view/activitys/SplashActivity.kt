package com.trickyandroid.playmusic.view.activitys

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.flaviofaria.kenburnsview.KenBurnsView
import com.flaviofaria.kenburnsview.Transition
import com.trickyandroid.playmusic.R
import android.widget.LinearLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


class SplashActivity : AppCompatActivity() {
    var kbvspalsh: KenBurnsView? = null
    //    var imvLogo: ImageView? = null
    var linearLogo: LinearLayout? = null

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        kbvspalsh = findViewById<View>(R.id.kbvspalsh) as KenBurnsView
        linearLogo = findViewById<View>(R.id.linearLogo) as LinearLayout

        var animFadeIn = AnimationUtils.loadAnimation(this, R.transition.transition_topin)
        linearLogo?.startAnimation(animFadeIn)

        kbvspalsh?.setTransitionListener(object : KenBurnsView.TransitionListener {
            override fun onTransitionStart(transition: Transition) {

            }

            override fun onTransitionEnd(transition: Transition) {

            }
        })

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this@SplashActivity)) {

                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
                { result: ActivityResult ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        //  you will get result here in result.data
                    }

                }

                startForResult.launch(intent)
                Toast.makeText(applicationContext, "Please turn On", Toast.LENGTH_SHORT).show()

//                startActivityForResult(intent, 77)

            }
        }

        if (checkPermisson()) {
            splash()
        } else {
            requestPermission()
        }

        overridePendingTransition(R.transition.transition_topin, 0)

    }

    override fun onStart() {
        super.onStart()
        checkPermisson()
    }

    fun checkPermisson(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val result1 = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
//        val result2 = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_PHONE_STATE)
        val result3 = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO)
        val result4 = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.INTERNET)
//        val result5 = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CHANGE_CONFIGURATION)
//        val result6 = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_SETTINGS)
//                && result2 == PackageManager.PERMISSION_GRANTED
//                && result5 == PackageManager.PERMISSION_GRANTED && result6 == PackageManager.PERMISSION_GRANTED
        return (result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED  &&
                result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED )
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(this@SplashActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET), requestcode_permisson)
//        , Manifest.permission.CHANGE_CONFIGURATION, Manifest.permission.WRITE_SETTINGS
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestcode_permisson -> if (grantResults.size > 0) {
                var ExStroagePermisson = grantResults[0] == PackageManager.PERMISSION_DENIED
//                var readphonePermisson = grantResults[1] == PackageManager.PERMISSION_DENIED
                var camerPermisson = grantResults[2] == PackageManager.PERMISSION_DENIED
                var audioPermisson = grantResults[3] == PackageManager.PERMISSION_DENIED
                var internet = grantResults[4] == PackageManager.PERMISSION_DENIED
//                var changeconfig = grantResults[5] == PackageManager.PERMISSION_DENIED
//                var writeSetting = grantResults[6] == PackageManager.PERMISSION_DENIED
//                        && readphonePermisson
//                (changeconfig && writeSetting)
                if (ExStroagePermisson  || camerPermisson || audioPermisson || internet ) {
                    Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT).show()
                    requestPermission()
                } else {
                    splash()
                }
            }
        }
    }


    fun splash() {
        Handler(Looper.getMainLooper()).postDelayed(
                {
                    val i = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(i)
                    finish()
                }, 4000)
    }

    companion object {
        val requestcode_permisson = 123
    }
}
