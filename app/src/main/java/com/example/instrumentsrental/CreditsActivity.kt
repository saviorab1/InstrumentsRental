package com.example.instrumentsrental

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.instrumentsrental.utils.CreditsManager

class CreditsActivity : AppCompatActivity() {

    private lateinit var creditsBalanceTextView: TextView
    private lateinit var addCreditsEditText: EditText
    private lateinit var addCreditsButton: Button
    private lateinit var creditsManager: CreditsManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)
        
        // Enable the back button in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        
        // Initialize views
        creditsBalanceTextView = findViewById(R.id.creditsBalanceTextView)
        addCreditsEditText = findViewById(R.id.addCreditsEditText)
        addCreditsButton = findViewById(R.id.addCreditsButton)
        
        // Initialize credits manager
        creditsManager = CreditsManager(this)
        
        // Display current balance
        updateCreditsDisplay()
        
        // Set up click listeners
        addCreditsButton.setOnClickListener {
            addCredits()
        }
    }
    
    // Handle action bar back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
    private fun updateCreditsDisplay() {
        val balance = creditsManager.getCreditsBalance()
        creditsBalanceTextView.text = "Current Balance: $$balance"
    }
    
    private fun addCredits() {
        val amountText = addCreditsEditText.text.toString()
        if (amountText.isNotEmpty()) {
            try {
                val amountToAdd = amountText.toInt()
                if (amountToAdd > 0) {
                    val newBalance = creditsManager.addCredits(amountToAdd)
                    updateCreditsDisplay()
                    Toast.makeText(this, "$amountToAdd credits added! New balance: $$newBalance", Toast.LENGTH_SHORT).show()
                    addCreditsEditText.text.clear()
                } else {
                    Toast.makeText(this, "Please enter a positive number", Toast.LENGTH_SHORT).show()
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 