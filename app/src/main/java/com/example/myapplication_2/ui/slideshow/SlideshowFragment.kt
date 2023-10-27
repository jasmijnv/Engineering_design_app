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
//ThingSpeak
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

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //ThingSpeak
    private val API_KEY = "ZDBRGUOS0NWQM27Y"
    private val THINGSPEAK_API_URL = "https://api.thingspeak.com/update"
    private val REFRESH_INTERVAL = 10000 // Refresh every 10 seconds
    private val handler = Handler()
    var currentLevels: Int = 0
    var desiredLevels: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        ThingSpeak
        UpdateThingSpeakTask1().execute("20203048")
        UpdateThingSpeakTask2().execute("99999999")
        fetchData()
        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL.toLong())

        val textView: TextView = binding.textWeatherApi
        slideshowViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //ThingSpeak
    private fun createString(desired : Int, current : Int) {
        val moistureLevelText: TextView = binding.moistureLevelText
        val text = "Desired Soil Moisture level 1: ${desired/1000000}\n Current soil Moisture level 1: ${current/1000000}\n Desired Soil Moisture level 2: ${(desired % 1000000)/10000}\n Current soil Moisture level 2: ${(current % 1000000)/10000}\n Desired Soil Moisture level 3: ${(desired % 10000)/100}\n Current soil Moisture level 3: ${(current % 10000)/100}\n Desired Soil Moisture level 4: ${desired % 100}\n Current soil Moisture level 4: ${current % 100}\n"
        Log.d("Desired moisture levels", "$desired")
        Log.d("Current moisture levels", "$current")
        moistureLevelText.text=text
        val progressBar1 = binding.progressBar1
        val progressBar2 = binding.progressBar2
        val progressBar3 = binding.progressBar3
        val progressBar4 = binding.progressBar4
        progressBar1.progress=current/1000000
        progressBar2.progress=(current % 1000000)/10000
        progressBar3.progress=(current % 10000)/100
        progressBar4.progress=current % 100
    }

    fun fetchData() {
        ReadThingSpeakData1().execute()
        ReadThingSpeakData2().execute()
        println(currentLevels)
        println(desiredLevels)
        createString(currentLevels, desiredLevels)
    }

    private val refreshRunnable: Runnable = object : Runnable {
        override fun run() {
            fetchData()

            // Schedule the next refresh
            handler.postDelayed(this, REFRESH_INTERVAL.toLong())
        }
    }
    //ThingSpeak
    private inner class UpdateThingSpeakTask1 : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg data: String): String {
            return try {
                val url = URL(THINGSPEAK_API_URL)
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.doOutput = true

                val postData = "field2=" + data[0] + "&api_key=" + API_KEY
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
    inner class ReadThingSpeakData1 : AsyncTask<Void, Void, String>() {
        private val READ_API_KEY = "DVUX98PA16SLK9V7"
        private val CHANNEL_ID = "2320694"
        override fun doInBackground(vararg p0: Void?): String {
            try {
                val url = URL("https://api.thingspeak.com/channels/$CHANNEL_ID/fields/2/last.json?api_key=$READ_API_KEY")
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
                val field2Data = jsonObject.getString("field2")
                currentLevels = field2Data.toInt()
                val displayText = "Created At: $createdAt\nEntry ID: $entryId\nField2 Data: $field2Data"
                println(displayText)
            } catch (e: Exception) {
                println("Error parsing JSON: " + e.message)
            }
        }
    }
    //ThingSpeak
    private inner class UpdateThingSpeakTask2 : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg data: String): String {
            return try {
                val url = URL(THINGSPEAK_API_URL)
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.doOutput = true

                val postData = "field3=" + data[0] + "&api_key=" + API_KEY
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
    inner class ReadThingSpeakData2 : AsyncTask<Void, Void, String>() {
        private val READ_API_KEY = "DVUX98PA16SLK9V7"
        private val CHANNEL_ID = "2320694"
        override fun doInBackground(vararg p0: Void?): String {
            try {
                val url = URL("https://api.thingspeak.com/channels/$CHANNEL_ID/fields/3/last.json?api_key=$READ_API_KEY")
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
                val field3Data = jsonObject.getString("field3")
                desiredLevels = field3Data.toInt()
                val displayText = "Created At: $createdAt\nEntry ID: $entryId\nField3 Data: $field3Data"
                println(displayText)
            } catch (e: Exception) {
                println("Error parsing JSON: " + e.message)
            }
        }
    }
}