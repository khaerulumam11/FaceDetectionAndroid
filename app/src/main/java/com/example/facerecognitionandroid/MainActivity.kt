package com.example.facerecognitionandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import androidx.annotation.NonNull

import android.graphics.Bitmap

import android.content.Intent

import android.provider.MediaStore

import android.app.Activity
import android.widget.Button
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    var cameraButton: Button? = null
    var image: InputImage? = null
    var detector: FaceDetector? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initializing our firebase in main activity
//        FirebaseApp.initializeApp(this)

        // finding the elements by their id's alloted.
        cameraButton = findViewById(R.id.camera_button)

        // setting an onclick listener to the button so as
        // to request image capture using camera
        cameraButton!!.setOnClickListener {
            val intent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
            )
            if (intent.resolveActivity(
                    packageManager
                )
                != null
            ) {
                startActivityForResult(
                    intent, REQUEST_IMAGE_CAPTURE
                )
            } else {
                // if the image is not captured, set
                // a toast to display an error image.
                Toast
                    .makeText(
                        this@MainActivity,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
        }

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        // after the image is captured, ML Kit provides an
        // easy way to detect faces from variety of image
        // types like Bitmap
        super.onActivityResult(
            requestCode, resultCode,
            data
        )
        if (requestCode == REQUEST_IMAGE_CAPTURE
            && resultCode == RESULT_OK
        ) {
            val extra = data!!.extras
            val bitmap = extra!!["data"] as Bitmap?
            detectFace(bitmap)
        }
    }

    // If you want to configure your face detection model
    // according to your needs, you can do that with a
    // FirebaseVisionFaceDetectorOptions object.
    private fun detectFace(bitmap: Bitmap?) {
        val options: FaceDetectorOptions = FaceDetectorOptions .Builder()
            .setPerformanceMode(
                FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE
            )
            .setLandmarkMode(
                FaceDetectorOptions.LANDMARK_MODE_ALL
            )
            .setClassificationMode(
                FaceDetectorOptions.CLASSIFICATION_MODE_ALL
            )
            .setContourMode(
                FaceDetectorOptions.CONTOUR_MODE_ALL
            )
            .enableTracking()
            .setMinFaceSize(0.15f)
            .build()

        // we need to create a FirebaseVisionImage object
        // from the above mentioned image types(bitmap in
        // this case) and pass it to the model.
        try {
            image = InputImage.fromBitmap(bitmap!!,0)
            detector = FaceDetection.getClient(options)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // It’s time to prepare our Face Detection model.
        detector!!.process(image!!)
            .addOnSuccessListener { firebaseVisionFaces ->

                // adding an onSuccess Listener, i.e, in case
                // our image is successfully detected, it will
                // append it's attribute to the result
                // textview in result dialog box.
                var resultText: String? = ""
                var i = 1
                for (face in firebaseVisionFaces) {
                    val bounds = face.boundingBox
                    val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
                    val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

                    // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                    // nose available):
                    val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
                    leftEar?.let {
                        val leftEarPos = leftEar.position
                    }

                    // If classification was enabled:
                    if (face.smilingProbability != null) {
                        val smileProb = face.smilingProbability
                    }
                    if (face.rightEyeOpenProbability != null) {
                        val rightEyeOpenProb = face.rightEyeOpenProbability
                    }

                    // If face tracking was enabled:
                    if (face.trackingId != null) {
                        val id = face.trackingId
                    }
                    resultText = resultText+"""
                    
                    FACE NUMBER. $i: 
                    """.trimIndent() +
                            ("\nSmile: "
                                    + (face.smilingProbability?.times(100)) + "%") +
                            ("\nleft eye open: "
                                    + (face.leftEyeOpenProbability?.times(100)) + "%") +
                            ("\nright eye open "
                                    + (face.rightEyeOpenProbability?.times(100)) + "%")
                    i++
                }

                // if no face is detected, give a toast
                // message.
                if (firebaseVisionFaces.size == 0) {
                    Toast
                        .makeText(
                            this@MainActivity,
                            "NO FACE DETECT",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                } else {
                    val bundle = Bundle()
                    bundle.putString(
                        LCOFaceDetection.RESULT_TEXT,
                        resultText
                    )
                    val resultDialog: DialogFragment = ResultDialog()
                    resultDialog.setArguments(bundle)
                    resultDialog.setCancelable(true)
                    resultDialog.show(
                        supportFragmentManager,
                        LCOFaceDetection.RESULT_DIALOG
                    )
                }
            } // adding an onfailure listener as well if
            // something goes wrong.
            .addOnFailureListener {
                Toast
                    .makeText(
                        this@MainActivity,
                        "Oops, Something went wrong",
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
    }

    companion object {
        // whenever we request for our customized permission, we
        // need to declare an integer and initialize it to some
        // value .
        private const val REQUEST_IMAGE_CAPTURE = 124
    }
}