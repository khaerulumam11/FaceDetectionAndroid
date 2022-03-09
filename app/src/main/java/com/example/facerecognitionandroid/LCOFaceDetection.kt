package com.example.facerecognitionandroid


import android.app.Application


class LCOFaceDetection : Application() {
    // initializing our firebase
    override fun onCreate() {
        super.onCreate()
//        FirebaseApp.initializeApp(this)
    }

    companion object {
        const val RESULT_TEXT = "RESULT_TEXT"
        const val RESULT_DIALOG = "RESULT_DIALOG"
    }
}