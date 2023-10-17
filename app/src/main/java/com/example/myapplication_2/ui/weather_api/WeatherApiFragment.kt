package com.example.myapplication_2.ui.weather_api

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication_2.databinding.FragmentSlideshowBinding
import com.example.myapplication_2.databinding.FragmentWeatherApiBinding

class WeatherApiFragment : Fragment() {

    private var _binding: FragmentWeatherApiBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val WeatherApiViewModel =
            ViewModelProvider(this).get(WeatherApiViewModel::class.java)

        _binding = FragmentWeatherApiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textWeatherApi
        WeatherApiViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}