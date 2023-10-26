package com.example.myapplication_2.ui.slideshow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication_2.MainActivity
import com.example.myapplication_2.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textWeatherApi
        slideshowViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        // creates a button
        // when clicked, it will take the moisture levels from the arduino
        // and change the progress bars and values in the page accordingly
//        val button = binding.progressbutton
//        val progressBar0 = binding.progressBar0
//        val progressBar1 = binding.progressBar1
//        val progressBar2 = binding.progressBar2
//        val progressBar3 = binding.progressBar3
//        val progressBars = listOf(progressBar0, progressBar1, progressBar2, progressBar3)
//        button.setOnClickListener {
//            val moistureLevels = arrayOfNulls<Int>(4)
//            for (i in 0..3) {
//                moistureLevels[i] = (0..100).random()
//            }
//            changeProgressBars(moistureLevels, progressBars)
//            println(progressBar0.height)
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeProgressBars(percentageList: Array<Int?>, progressList: List<ProgressBar>){
        for (i in 0..3) {
            progressList[i].progress = percentageList[i]!!
            Log.d("Moisture value zone $i", "${percentageList[i]}")
        }
    }
}