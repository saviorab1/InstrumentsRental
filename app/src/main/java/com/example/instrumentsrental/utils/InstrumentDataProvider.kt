package com.example.instrumentsrental.utils

import com.example.instrumentsrental.R
import com.example.instrumentsrental.model.Instrument

object InstrumentDataProvider {
    
    private val instruments = mapOf(
        "Piano" to Instrument(
            "Steinway Piano",
            R.drawable.piano,
            250,
            "Steinway grand piano perfect for Green Book's performances",
            4.9f
        ),
        "Guitar" to Instrument(
            "Black Strat Guitar",
            R.drawable.guitar,
            125,
            "Black Strat Guitar inherited from Pink Floyd!",
            4.7f
        ),
        "Flute" to Instrument(
            "Brannet-Cooper Flute",
            R.drawable.flute,
            100,
            "Brannet-Cooper flute with pure gold plated!",
            4.5f
        ),
        "Saxophone" to Instrument(
            "Grafton Saxophone",
            R.drawable.saxophone,
            75,
            "You have to sell your kidney to compensate for the damage to this saxophone!",
            4.2f
        ),
        "Trumpet" to Instrument(
            "Stradivarius Trumpet",
            R.drawable.trumpet,
            50,
            "Bach Stradivarius trumpet that can blow dollar signs $$$",
            4.0f
        )
    )
    
    fun getCategories(): List<String> {
        return instruments.keys.toList()
    }
    
    fun getInstrumentByCategory(category: String): Instrument {
        return instruments[category] ?: instruments["Piano"]!!
    }
} 