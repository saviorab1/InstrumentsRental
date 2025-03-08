package com.example.instrumentsrental

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.instrumentsrental.model.Instrument
import com.example.instrumentsrental.utils.InstrumentDataProvider

class MainActivity : AppCompatActivity() {
    
    private lateinit var instrumentSpinner: Spinner
    private lateinit var instrumentImage: ImageView
    private lateinit var instrumentName: TextView
    private lateinit var priceTextView: TextView
    private lateinit var descriptionTextView: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize views
        instrumentSpinner = findViewById(R.id.instrumentSpinner)
        instrumentImage = findViewById(R.id.instrumentImage)
        instrumentName = findViewById(R.id.instrumentName)
        priceTextView = findViewById(R.id.priceTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        
        setupSpinner()
    }
    
    private fun setupSpinner() {
        val categories = InstrumentDataProvider.getCategories()
        
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        
        instrumentSpinner.adapter = adapter
        
        instrumentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
                updateInstrumentDetails(selectedCategory)
            }
            
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }
    
    private fun updateInstrumentDetails(category: String) {
        val instrument = InstrumentDataProvider.getInstrumentByCategory(category)
        
        instrumentImage.setImageResource(instrument.imageRes)
        instrumentName.text = instrument.name
        priceTextView.text = "${instrument.price} credits per month"
        descriptionTextView.text = instrument.description
    }
}