package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val status = intent.getStringExtra(STATUS_KEY)
        val fileName = intent.getStringExtra(FILE_NAME_KEY)

        if (status == null || fileName == null) {
            throw MissingDetailParameterException(fileName, status)
        }

        file_name_text.text = fileName
        status_text.text = status

        ok_button.setOnClickListener {
            navigateToMainActivity()
        }

        setSupportActionBar(toolbar)
    }


    private fun navigateToMainActivity() {
        val switchActivityIntent = Intent(this, MainActivity::class.java)
        startActivity(switchActivityIntent)
    }
}

class MissingDetailParameterException(fileName: String?, status: String?) :
    Exception("Parameter was not passed from main activity, fileName: $fileName, status: $status")