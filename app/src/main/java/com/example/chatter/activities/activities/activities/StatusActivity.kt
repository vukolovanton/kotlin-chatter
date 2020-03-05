package com.example.chatter.activities.activities.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatter.R
import kotlinx.android.synthetic.main.activity_status.*

class StatusActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        title = "Status";

        if (intent.extras != null) {
            var oldStatus = intent.extras!!.get("status")
            statusUpdateEt.setText(oldStatus.toString())
        }

        if (intent.extras!!.equals(null)) {
            statusUpdateEt.setText("Enter Your New Status")
        }

        statusUpdateButton.setOnClickListener {

        }
    }
}
