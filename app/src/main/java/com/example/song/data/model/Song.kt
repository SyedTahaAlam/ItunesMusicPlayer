package com.example.song.data.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
 class Song(){
    var resultCount: Int? = null
    var Results: ArrayList<Result>?  = null
}