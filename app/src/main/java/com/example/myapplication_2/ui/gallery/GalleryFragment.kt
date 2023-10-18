package com.example.myapplication_2.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication_2.databinding.FragmentGalleryBinding
import kotlin.math.round

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
//        code for displaying the default text:
//        val textView: TextView = binding.textGallery
//        galleryViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        // creates a button
        // when clicked, it will take the value of water tank from the arduino
        // and change the image and value in the page accordingly
        val button = binding.watertankbutton
        button.setOnClickListener {
            val waterLevel = (1..100).random()
            tankImage(waterLevel)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun tankImage(level: Int) {
        Log.d("BUTTONS", "User tapped the water tank button")
        var images = arrayOf(binding.watertank0, binding.watertank20, binding.watertank40, binding.watertank60, binding.watertank80, binding.watertank100)
        val currentImage = round(level / 20.0).toInt()
        var levelText = binding.textView2
        levelText.text="Current water level: ${currentImage*20}%"
        Log.d("WATER-LEVEL", "$level")
        Log.d("CURRENT-IMAGE", "$currentImage")

        for (i in 0..5) {
            if (i == currentImage){
                images[i].visibility = View.VISIBLE
            } else {
                images[i].visibility = View.GONE
            }

        }
    }
}