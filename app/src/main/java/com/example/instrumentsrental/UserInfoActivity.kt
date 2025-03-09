package com.example.instrumentsrental

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.MenuItem
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

class UserInfoActivity : AppCompatActivity() {
    
    private lateinit var instrumentNameTextView: TextView
    private lateinit var instrumentImageView: ImageView
    private lateinit var descriptionTextView: TextView
    private lateinit var rentalDetailsTextView: TextView
    
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var addressInputLayout: TextInputLayout
    private lateinit var phoneInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    
    private lateinit var nameEditText: TextInputEditText
    private lateinit var addressEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    
    private lateinit var cancelButton: Button
    private lateinit var confirmButton: Button
    
    private lateinit var rentalDetails: RentalDetails
    private lateinit var creditsManager: CreditsManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        
        // Enable the back button in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        
        // Get rental details from intent
        rentalDetails = intent.getParcelableExtra("RENTAL_DETAILS") ?: 
            throw IllegalArgumentException("No rental details provided")
        
        // Initialize credits manager
        creditsManager = CreditsManager(this)
        
        initializeViews()
        setupValidation()
        populateRentalDetails()
        setupButtons()
    }
    
    private fun initializeViews() {
        instrumentNameTextView = findViewById(R.id.instrumentNameTextView)
        instrumentImageView = findViewById(R.id.instrumentImageView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        rentalDetailsTextView = findViewById(R.id.rentalDetailsTextView)
        
        nameInputLayout = findViewById(R.id.nameInputLayout)
        addressInputLayout = findViewById(R.id.addressInputLayout)
        phoneInputLayout = findViewById(R.id.phoneInputLayout)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        
        nameEditText = findViewById(R.id.nameEditText)
        addressEditText = findViewById(R.id.addressEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        emailEditText = findViewById(R.id.emailEditText)
        
        cancelButton = findViewById(R.id.cancelButton)
        confirmButton = findViewById(R.id.confirmButton)
    }
    
    private fun setupValidation() {
        // Validate name as user types
        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().trim().isEmpty()) {
                    nameInputLayout.error = "Name is required"
                } else {
                    nameInputLayout.error = null
                }
            }
        })
        
        // Validate address as user types
        addressEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().trim().isEmpty()) {
                    addressInputLayout.error = "Address is required"
                } else {
                    addressInputLayout.error = null
                }
            }
        })
        
        // Validate phone as user types - ensure numbers only and min 10 digits
        phoneEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val phone = s.toString().trim()
                if (phone.isEmpty()) {
                    phoneInputLayout.error = "Phone number is required"
                } else if (!phone.matches(Regex("^[0-9]+$"))) {
                    phoneInputLayout.error = "Phone must contain numbers only"
                } else if (phone.length < 10) {
                    phoneInputLayout.error = "Phone must be at least 10 digits"
                } else {
                    phoneInputLayout.error = null
                }
            }
        })
        
        // Validate email as user types - ensure it has @ character
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                if (email.isEmpty()) {
                    emailInputLayout.error = "Email is required"
                } else if (!email.contains('@') || email.indexOf('@') <= 0 || 
                           email.indexOf('@') == email.length - 1) {
                    emailInputLayout.error = "Email must contain @ symbol in the middle"
                } else {
                    emailInputLayout.error = null
                }
            }
        })
    }
    
    private fun populateRentalDetails() {
        instrumentNameTextView.text = rentalDetails.instrumentName
        instrumentImageView.setImageResource(rentalDetails.instrumentImageRes)
        descriptionTextView.text = rentalDetails.instrumentDescription
        rentalDetailsTextView.text = "Rental: ${rentalDetails.rentalPeriod} for ${rentalDetails.totalPrice} credits"
    }
    
    private fun setupButtons() {
        cancelButton.setOnClickListener {
            Toast.makeText(this, R.string.message_booking_cancelled, Toast.LENGTH_SHORT).show()
            finish()
        }
        
        confirmButton.setOnClickListener {
            if (validateAllFields()) {
                showConfirmationDialog()
            } else {
                Toast.makeText(this, "Please fix the errors before proceeding", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    // Handle action bar back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            Toast.makeText(this, R.string.message_booking_cancelled, Toast.LENGTH_SHORT).show()
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
    private fun validateAllFields(): Boolean {
        val name = nameEditText.text.toString().trim()
        val address = addressEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        
        val isNameValid = name.isNotEmpty()
        val isAddressValid = address.isNotEmpty()
        val isPhoneValid = phone.matches(Regex("^[0-9]+$")) && phone.length >= 10
        val isEmailValid = email.contains('@') && email.indexOf('@') > 0 && 
                           email.indexOf('@') < email.length - 1
        
        // Re-show errors if validation fails
        if (!isNameValid) nameInputLayout.error = "Name is required"
        if (!isAddressValid) addressInputLayout.error = "Address is required"
        
        if (!isPhoneValid) {
            if (phone.isEmpty()) {
                phoneInputLayout.error = "Phone number is required"
            } else if (!phone.matches(Regex("^[0-9]+$"))) {
                phoneInputLayout.error = "Phone must contain numbers only"
            } else {
                phoneInputLayout.error = "Phone must be at least 10 digits"
            }
        }
        
        if (!isEmailValid) {
            if (email.isEmpty()) {
                emailInputLayout.error = "Email is required"
            } else {
                emailInputLayout.error = "Email must contain @ symbol in the middle"
            }
        }
        
        return isNameValid && isAddressValid && isPhoneValid && isEmailValid
    }
    
    private fun showConfirmationDialog() {
        val name = nameEditText.text.toString().trim()
        val address = addressEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        
        val message = """
            Instrument: ${rentalDetails.instrumentName}
            
            Rental Period: ${rentalDetails.rentalPeriod}
            Total Price: ${rentalDetails.totalPrice} credits
            
            Your Details:
            Name: $name
            Address: $address
            Phone: $phone
            Email: $email
            
            Confirm this rental?
        """.trimIndent()
        
        AlertDialog.Builder(this)
            .setTitle("Confirm Rental")
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                processRental()
            }
            .setNegativeButton("No", null)
            .show()
    }
    
    private fun processRental() {
        val currentBalance = creditsManager.getCreditsBalance()
        val newBalance = currentBalance - rentalDetails.totalPrice
        
        // Update credits balance
        creditsManager.setCreditsBalance(newBalance)
        
        // Show success message
        AlertDialog.Builder(this)
            .setTitle("Rental Successful!")
            .setMessage("You have successfully rented a ${rentalDetails.instrumentName} for ${rentalDetails.rentalPeriod}.\n\n" +
                    "Amount charged: ${rentalDetails.totalPrice} credits\n" +
                    "Remaining balance: $newBalance credits")
            .setPositiveButton("OK") { _, _ ->
                setResult(RESULT_OK)
                finish()
            }
            .setCancelable(false)
            .show()
    }
} 