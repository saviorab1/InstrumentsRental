package com.example.instrumentsrental.model

data class Instrument(
    val name: String,
    val imageRes: Int,
    val price: Int,
    val description: String,
    val rating: Float  // Rating from 0-5
) 