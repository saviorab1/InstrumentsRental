package com.example.instrumentsrental

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.instrumentsrental.model.Instrument
import com.example.instrumentsrental.utils.CreditsManager
import com.example.instrumentsrental.utils.InstrumentDataProvider

class MainActivity : AppCompatActivity() {
    
    // UI components
    private lateinit var instrumentSpinner: Spinner
    private lateinit var instrumentImage: ImageView
    private lateinit var instrumentName: TextView
    private lateinit var ratingContainer: LinearLayout
    private lateinit var periodGroup: RadioGroup
    private lateinit var weeklyRadioButton: RadioButton
    private lateinit var monthlyRadioButton: RadioButton
    private lateinit var quantityLabel: TextView
    private lateinit var quantityEditText: EditText
    private lateinit var totalPriceTextView: TextView
    private lateinit var pricePerPeriodTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var cancelButton: Button
    private lateinit var borrowButton: Button
    
    // Add credits manager
    private lateinit var creditsManager: CreditsManager
    
    // State variables
    private var currentInstrument: Instrument? = null
    private var isMonthly = false
    private var quantity = 1
    
    // Reference to menu item
    private var creditsMenuItem: MenuItem? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize credits manager
        creditsManager = CreditsManager(this)
        
        // Initialize views
        initializeViews()
        setupListeners()
    }
    
    /**
     * Initialize all UI views from layout
     */
    private fun initializeViews() {
        instrumentSpinner = findViewById(R.id.instrumentSpinner)
        instrumentImage = findViewById(R.id.instrumentImage)
        instrumentName = findViewById(R.id.instrumentName)
        ratingContainer = findViewById(R.id.ratingContainer)
        periodGroup = findViewById(R.id.periodChipGroup)
        weeklyRadioButton = findViewById(R.id.weeklyChip)
        monthlyRadioButton = findViewById(R.id.monthlyChip)
        quantityLabel = findViewById(R.id.quantityLabel)
        quantityEditText = findViewById(R.id.quantityEditText)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        pricePerPeriodTextView = findViewById(R.id.pricePerPeriodTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        cancelButton = findViewById(R.id.cancelButton)
        borrowButton = findViewById(R.id.borrowButton)
    }
    
    /**
     * Set up all event listeners
     */
    private fun setupListeners() {
        setupSpinner()
        setupPeriodRadioButtons()
        setupQuantityInput()
        
        cancelButton.setOnClickListener {
            resetToDefaults()
        }
        
        borrowButton.setOnClickListener {
            processBorrowing()
        }
    }
    
    /**
     * Updates credits display when returning to activity
     */
    override fun onResume() {
        super.onResume()
        // Update credits display when returning to this activity
        updateCreditsDisplay()
    }
    
    /**
     * Updates the credits balance in the menu item
     */
    private fun updateCreditsDisplay() {
        // Update the menu item text if available
        creditsMenuItem?.title = "$${creditsManager.getCreditsBalance()}"
    }
    
    /**
     * Set up the instrument category spinner with data and listener
     */
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
    
    /**
     * Set up weekly/monthly radio button group
     */
    private fun setupPeriodRadioButtons() {
        periodGroup.setOnCheckedChangeListener { _, checkedId ->
            isMonthly = checkedId == R.id.monthlyChip
            updateQuantityLabel()
            updatePriceDisplay()
        }
    }
    
    /**
     * Set up quantity input field with validation
     */
    private fun setupQuantityInput() {
        quantityEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                try {
                    val value = s.toString().toInt()
                    if (value in 1..99) {
                        quantity = value
                        updatePriceDisplay()
                    } else if (value > 99) {
                        quantityEditText.setText("99")
                        quantityEditText.setSelection(2)
                    }
                } catch (e: NumberFormatException) {
                    if (s.toString().isEmpty()) {
                        quantity = 1
                        updatePriceDisplay()
                    }
                }
            }
        })
    }
    
    /**
     * Update the UI with details of the selected instrument
     */
    private fun updateInstrumentDetails(category: String) {
        val instrument = InstrumentDataProvider.getInstrumentByCategory(category)
        currentInstrument = instrument
        
        // Update UI with instrument details in the correct order
        instrumentName.text = instrument.name
        descriptionTextView.text = instrument.description
        setupRatingBar(instrument.rating)
        instrumentImage.setImageResource(instrument.imageRes)
        
        updatePriceDisplay()
    }
    
    /**
     * Create dynamic rating stars based on instrument rating
     */
    private fun setupRatingBar(rating: Float) {
        // Clear existing stars
        ratingContainer.removeAllViews()
        
        // Add stars based on rating
        val roundedRating = kotlin.math.round(rating).toInt()
        for (i in 1..5) {
            val isFilled = i <= roundedRating
            val starImageView = ImageView(this).apply {
                setImageResource(R.drawable.star)
                layoutParams = LinearLayout.LayoutParams(120, 120)
                alpha = if (isFilled) 1f else 0.3f
            }
            ratingContainer.addView(starImageView)
        }
        
        // Add rating text
        val ratingText = TextView(this).apply {
            text = String.format("%.1f", rating)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(20, 0, 0, 0)
            textSize = 24f
        }
        ratingContainer.addView(ratingText)
    }
    
    /**
     * Update the quantity label text based on selected period
     */
    private fun updateQuantityLabel() {
        quantityLabel.text = "Number of ${if (isMonthly) "months" else "weeks"}:"
    }
    
    /**
     * Calculate and display prices based on current selections
     */
    private fun updatePriceDisplay() {
        val instrument = currentInstrument ?: return
        
        val basePrice = if (isMonthly) instrument.price * 4 else instrument.price
        val totalPrice = basePrice * quantity
        
        totalPriceTextView.text = "$totalPrice credits total"
        pricePerPeriodTextView.text = "(${basePrice} credits per ${if (isMonthly) "month" else "week"})"
    }

    /**
     * Set up the credits menu item
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        creditsMenuItem = menu.findItem(R.id.action_credits)
        updateCreditsDisplay() // Set initial credits value
        return true
    }

    /**
     * Handle menu item selections
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_credits -> {
                // Launch the Credits Activity
                startActivity(Intent(this, CreditsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Reset all selections to default values
     */
    private fun resetToDefaults() {
        // Reset to first item in spinner
        instrumentSpinner.setSelection(0)
        
        // Reset to weekly rental
        weeklyRadioButton.isChecked = true
        
        // Reset quantity to 1
        quantityEditText.setText("1")
        
        // Show confirmation
        Toast.makeText(this, "Selection reset", Toast.LENGTH_SHORT).show()
    }

    /**
     * Process the borrowing transaction
     */
    private fun processBorrowing() {
        val instrument = currentInstrument ?: return
        val totalPrice = if (isMonthly) {
            instrument.price * 4 * quantity
        } else {
            instrument.price * quantity
        }
        
        val currentBalance = creditsManager.getCreditsBalance()
        
        if (totalPrice > currentBalance) {
            // Show a dialog asking if they want to add more credits
            AlertDialog.Builder(this)
                .setTitle("Insufficient Credits")
                .setMessage("You need $totalPrice credits but only have $currentBalance. Would you like to add more credits?")
                .setPositiveButton("Add Credits") { _, _ ->
                    // Open credits activity
                    startActivity(Intent(this, CreditsActivity::class.java))
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            // Sufficient credits, process the rental
            val remainingBalance = currentBalance - totalPrice
            creditsManager.setCreditsBalance(remainingBalance)
            updateCreditsDisplay()
            
            // Show success toast with correct name reference
            val rentalPeriod = if (isMonthly) {
                "$quantity ${if (quantity > 1) "months" else "month"}"
            } else {
                "$quantity ${if (quantity > 1) "weeks" else "week"}"
            }
            
            Toast.makeText(
                this,
                "Success! Rented ${instrument.name} for $rentalPeriod ($totalPrice credits). Remaining balance: $remainingBalance",
                Toast.LENGTH_LONG
            ).show()
            
            // Reset after successful rental (auto-reset after delay)
            Handler(Looper.getMainLooper()).postDelayed({
                resetToDefaults()
            }, 2000)
        }
    }
}