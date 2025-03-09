package com.example.instrumentsrental

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.instrumentsrental.utils.CreditsManager
import com.google.android.material.textfield.TextInputEditText

/**
 * CreditsActivity - Handles credit management for the app.
 * Allows users to view and add credits to their account.
 */
class CreditsActivity : AppCompatActivity() {

    private val TAG = "CreditsActivity"
    
    // UI components
    private lateinit var currentCreditsTextView: TextView
    private lateinit var creditsEditText: TextInputEditText
    private lateinit var addCreditsButton: Button
    
    // Credits management
    private lateinit var creditsManager: CreditsManager
    
    // Reference to menu item
    private var creditsMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Initializing CreditsActivity")
        setContentView(R.layout.activity_credits)
        
        // Set activity title
        title = "MUSICLEASE"
        
        // Display back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // Initialize credits manager
        creditsManager = CreditsManager(this)
        
        // Initialize views
        initializeViews()
        
        // Update displayed credits
        updateCreditsDisplay()
        
        // Set up button listeners
        setupButtons()
    }
    
    /**
     * Initialize all UI views from layout. Binds layout elements to properties.
     */
    private fun initializeViews() {
        Log.d(TAG, "initializeViews: Binding UI elements")
        currentCreditsTextView = findViewById(R.id.currentCreditsTextView)
        creditsEditText = findViewById(R.id.creditsEditText)
        addCreditsButton = findViewById(R.id.addCreditsButton)
    }
    
    /**
     * Set up button click listeners. Configures actions for credit buttons.
     */
    private fun setupButtons() {
        addCreditsButton.setOnClickListener {
            val amountText = creditsEditText.text.toString().trim()
            if (amountText.isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            try {
                val amount = amountText.toInt()
                if (amount <= 0) {
                    Toast.makeText(this, "Amount must be greater than zero", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                
                addCredits(amount)
                creditsEditText.setText("")
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * Add specified amount of credits to the account.
     * Updates balance and refreshes the display.
     * 
     * @param amount The amount of credits to add
     */
    private fun addCredits(amount: Int) {
        val currentBalance = creditsManager.getCreditsBalance()
        val newBalance = currentBalance + amount
        
        // Update credits balance
        creditsManager.setCreditsBalance(newBalance)
        
        // Update display
        updateCreditsDisplay()
        
        // Show confirmation message
        Toast.makeText(
            this,
            "$${amount} credits added successfully. New balance: $${newBalance}",
            Toast.LENGTH_SHORT
        ).show()
    }
    
    /**
     * Updates the credits display in both the UI and menu.
     * Shows current credit balance in TextView and action bar.
     */
    private fun updateCreditsDisplay() {
        val credits = creditsManager.getCreditsBalance()
        Log.d(TAG, "updateCreditsDisplay: Current credits balance: $credits")
        
        // Update the current credits TextView
        currentCreditsTextView.text = "$${credits} credits"
        
        // Get the menu item and apply custom styling
        creditsMenuItem?.let { menuItem ->
            // Create a SpannableString to apply custom styling
            val creditText = "$${credits}"
            val spannableString = SpannableString(creditText)
            
            // Set the title with the styled text
            spannableString.setSpan(ForegroundColorSpan(android.graphics.Color.parseColor("#4CAF50")), 
                0, spannableString.length, 0)
            spannableString.setSpan(StyleSpan(android.graphics.Typeface.BOLD), 
                0, spannableString.length, 0)
            menuItem.title = spannableString
        }
    }
    
    /**
     * Inflate options menu with credits display.
     * Creates the action bar menu with credits balance.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        creditsMenuItem = menu.findItem(R.id.action_credits)
        
        // Set the title text color programmatically
        val spanString = SpannableString(creditsMenuItem?.title)
        spanString.setSpan(ForegroundColorSpan(android.graphics.Color.parseColor("#4CAF50")), 
            0, spanString.length, 0)
        spanString.setSpan(StyleSpan(android.graphics.Typeface.BOLD), 
            0, spanString.length, 0)
        creditsMenuItem?.title = spanString
        
        updateCreditsDisplay()
        return true
    }
    
    /**
     * Handle action bar back button.
     * Processes navigation events, including back button press.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
    /**
     * Updates credits display when returning to activity.
     * Called when the activity returns to foreground.
     */
    override fun onResume() {
        super.onResume()
        updateCreditsDisplay()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 