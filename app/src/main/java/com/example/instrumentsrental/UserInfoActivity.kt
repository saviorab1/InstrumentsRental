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

/**
 * UserInfoActivity - Handles user information collection for instrument rental.
 * Collects and validates user details before finalizing the rental transaction.
 */
class UserInfoActivity : AppCompatActivity() {
    
    // UI components - Instrument details
    private lateinit var instrumentNameTextView: TextView
    private lateinit var instrumentImageView: ImageView
    private lateinit var descriptionTextView: TextView
    private lateinit var rentalDetailsTextView: TextView
    
    // UI components - Input layouts
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var addressInputLayout: TextInputLayout
    private lateinit var phoneInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    
    // UI components - Input fields
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
    
    // Reference to menu item
    private var creditsMenuItem: MenuItem? = null
    
    /**
     * Initializes the activity, sets up the UI components, and loads rental details.
     */
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
    
    /**
     * Initialize all UI views from layout. Binds layout elements to properties.
     */
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
    
    /**
     * Set up validation for all input fields.
     * Configures real-time validation as the user types.
     */
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
        
        // Validate phone as user types
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
    
    /**
     * Populate the UI with rental details.
     * Displays instrument information retrieved from the intent.
     */
    private fun populateRentalDetails() {
        instrumentNameTextView.text = rentalDetails.instrumentName
        instrumentImageView.setImageResource(rentalDetails.instrumentImageRes)
        descriptionTextView.text = rentalDetails.instrumentDescription
        
        // Format rental details text
        rentalDetailsTextView.text = getString(
            R.string.rental_details_format, 
            rentalDetails.rentalPeriod, 
            rentalDetails.totalPrice
        )
    }
    
    /**
     * Set up button click listeners.
     * Configures actions for cancel and confirm buttons.
     */
    private fun setupButtons() {
        cancelButton.setOnClickListener {
            Toast.makeText(this, R.string.message_booking_cancelled, Toast.LENGTH_SHORT).show()
            finish()
        }
        
        confirmButton.setOnClickListener {
            if (validateInputs()) {
                showConfirmationDialog()
            }
        }
    }
    
    /**
     * Handle action bar back button.
     * Processes navigation events, including back button press.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            Toast.makeText(this, R.string.message_booking_cancelled, Toast.LENGTH_SHORT).show()
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
    /**
     * Validate all input fields before proceeding.
     * Performs comprehensive validation of all user inputs.
     * 
     * @return true if all inputs are valid, false otherwise
     */
    private fun validateInputs(): Boolean {
        val name = nameEditText.text.toString().trim()
        val address = addressEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        
        var isNameValid = false
        var isAddressValid = false
        var isPhoneValid = false
        var isEmailValid = false
        
        // Validate name
        if (name.isNotEmpty()) {
            isNameValid = true
            nameInputLayout.error = null
        } else {
            nameInputLayout.error = "Name is required"
        }
        
        // Validate address
        if (address.isNotEmpty()) {
            isAddressValid = true
            addressInputLayout.error = null
        } else {
            addressInputLayout.error = "Address is required"
        }
        
        // Validate phone (numbers only, 10+ digits)
        if (phone.isNotEmpty() && phone.matches(Regex("^[0-9]+$")) && phone.length >= 10) {
            isPhoneValid = true
            phoneInputLayout.error = null
        } else {
            if (phone.isEmpty()) {
                phoneInputLayout.error = "Phone number is required"
            } else if (!phone.matches(Regex("^[0-9]+$"))) {
                phoneInputLayout.error = "Phone must contain numbers only"
            } else {
                phoneInputLayout.error = "Phone must be at least 10 digits"
            }
        }
        
        // Validate email (must have @ in the middle)
        if (email.isNotEmpty() && email.contains('@') && 
            email.indexOf('@') > 0 && email.indexOf('@') < email.length - 1) {
            isEmailValid = true
            emailInputLayout.error = null
        } else {
            if (email.isEmpty()) {
                emailInputLayout.error = "Email is required"
            } else {
                emailInputLayout.error = "Email must contain @ symbol in the middle"
            }
        }
        
        return isNameValid && isAddressValid && isPhoneValid && isEmailValid
    }
    
    /**
     * Show rental confirmation dialog.
     * Displays a summary of rental and user information for final confirmation.
     */
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
            .setTitle(R.string.confirm_rental_title)
            .setMessage(message)
            .setPositiveButton(R.string.action_confirm) { _, _ ->
                processRental()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }
    
    /**
     * Process the rental transaction.
     * Updates credits balance and displays success message.
     */
    private fun processRental() {
        val currentBalance = creditsManager.getCreditsBalance()
        val newBalance = currentBalance - rentalDetails.totalPrice
        
        // Update credits balance
        creditsManager.setCreditsBalance(newBalance)
        
        // Update the displayed balance
        updateCreditsDisplay()
        
        // Show success message
        AlertDialog.Builder(this)
            .setTitle(R.string.rental_success_title)
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
    
    /**
     * Set up the credits menu item.
     * Creates the action bar menu with credits balance.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        creditsMenuItem = menu.findItem(R.id.action_credits)
        updateCreditsDisplay() // Set initial credits value
        return true
    }
    
    /**
     * Updates the credits balance in the menu item.
     * Shows current credit balance in the action bar.
     */
    private fun updateCreditsDisplay() {
        // Update the menu item text if available
        creditsMenuItem?.title = "$${creditsManager.getCreditsBalance()}"
    }
    
    /**
     * Updates credits display when returning to activity.
     * Called when the activity returns to foreground.
     */
    override fun onResume() {
        super.onResume()
        updateCreditsDisplay()
    }
} 