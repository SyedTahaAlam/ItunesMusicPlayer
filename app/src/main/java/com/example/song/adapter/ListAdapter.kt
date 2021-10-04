package com.example.song.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.song.R
import com.example.song.data.model.Result
import com.example.song.databinding.ListItemBinding
import com.squareup.picasso.Picasso

class ListAdapter(
    val context: Context,
    val list: ArrayList<Result>,
    val clicklistener: (String, ViewHolder?) -> Unit
) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    var previousHolder: ViewHolder? = null

    class ViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val songs: Result = list[position]
        holder.binding.playView.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.play
            )
        )
        Picasso.get().load(songs.artworkUrl100).into(holder.binding.imageViewProduct)

        holder.binding.trackName.text = songs.trackName

        holder.binding.collectionName.text = songs.collectionName

        holder.binding.artistName.text = songs.artistName

        holder.binding.parent.setOnClickListener {
            if (previousHolder != null) {

                previousHolder!!.binding.playView.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.play
                    )
                )
                if (previousHolder == holder) {
                    clicklistener("",null)
                    previousHolder = null
                    return@setOnClickListener
                }
            }
            holder.binding.playView.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.pause
                )
            )
            previousHolder = holder
            if(!songs.previewUrl.isNullOrBlank())
                 clicklistener(songs.previewUrl!!,holder)
            else
                Toast.makeText(context,"No preview url found",Toast.LENGTH_LONG).show()
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}