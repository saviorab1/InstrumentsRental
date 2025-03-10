package com.example.instrumentsrental

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.Menu
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.instrumentsrental.model.RentalDetails
import com.example.instrumentsrental.utils.CreditsManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.util.Log

/**
 * UserInfoActivity - Collects and validates user information for instrument rental
 */
class UserInfoActivity : AppCompatActivity() {
    
    private val TAG = "UserInfoActivity"
    
    // UI components - Instrument details
    private lateinit var instrumentNameTextView: TextView
    private lateinit var instrumentImageView: ImageView
    private lateinit var instrumentDescriptionTextView: TextView
    private lateinit var rentalDetailsTextView: TextView
    private lateinit var totalPriceTextView: TextView
    private lateinit var availableBalanceTextView: TextView
    
    // UI components - Input fields
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var addressInputLayout: TextInputLayout
    private lateinit var phoneInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var nameEditText: TextInputEditText
    private lateinit var addressEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    
    // UI components - Buttons
    private lateinit var cancelButton: Button
    private lateinit var confirmButton: Button
    
    // Data
    private lateinit var rentalDetails: RentalDetails
    private lateinit var creditsManager: CreditsManager
    private var creditsMenuItem: MenuItem? = null
    
    /**
     * Initializes the activity, loads rental details, and sets up UI components
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Initializing UserInfoActivity")
        setContentView(R.layout.activity_user_info)
        
        // Set activity title and enable back button
        title = "MUSICLEASE"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // Initialize credits manager
        creditsManager = CreditsManager(this)
        
        // Initialize UI elements
        initializeViews()
        
        // Get rental details from intent
        val rentalDetailsFromIntent = intent.getParcelableExtra<RentalDetails>("RENTAL_DETAILS")
        if (rentalDetailsFromIntent == null) {
            Log.e(TAG, "onCreate: RentalDetails is null")
            Toast.makeText(this, "Error loading rental details", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        rentalDetails = rentalDetailsFromIntent
        
        // Setup UI components
        setupValidation()
        populateRentalDetails()
        setupButtons()
    }
    
    /**
     * Connects layout elements to their respective variables
     */
    private fun initializeViews() {
        Log.d(TAG, "initializeViews: Binding UI elements")
        
        // Instrument details views
        instrumentNameTextView = findViewById(R.id.instrumentNameTextView)
        instrumentImageView = findViewById(R.id.instrumentImageView)
        instrumentDescriptionTextView = findViewById(R.id.instrumentDescriptionTextView)
        rentalDetailsTextView = findViewById(R.id.rentalDetailsTextView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        availableBalanceTextView = findViewById(R.id.availableBalanceTextView)
        
        // Input layouts and fields
        nameInputLayout = findViewById(R.id.nameInputLayout)
        addressInputLayout = findViewById(R.id.addressInputLayout)
        phoneInputLayout = findViewById(R.id.phoneInputLayout)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        nameEditText = findViewById(R.id.nameEditText)
        addressEditText = findViewById(R.id.addressEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        emailEditText = findViewById(R.id.emailEditText)
        
        // Buttons
        cancelButton = findViewById(R.id.cancelButton)
        confirmButton = findViewById(R.id.confirmButton)
    }
    
    /**
     * Sets up real-time validation for input fields
     */
    private fun setupValidation() {
        // Name validation
        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                nameInputLayout.error = if (s.toString().trim().isEmpty()) "Name is required" else null
            }
        })
        
