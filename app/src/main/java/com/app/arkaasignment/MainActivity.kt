package com.app.arkaasignment

import ImageAdapter
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.arkaasignment.R.*
import com.app.arkaasignment.model.Data
import com.app.arkaasignment.model.ImageDataClass
import com.app.arkaasignment.networking.ApiClient
import com.app.arkaasignment.networking.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var toggleButton: ToggleButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var search: SearchView
    private var listLayout = true
    var imgList = arrayListOf<Data>()
    lateinit var adapter: ImageAdapter

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        // init views
        initViews()

        // search view implementation
        val cancelIcon =
            search.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        cancelIcon.setColorFilter(Color.WHITE)

        search.queryHint = "Search here.."
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

        // toggle btn to change list or grid
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            listLayout = !isChecked
            setRecyclerViewLayout()
        }

        // api call for get the data
        apiCall()
    }

    private fun initViews() {
        toggleButton = findViewById(id.toggleButton)
        recyclerView = findViewById(id.recyclerView)
        progressBar = findViewById(id.progressBar)
        search = findViewById(id.search_view)
    }

    private fun apiCall() {
        if (ApiClient.isOnline(this)) {
            val call = ApiClient.service.getData(ApiInterface.CLIENT_ID)
            call.enqueue(object : Callback<ImageDataClass> {
                override fun onResponse(
                    call: Call<ImageDataClass>,
                    response: Response<ImageDataClass>
                ) {
                    if (response.isSuccessful) {
                        imgList.clear()
                        response.body()?.data?.let { imgList.addAll(it) }
                        progressBar.visibility = View.GONE
                        setRecyclerViewLayout()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            response.errorBody()?.string(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ImageDataClass>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } else {
            Toast.makeText(this, "Please check your internet and try again", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setRecyclerViewLayout() {
        recyclerView.layoutManager = if (listLayout) {
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        } else {
            GridLayoutManager(this, 2)
        }
        adapter = ImageAdapter(this, imgList, listLayout)
        recyclerView.adapter = adapter
    }
}

