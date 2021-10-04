package com.example.song

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.song.adapter.ListAdapter
import com.example.song.data.model.Song
import com.example.song.data.netwok.Api
import com.example.song.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), PlayerListener {
    private var isPlaying: Boolean = false
    private lateinit var dataBinder: ActivityMainBinding
    var mediaPlayer: MediaPlayer? = null
    var viewSelected : ListAdapter.ViewHolder?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinder = setContentView(this, R.layout.activity_main)

        if(savedInstanceState == null){
            dataBinder.noItemView.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.music
                )
            )
        }


        dataBinder.searchbar.isFocusableInTouchMode = true
//        dataBinder.searchbar.addTextChangedListener(object : TextWatcher {
//            var timer: CountDownTimer? = null
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//                if (timer != null) {
//                    timer!!.cancel()
//                }
//
//                timer = object : CountDownTimer(1500, 1000) {
//                    override fun onTick(millisUntilFinished: Long) {}
//                    override fun onFinish() {
//                        val imm: InputMethodManager = getSystemService(
//                            INPUT_METHOD_SERVICE
//                        ) as InputMethodManager
//                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
//                        dataBinder.playbutton.visibility = View.GONE
//                        searchArtist(s)
////                        this@MainActivity.getWindow()
////                            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//
//                    }
//                }.start()
//
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//            }
//        })

        dataBinder.searchbar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val imm: InputMethodManager = getSystemService(
                            INPUT_METHOD_SERVICE
                        ) as InputMethodManager
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                        dataBinder.playbutton.visibility = GONE
                        searchArtist(dataBinder.searchbar.text.toString())
                true
            } else false
        }

        dataBinder.playbutton.setOnClickListener {
            changeapearence()
            if (isPlaying) {
                stop()
            }else
                start()

        }


    }

    private fun changeapearence() {

        if (isPlaying) {
            dataBinder.playbutton.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.play
                )
            )
            if(viewSelected != null)
            viewSelected!!.binding.playView.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.play
                )
            )
        } else {
            dataBinder.playbutton.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.pause
                )
            )
            if(viewSelected != null)
            viewSelected!!.binding.playView.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.pause
                )
            )
        }
    }

    private fun searchArtist(s: CharSequence?) {
        lifecycleScope.launch {
//            Api().getSongs(searchText.text.toString())
            val call = Api().getSongs(s.toString())

            call.enqueue(object : Callback<Song> {
                override fun onResponse(call: Call<Song>, response: Response<Song>) {
                    if (response.isSuccessful) {
                        if (response.body()!!.resultCount != 0) {
                            if (dataBinder.noItemView.visibility == VISIBLE) {
                                dataBinder.recyclerView.visibility = VISIBLE
                                dataBinder.noItemView.visibility = GONE
                            }
                            val adapter = response.body()!!.Results?.let {
                                ListAdapter(this@MainActivity, it){primaryurl,holder ->
                                    viewSelected = holder
                                    lifecycleScope.launch {
                                        playmusic(primaryurl)
                                    }
                                }
                            }
                            dataBinder.recyclerView.layoutManager =
                                LinearLayoutManager(this@MainActivity)
                            dataBinder.recyclerView.adapter = adapter


                        } else {
                            dataBinder.recyclerView.visibility = GONE
                            dataBinder.noItemView.visibility = VISIBLE
                            dataBinder.noItemView.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this@MainActivity,
                                    R.drawable.nodata
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<Song>, t: Throwable) {
                    println(t.message)
                    dataBinder.recyclerView.visibility = GONE
                    dataBinder.noItemView.visibility = VISIBLE
                    dataBinder.noItemView.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@MainActivity,
                            R.drawable.nodata
                        )
                    )
                }

            })
        }
    }

    var url: String? = ""
    private  fun playmusic(songUrl: String) {
        url = songUrl
        dataBinder.playbutton.visibility = VISIBLE
        if (songUrl.isBlank()) {
           stop()
            return
        }

        stop()

        start()

    }

    override fun start() {

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            prepare()
            start()
            changeapearence()
            this@MainActivity.isPlaying = true
        }
    }

    override fun stop() {
        if(mediaPlayer != null)
          mediaPlayer.let { it!!.release() }
        mediaPlayer = null
        changeapearence()
        this@MainActivity.isPlaying = false


    }

}