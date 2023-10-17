package com.example.myapplication_2.ui.weather_api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeatherApiViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is weather api Fragment"
    }
    val text: LiveData<String> = _text
}