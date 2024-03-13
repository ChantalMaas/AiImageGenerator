package com.example.aiimagemaker

import android.R.attr
import android.annotation.SuppressLint
import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    // creating variables on below line.
    lateinit var imageIV: ImageView
    lateinit var questionTV: TextView
    lateinit var queryEdt: TextInputEditText
//    private lateinit var switchButton : Button

    var url = "https://api.limewire.com/api/image/generation"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initializing variables on below line.
        imageIV = findViewById(R.id.idIVImage)
        questionTV = findViewById(R.id.idTVQuestion)
        queryEdt = findViewById(R.id.idEdtQuery)

        // adding editor action listener for edit text on below line.
        queryEdt.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                // setting response tv on below line.
                // validating text
                if (queryEdt.text.toString().length > 0) {
                    // calling get response to get the response.
                    getResponse(queryEdt.text.toString())
                } else {
                    Toast.makeText(this, "Please enter your query..", Toast.LENGTH_SHORT).show()
                }
                return@OnEditorActionListener true
            }
            false
        })
//        switchButton = findViewById(R.id.switchButton)
//
//        switchButton.setOnClickListener {
//            val intent = Intent(this, IngredientActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun getResponse(query: String) {
        // setting text on for question on below line.
        questionTV.text = query
        queryEdt.setText("")
        // creating a queue for request queue.
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
        // creating a json object on below line.
        val jsonObject: JSONObject? = JSONObject()
        // adding params to json object.
        jsonObject?.put("prompt", query)
        jsonObject?.put("aspect_ratio", "1:1")

        // on below line making json object request.
        val postRequest: JsonObjectRequest =
            // on below line making json object request.
            object : JsonObjectRequest(Method.POST, url, jsonObject,
                Response.Listener { response ->
                    try {
                        Log.d("TAGAPI", "Response JSON: $response")
                        if (response.has("data") && !response.isNull("data")) {
                            val dataArray = response.getJSONArray("data")
                            if (dataArray.length() > 0) {
                                val firstDataObject = dataArray.getJSONObject(0)
                                val imageURL = firstDataObject.optString("asset_url")
                                if (!imageURL.isNullOrEmpty()) {
                                    // Load the image using Picasso
                                    Picasso.get().load(imageURL).into(imageIV)
                                } else {
                                    Log.e("TAGAPI", "Image URL is null or empty")
                                }
                            } else {
                                Log.e("TAGAPI", "Data array is empty")
                            }
                        } else {
                            Log.e("TAGAPI", "Data object is null or missing")
                        }
                    } catch (e: JSONException) {
                        Log.e("TAGAPI", "Error parsing JSON: ${e.message}")
                    }
                },

                // adding on error listener
                Response.ErrorListener { error ->
                    Log.e("TAGAPI", "Error is : " + error.message + "\n" + error)
                }) {
                override fun getHeaders(): kotlin.collections.MutableMap<kotlin.String, kotlin.String> {
                    val params: MutableMap<String, String> = HashMap()
                    // adding headers on below line.
                    params["Content-Type"] = "application/json"
                    params["Authorization"] = "Bearer lmwr_sk_tXB3YXStTu_UYSI0llPgkkdZaCDGtuk11sv0ZnV6JOMWqoMP"
                    return params;
                }
            }
        // on below line adding retry policy for our request.
        postRequest.setRetryPolicy(object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        })
        // on below line adding our request to queue.
        queue.add(postRequest)
    }
}
