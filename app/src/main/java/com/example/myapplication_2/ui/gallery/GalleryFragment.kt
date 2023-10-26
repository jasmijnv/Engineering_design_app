package com.example.myapplication_2.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication_2.databinding.FragmentGalleryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.round
import android.os.AsyncTask
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import android.os.Handler
class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //Firebase
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.reference
    private val tankStatus = myRef.child("Tank").child("status")

    //ThingSpeak
    private lateinit var dataField: EditText
    private lateinit var sendButton: Button
    private val API_KEY = "ZDBRGUOS0NWQM27Y"
    private val THINGSPEAK_API_URL = "https://api.thingspeak.com/update"
    private val REFRESH_INTERVAL = 10000 // Refresh every 10 seconds
    private val handler = Handler()
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

        //ThingSpeak
        ReadThingSpeakData().execute()
        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL.toLong())

        // creates a button
        // when clicked, it will take the value of water tank from the arduino
        // and change the image and value in the page accordingly
        val button = binding.watertankbutton
        button.setOnClickListener {
            var waterLevel = (1..100).random()
            tankImage(waterLevel)
            //Firebase
            tankStatus.setValue(waterLevel)
            //ThingSpeak
            UpdateThingSpeakTask().execute(waterLevel.toString())
        }

        //Firebase
        tankStatus.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val value = dataSnapshot.getValue()
                    Log.d("file", "Value is: $value")
                } else {
                    println("database doesnt exist")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("error", "Database error")
            }
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun tankImage(level: Int) {
        Log.d("BUTTONS", "User tapped the water tank button")
        var images = arrayOf(binding.watertank0, binding.watertank20, binding.watertank40, binding.watertank60, binding.watertank80, binding.watertank100)
        var currentImage = round(level / 20.0).toInt()
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
    //ThingSpeak
    private val refreshRunnable: Runnable = object : Runnable {
        override fun run() {
            ReadThingSpeakData().execute()
            // Schedule the next refresh
            handler.postDelayed(this, REFRESH_INTERVAL.toLong())
        }
    }
    //ThingSpeak
    private inner class UpdateThingSpeakTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg data: String): String {
            return try {
                val url = URL(THINGSPEAK_API_URL)
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.doOutput = true

                val postData = "field1=" + data[0] + "&api_key=" + API_KEY
                val postDataBytes = postData.toByteArray(Charsets.UTF_8)

                val os: OutputStream = httpURLConnection.outputStream
                os.write(postDataBytes)
                os.flush()
                os.close()

                val responseCode = httpURLConnection.responseCode
                if (responseCode == 200) {
                    "Data sent successfully!"
                } else {
                    "Error sending data."
                }
            } catch (e: Exception) {
                "Error: " + e.message
            }
        }
    }
    //ThingSpeak
    inner class ReadThingSpeakData : AsyncTask<Void, Void, String>() {
        private val READ_API_KEY = "DVUX98PA16SLK9V7"
        private val CHANNEL_ID = "2320694"
        override fun doInBackground(vararg p0: Void?): String {
            try {
                val url = URL("https://api.thingspeak.com/channels/$CHANNEL_ID/fields/1/last.json?api_key=$READ_API_KEY")
                val connection = url.openConnection() as HttpURLConnection

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val result = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    result.append(line)
                }

                reader.close()
                connection.disconnect()

                return result.toString()
            } catch (e: Exception) {
                return "Error: " + e.message
            }
        }

        override fun onPostExecute(result: String) {
            // Parse the JSON response
            try {
                val jsonObject = JSONObject(result)
                val createdAt = jsonObject.getString("created_at")
                val entryId = jsonObject.getInt("entry_id")
                val field1Data = jsonObject.getString("field1")

                val displayText = "Created At: $createdAt\nEntry ID: $entryId\nField1 Data: $field1Data"
                println(displayText)
            } catch (e: Exception) {
                println("Error parsing JSON: " + e.message)
            }
        }
    }
}