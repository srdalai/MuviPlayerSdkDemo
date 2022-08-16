package com.muvi.tvsdk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.muvi.tvplayer.utils.Util
import com.muvi.tvsdk.databinding.ActivityPlayerDemoBinding

class PlayerDemoActivity : AppCompatActivity() {

    private val TAG = "PlayerDemoActivity"

    lateinit var binding: ActivityPlayerDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSelectFile.setOnClickListener(View.OnClickListener {
            //openFile()
            startActivity(Intent(this, DemoPlayerActivity::class.java))
        })

        Util.supportsResolution("2160")

        val fileInString: String = applicationContext.assets.open("media_list.json").bufferedReader().use { it.readText() }
        //Log.d(TAG, fileInString)

        val listType = object : TypeToken<List<MediaObject>>() {}.type
        val mediaItems: List<MediaObject> = Gson().fromJson(fileInString, listType)

        binding.listView.adapter = MediaObjectAdapter(mediaItems)
        binding.listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val intent = Intent(this@PlayerDemoActivity, DemoPlayerActivity::class.java)
                intent.putExtra("media_object", mediaItems[position])
                startActivity(intent)
            }
    }

    // Request code for selecting a PDF document.
    val PICK_PDF_FILE = 2

    fun openFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, PICK_PDF_FILE)
    }

    fun openFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
        }

        startActivityForResult(intent, PICK_PDF_FILE)
    }


}