        // Address validation
        addressEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                addressInputLayout.error = if (s.toString().trim().isEmpty()) "Address is required" else null
            }
        })
        
        // Phone validation
        phoneEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val phone = s.toString().trim()
                phoneInputLayout.error = when {
                    phone.isEmpty() -> "Phone number is required"
                    !phone.matches(Regex("^[0-9]+$")) -> "Phone must contain numbers only"
                    phone.length < 10 -> "Phone must be at least 10 digits"
                    else -> null
                }
            }
        })
        
        // Email validation
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                emailInputLayout.error = when {
                    email.isEmpty() -> "Email is required"
                    !email.contains('@') || email.indexOf('@') <= 0 || email.indexOf('@') == email.length - 1 -> 
                        "Email must contain @ symbol in the middle"
                    else -> null
                }
            }
        })
    }
    
    /**
     * Fills the UI with rental details from the intent
     */
    private fun populateRentalDetails() {
        Log.d(TAG, "populateRentalDetails: Setting up UI with rental details")
        
        try {
            // Display instrument information
            instrumentNameTextView.text = rentalDetails.instrumentName
            instrumentImageView.setImageResource(rentalDetails.instrumentImageRes)
            instrumentDescriptionTextView.text = rentalDetails.instrumentDescription
            
            // Display rental and price information
            rentalDetailsTextView.text = "Rental Period: ${rentalDetails.rentalPeriod}"
            totalPriceTextView.text = "$${rentalDetails.totalPrice} credits"
            
            // Display available balance
            val availableBalance = creditsManager.getCreditsBalance()
            availableBalanceTextView.text = "$${availableBalance} credits"
            
        } catch (e: Exception) {
            Log.e(TAG, "populateRentalDetails: Error setting up UI", e)
            Toast.makeText(this, "Error displaying rental details", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Sets up button click listeners
     */
    private fun setupButtons() {
        // Cancel button - return to previous screen
        cancelButton.setOnClickListener {
            Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show()
            finish()
        }
        
        // Confirm button - validate inputs and show confirmation
        confirmButton.setOnClickListener {
            if (validateInputs()) {
                showConfirmationDialog()
            }
        }
    }
    
    /**
     * Handles action bar item selections
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show()
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
    /**
     * Validates all input fields before proceeding
     */
    private fun validateInputs(): Boolean {
        val name = nameEditText.text.toString().trim()
        val address = addressEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        
        // Check name
        if (name.isEmpty()) {
            nameInputLayout.error = "Name is required"
            return false
        }
        
        // Check address
        if (address.isEmpty()) {
            addressInputLayout.error = "Address is required"
            return false
        }
        
        // Check phone
        if (phone.isEmpty()) {
            phoneInputLayout.error = "Phone number is required"
            return false
        } else if (!phone.matches(Regex("^[0-9]+$"))) {
            phoneInputLayout.error = "Phone must contain numbers only"
            return false
        } else if (phone.length < 10) {
            phoneInputLayout.error = "Phone must be at least 10 digits"
            return false
        }
        
        // Check email
        if (email.isEmpty()) {
            emailInputLayout.error = "Email is required"
            return false
        } else if (!email.contains('@') || email.indexOf('@') <= 0 || email.indexOf('@') == email.length - 1) {
            emailInputLayout.error = "Email must contain @ symbol in the middle"
            return false
        }
        
        // All inputs are valid
        return true
    }
    
    /**
     * Shows dialog with summary of rental information for final confirmation
     */
    private fun showConfirmationDialog() {
        val name = nameEditText.text.toString().trim()
        val address = addressEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        
        val message = """
            Instrument: ${rentalDetails.instrumentName}
            
            Rental Period: ${rentalDetails.rentalPeriod}
            Total Price: $${rentalDetails.totalPrice} credits
            
            Your Details:
            Name: $name
            Address: $address
            Phone: $phone
            Email: $email
            
            Confirm this rental?
        """.trimIndent()
        
        AlertDialog.Builder(this, R.style.AppDialogTheme)
            .setTitle("Confirm Rental")
            .setMessage(message)
            .setPositiveButton("Confirm") { _, _ -> processRental() }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    /**
     * Processes the rental transaction by deducting credits
     */
    private fun processRental() {
        try {
            val currentBalance = creditsManager.getCreditsBalance()
            val newBalance = currentBalance - rentalDetails.totalPrice
            
            // Update credits balance
            creditsManager.setCreditsBalance(newBalance)
            
            // Update the displayed balance
            updateCreditsDisplay()
            
            // Show success message
            AlertDialog.Builder(this, R.style.AppDialogTheme)
                .setTitle("Rental Successful!")
                .setMessage("You have successfully rented a ${rentalDetails.instrumentName} for ${rentalDetails.rentalPeriod}.\n\n" +
                        "Amount charged: $${rentalDetails.totalPrice} credits\n" +
                        "Remaining balance: $$newBalance credits")
                .setPositiveButton("OK") { _, _ ->
                    setResult(RESULT_OK)
                    finish()
                }
                .setCancelable(false)
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "processRental: Error processing rental", e)
            Toast.makeText(this, "Error processing rental", Toast.LENGTH_LONG).show()
        }
    }
    
    /**
     * Creates the options menu with credits display
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        creditsMenuItem = menu.findItem(R.id.action_credits)
        
        // Style the credits display
        val spanString = android.text.SpannableString(creditsMenuItem?.title)
        spanString.setSpan(android.text.style.ForegroundColorSpan(android.graphics.Color.parseColor("#4CAF50")), 
            0, spanString.length, 0)
        spanString.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 
            0, spanString.length, 0)
        creditsMenuItem?.title = spanString
        
        updateCreditsDisplay()
        return true
    }
    
    /**
     * Updates the credits display in the action bar and in the UI
     */
    private fun updateCreditsDisplay() {
        val credits = creditsManager.getCreditsBalance()
        
        // Update menu item
        creditsMenuItem?.let { menuItem ->
            val creditText = "$${credits}"
            val spannableString = android.text.SpannableString(creditText)
            menuItem.title = spannableString
        }
        
        // Update balance in UI
        availableBalanceTextView.text = "$${credits} credits"
    }
    
    /**
     * Updates credits display when returning to activity
     */
    override fun onResume() {
        super.onResume()
        updateCreditsDisplay()
    }
    
    /**
     * Handles back button navigation
     */
    override fun onSupportNavigateUp(): Boolean {
        Log.d(TAG, "onSupportNavigateUp: Navigating back")
        onBackPressed()
        return true
    }
} 