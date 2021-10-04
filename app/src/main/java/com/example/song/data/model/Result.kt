package com.example.song.data.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)

 class Result()
{

    var artworkUrl100: String? = null
    var trackName: String? = null
    var artistName: String? = null
    var collectionName: String? = null
    var previewUrl: String? = null

}