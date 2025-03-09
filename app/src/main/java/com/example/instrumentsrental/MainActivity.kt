package com.example.instrumentsrental

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.instrumentsrental.model.Instrument
import com.example.instrumentsrental.model.RentalDetails
import com.example.instrumentsrental.utils.CreditsManager
import com.example.instrumentsrental.utils.InstrumentDataProvider

/**
 * MainActivity - Main screen of the Instruments Rental application.
 * Allows users to select and configure their instrument rental options.
 */
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
    
    // Credits management
    private lateinit var creditsManager: CreditsManager
    
    // State variables
    private var currentInstrument: Instrument? = null
    private var isMonthly = false
    private var quantity = 1
    
    // Reference to menu item
    private var creditsMenuItem: MenuItem? = null
    
    companion object {
        private const val REQUEST_USER_INFO = 101
    }
    
    /**
     * Initializes the activity, sets up the UI components and listeners.
     */
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
     * Initialize all UI views from layout. Binds layout elements to properties.
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
     * Set up all event listeners for user interactions.
     * Configures spinner, radio buttons, quantity input and action buttons.
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
     * Updates credits display when returning to activity.
     * Called when the activity returns to foreground.
     */
    override fun onResume() {
        super.onResume()
        updateCreditsDisplay()
    }
    
    /**
     * Updates the credits balance in the menu item.
     * Shows current credit balance in the action bar.
     */
    private fun updateCreditsDisplay() {
        creditsMenuItem?.title = "$${creditsManager.getCreditsBalance()}"
    }
    
    /**
     * Set up the instrument category spinner with data and listener.
     * Populates spinner with instrument categories and handles selection changes.
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
     * Set up weekly/monthly radio button group.
     * Handles rental period selection changes between weekly and monthly options.
     */
    private fun setupPeriodRadioButtons() {
        periodGroup.setOnCheckedChangeListener { _, checkedId ->
            isMonthly = checkedId == R.id.monthlyChip
            updateQuantityLabel()
            updatePriceDisplay()
        }
    }
    
    /**
     * Set up quantity input field with validation.
     * Validates and processes user input for rental quantity.
     * Restricts input to 1-99 range and updates price accordingly.
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
     * Update the UI with details of the selected instrument.
     * Populates UI elements with data from the selected instrument.
     * 
     * @param category The selected instrument category
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
     * Create dynamic rating stars based on instrument rating.
     * Generates a visual representation of the instrument's rating.
     * 
     * @param rating The rating value (0-5)
     */
    private fun setupRatingBar(rating: Float) {
        ratingContainer.removeAllViews()
        ratingContainer.gravity = android.view.Gravity.CENTER_VERTICAL
        
        val roundedRating = kotlin.math.round(rating).toInt()
        val starSize = resources.getDimensionPixelSize(R.dimen.rating_star_size)
        val starMargin = resources.getDimensionPixelSize(R.dimen.rating_star_margin)
        
        // Create horizontal layout for stars only
        val starsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        
        for (i in 1..5) {
            val isFilled = i <= roundedRating
            val starImageView = ImageView(this).apply {
                setImageResource(R.drawable.star)
                layoutParams = LinearLayout.LayoutParams(starSize, starSize).apply {
                    marginEnd = starMargin
                }
                alpha = if (isFilled) 1f else 0.3f
            }
            starsLayout.addView(starImageView)
        }
        
        // Add stars layout to the container
        ratingContainer.addView(starsLayout)
        
        // Add the rating text
        val ratingText = TextView(this).apply {
            text = String.format("%.1f", rating)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(
                resources.getDimensionPixelSize(R.dimen.rating_text_padding_start),
                0, 0, 0
            )
            textSize = resources.getDimension(R.dimen.rating_text_size) / 
                       resources.displayMetrics.density
            gravity = android.view.Gravity.CENTER_VERTICAL
        }
        ratingContainer.addView(ratingText)
    }
    
    /**
     * Update the quantity label text based on selected period.
     * Changes label to reflect weekly or monthly selection.
     */
    private fun updateQuantityLabel() {
        quantityLabel.text = getString(
            if (isMonthly) R.string.label_quantity_months else R.string.label_quantity_weeks
        )
    }
    
    /**
     * Calculate and display prices based on current selections.
     * Updates the UI with calculated price information.
     */
    private fun updatePriceDisplay() {
        val instrument = currentInstrument ?: return
        
        val basePrice = if (isMonthly) instrument.price * 4 else instrument.price
        val totalPrice = basePrice * quantity
        
        totalPriceTextView.text = getString(R.string.format_total_price, totalPrice)
        pricePerPeriodTextView.text = getString(
            R.string.format_price_per_period,
            basePrice,
            getString(if (isMonthly) R.string.format_period_month else R.string.format_period_week)
        )
    }
    
    /**
     * Process the instrument borrowing request.
     * Checks credit balance and either initiates the rental process
     * or prompts the user to add more credits.
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
            AlertDialog.Builder(this)
                .setTitle(R.string.title_insufficient_credits)
                .setMessage(getString(R.string.message_insufficient_credits, totalPrice, currentBalance))
                .setPositiveButton(R.string.action_add_credits) { _, _ ->
                    startActivity(Intent(this, CreditsActivity::class.java))
                }
                .setNegativeButton(R.string.action_cancel, null)
                .show()
        } else {
            // If user has enough credits, proceed to user info screen
            val rentalPeriod = if (isMonthly) {
                val periodString = if (quantity > 1) 
                    R.string.format_period_months else R.string.format_period_month
                "$quantity ${getString(periodString)}"
            } else {
                val periodString = if (quantity > 1) 
                    R.string.format_period_weeks else R.string.format_period_week
                "$quantity ${getString(periodString)}"
            }
            
            // Create rental details object to pass to UserInfoActivity
            val rentalDetails = RentalDetails(
                instrumentName = instrument.name,
                instrumentDescription = instrument.description,
                instrumentImageRes = instrument.imageRes,
                rentalPeriod = rentalPeriod,
                totalPrice = totalPrice,
                isMonthly = isMonthly,
                quantity = quantity
            )
            
            // Launch UserInfoActivity with rental details
            val intent = Intent(this, UserInfoActivity::class.java)
            intent.putExtra("RENTAL_DETAILS", rentalDetails)
            startActivityForResult(intent, REQUEST_USER_INFO)
        }
    }
    
    /**
     * Reset selection to defaults and notify user.
     * Clears all user selections and returns to initial state.
     */
    private fun resetToDefaults() {
        instrumentSpinner.setSelection(0)
        periodGroup.check(R.id.weeklyChip)
        isMonthly = false
        quantityEditText.setText("1")
        quantity = 1
        
        Toast.makeText(this, R.string.message_selection_reset, Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Inflate options menu with credits display.
     * Creates the action bar menu with credits balance.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        creditsMenuItem = menu.findItem(R.id.action_credits)
        updateCreditsDisplay()
        return true
    }
    
    /**
     * Handle activity result from UserInfoActivity.
     * Process the result of the rental flow.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_USER_INFO && resultCode == RESULT_OK) {
            // Rental was successful
            resetToDefaults()
        }
    }
}