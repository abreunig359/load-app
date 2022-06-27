package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.udacity.databinding.ContentDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ContentDetailBinding
    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentDetailBinding.inflate(layoutInflater)

        val status = intent.getStringExtra(STATUS_KEY)
        val fileName = intent.getStringExtra(FILE_NAME_KEY)

        if (status == null || fileName == null) {
            throw MissingDetailParameterException(fileName, status)
        }

        initializeViewModel(fileName, status)

        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
    }

    private fun initializeViewModel(fileName: String, status: String) {
        val viewModelFactory = DetailViewModel.Factory(fileName, status)
        val viewModelProvider = ViewModelProvider(this, viewModelFactory)
        viewModel = viewModelProvider[DetailViewModel::class.java]
        binding.viewModel = viewModel
    }

    private fun navigateToMainActivity() {
        val switchActivityIntent = Intent(this, MainActivity::class.java)
        startActivity(switchActivityIntent)
    }
}

class MissingDetailParameterException(private val fileName: String?, private val status: String?) :
    Exception("Parameter was not passed from main activity, fileName: $fileName, status: $status")