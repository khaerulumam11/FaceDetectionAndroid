package com.example.facerecognitionandroid

import android.os.Bundle


import android.view.ViewGroup

import android.view.LayoutInflater
import android.view.View
import android.widget.Button

import androidx.annotation.NonNull

import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment


class ResultDialog : DialogFragment() {
    var okBtn: Button? = null
    var resultTextView: TextView? = null
    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {

        // importing View so as to inflate
        // the layout of our result dialog
        // using layout inflater.
        val view: View = inflater.inflate(
            R.layout.fragment_resultdialog, container,
            false
        )
        var resultText = ""

        // finding the elements by their id's.
        okBtn = view.findViewById(R.id.result_ok_button)
        resultTextView = view.findViewById(R.id.result_text_view)

        // To get the result text
        // after final face detection
        // and append it to the text view.
        val bundle: Bundle? = getArguments()
        resultText = bundle!!.getString(
            LCOFaceDetection.RESULT_TEXT
        ).toString()
        resultTextView!!.text = resultText

        // Onclick listener so as
        // to make a dismissable button
        okBtn!!.setOnClickListener {
            dismiss()
        }
        return view
    }
}