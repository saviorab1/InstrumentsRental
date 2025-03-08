package com.example.instrumentsrental.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.instrumentsrental.R

@Composable
fun DisplayInstrument(category: String) {
    // Get the appropriate image resource based on the category
    val (name, imageRes, price, description) = when (category.lowercase()) {
        "piano" -> Quadruple(
            "Steinway Piano",
            R.drawable.piano, 
            999,
            "Steinway grand piano perfect for Green Book's performances"
        )
        "guitar" -> Quadruple(
            "Black Strat Guitar",
            R.drawable.guitar, 
            500,
            "Black Strat Guitar inherited from Pink Floyd!"
        )
        "flute" -> Quadruple(
            "Brannet-Cooper Flute",
            R.drawable.flute, 
            400,
            "Brannet-Cooper flute with pure gold plated!"
        )
        "saxophone" -> Quadruple(
            "Grafton Saxophone",
            R.drawable.saxophone, 
            300,
            "You have to sell your kidney to compensate for the damage to this saxophone!"
        )
        "trumpet" -> Quadruple(
            "Stradivarius Trumpet",
            R.drawable.trumpet, 
            200, 
            "Bach Stradivarius trumpet that can blow dollar signs $$$"
        )
        else -> Quadruple(
            "Concert Piano", 
            R.drawable.piano, 
            999,
            "Steinway grand piano perfect for classical performances"
        )
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        // Display the instrument image
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = name,
            modifier = Modifier
                .size(200.dp)
                .padding(8.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Instrument name
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Price
        Text(
            text = "$price credits per month",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Description
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// Helper class to return multiple values
data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D) 