package com.example.instrumentsrental

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
    
    // Tag for logging
    private val TAG = "MainActivity"
    
    // UI components
    private lateinit var instrumentSpinner: Spinner
    private lateinit var instrumentImage: ImageView
    private lateinit var instrumentName: TextView
    private lateinit var instrumentRatingBar: RatingBar
    private lateinit var ratingTextView: TextView
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
    
    // State keys for saving instance state
    private val KEY_IS_MONTHLY = "is_monthly"
    private val KEY_QUANTITY = "quantity"
    private val KEY_SELECTED_INSTRUMENT = "selected_instrument"
    
    companion object {
        private const val REQUEST_USER_INFO = 101
    }
    
    /**
     * Initializes the activity, sets up the UI components and listeners.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Initializing MainActivity")
        setContentView(R.layout.activity_main)
        
        // Set activity title
        title = "MUSICLEASE"
        
        // Initialize credits manager
        creditsManager = CreditsManager(this)
        
        // Initialize views
        initializeViews()
        setupListeners()
        
        // Restore state if available
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState)
        }
        
        Log.d(TAG, "onCreate: Setup complete")
    }
    
    /**
     * Initialize all UI views from layout. Binds layout elements to properties.
     */
    private fun initializeViews() {
        Log.d(TAG, "initializeViews: Binding UI elements")
        instrumentSpinner = findViewById(R.id.instrumentSpinner)
        instrumentImage = findViewById(R.id.instrumentImage)
        instrumentName = findViewById(R.id.instrumentName)
        instrumentRatingBar = findViewById(R.id.instrumentRatingBar)
        ratingTextView = findViewById(R.id.ratingTextView)
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
        Log.d(TAG, "setupListeners: Setting up UI listeners")
        setupSpinner()
        setupPeriodRadioButtons()
        setupQuantityInput()
        
        cancelButton.setOnClickListener {
            Log.d(TAG, "Cancel button clicked")
            resetToDefaults()
        }
        
        borrowButton.setOnClickListener {
            Log.d(TAG, "Borrow button clicked")
            processBorrowing()
        }
    }
    
    /**
     * Updates credits display when returning to activity.
     * Called when the activity returns to foreground.
     */
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Activity resumed")
        updateCreditsDisplay()
    }
    
    /**
     * Updates the credits balance in the menu item.
     * Shows current credit balance in the action bar.
     */
    private fun updateCreditsDisplay() {
        val credits = creditsManager.getCreditsBalance()
        Log.d(TAG, "updateCreditsDisplay: Current credits balance: $credits")
        
        // Get the menu item and apply custom styling
        creditsMenuItem?.let { menuItem ->
            // Create a SpannableString to apply custom styling
            val creditText = "$${credits}"
            val spannableString = android.text.SpannableString(creditText)
            
            // Set the title with the styled text
            menuItem.title = spannableString
        }
    }
    
    /**
     * Set up the instrument category spinner with data and listener.
     * Populates spinner with instrument categories and handles selection changes.
     */
    private fun setupSpinner() {
        val categories = InstrumentDataProvider.getCategories()
        Log.d(TAG, "setupSpinner: Loading ${categories.size} instrument categories")
        
        // Custom adapter with improved styling
        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            categories
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                if (view is TextView) {
                    view.typeface = resources.getFont(R.font.open_sans_semibold)
                    view.textSize = 16f
                    view.setPadding(8, 16, 8, 16)
                }
                return view
            }
            
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                if (view is TextView) {
                    view.typeface = resources.getFont(R.font.open_sans_regular)
                    view.setTextColor(resources.getColor(R.color.black, null))
                    view.setPadding(24, 16, 24, 16)
                }
                return view
            }
        }
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        instrumentSpinner.adapter = adapter
        
        instrumentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
                Log.d(TAG, "onItemSelected: Category selected: $selectedCategory")
                updateInstrumentDetails(selectedCategory)
            }
            
            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d(TAG, "onNothingSelected: No instrument category selected")
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
            Log.d(TAG, "setupPeriodRadioButtons: Period changed, isMonthly=$isMonthly")
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
                        Log.d(TAG, "Quantity changed to: $quantity")
                        updatePriceDisplay()
                    } else if (value > 99) {
                        Log.d(TAG, "Quantity exceeds limit (99), setting to max")
                        quantityEditText.setText("99")
                        quantityEditText.setSelection(2)
                    }
                } catch (e: NumberFormatException) {
                    if (s.toString().isEmpty()) {
                        quantity = 1
                        Log.d(TAG, "Empty quantity, defaulting to 1")
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
        
        Log.d(TAG, "updateInstrumentDetails: Loading details for ${instrument.name}")
        
        // Update UI with instrument details in the correct order
        instrumentName.text = instrument.name
        descriptionTextView.text = instrument.description
        updateRatingBar(instrument.rating)
        instrumentImage.setImageResource(instrument.imageRes)
        
        updatePriceDisplay()
    }
    
    /**
     * Updates the rating bar with the instrument's rating value
     */
    private fun updateRatingBar(rating: Float) {
        // Set the rating on the RatingBar
        instrumentRatingBar.rating = rating
        
        // Update the text display of the rating
        ratingTextView.text = String.format("%.1f", rating)
    }
    
    /**
     * Update the quantity label text based on selected period.
     * Changes label to reflect weekly or monthly selection.
     */
    private fun updateQuantityLabel() {
        val labelRes = if (isMonthly) R.string.label_quantity_months else R.string.label_quantity_weeks
        Log.d(TAG, "updateQuantityLabel: Updating to ${resources.getString(labelRes)}")
        quantityLabel.text = getString(labelRes)
    }
    
    /**
     * Calculate and display prices based on current selections.
     * Updates the UI with calculated price information.
     */
    private fun updatePriceDisplay() {
        val instrument = currentInstrument ?: return
        
        val basePrice = if (isMonthly) instrument.price * 4 else instrument.price
        val totalPrice = basePrice * quantity
        
        Log.d(TAG, "updatePriceDisplay: Base price=$basePrice, Total price=$totalPrice, isMonthly=$isMonthly, quantity=$quantity")
        
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
        Log.d(TAG, "processBorrowing: Attempting to rent instrument. Required credits=$totalPrice, Available balance=$currentBalance")
        
        if (totalPrice > currentBalance) {
            Log.d(TAG, "processBorrowing: Insufficient credits, showing dialog")
            // Using themed dialog
            AlertDialog.Builder(this, R.style.AppDialogTheme)
                .setTitle(R.string.title_insufficient_credits)
                .setMessage(getString(R.string.message_insufficient_credits, totalPrice, currentBalance))
                .setPositiveButton(R.string.action_add_credits) { _, _ ->
                    Log.d(TAG, "processBorrowing: User chose to add credits")
                    startActivity(Intent(this, CreditsActivity::class.java))
                }
                .setNegativeButton(R.string.action_cancel) { _, _ ->
                    Log.d(TAG, "processBorrowing: User cancelled adding credits")
                }
                .show()
        } else {
            // If user has enough credits, proceed to user info screen
            Log.d(TAG, "processBorrowing: Sufficient credits, proceeding to user info")
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
        Log.d(TAG, "resetToDefaults: Resetting all selections to default values")
        instrumentSpinner.setSelection(0)
        periodGroup.check(R.id.weeklyChip)
        isMonthly = false
        quantityEditText.setText("1")
        quantity = 1
        
        // Custom toast with better styling
        val toast = Toast.makeText(this, R.string.message_selection_reset, Toast.LENGTH_SHORT)
        val view = toast.view
        view?.apply {
            background.setTint(resources.getColor(R.color.purple_700, null))
            findViewById<TextView>(android.R.id.message)?.apply {
                setTextColor(resources.getColor(android.R.color.white, null))
                typeface = resources.getFont(R.font.open_sans_semibold)
            }
        }
        toast.show()
    }
    
    /**
     * Inflate options menu with credits display.
     * Creates the action bar menu with credits balance.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu: Creating options menu")
        menuInflater.inflate(R.menu.main_menu, menu)
        creditsMenuItem = menu.findItem(R.id.action_credits)
        
        // Set the title text color programmatically
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
     * Handle options menu item selection.
     * Handles navigation to Credits Activity when credits balance is clicked.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected: Menu item selected: ${item.itemId}")
        return when (item.itemId) {
            R.id.action_credits -> {
                Log.d(TAG, "onOptionsItemSelected: Credits menu item clicked, navigating to CreditsActivity")
                startActivity(Intent(this, CreditsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    /**
     * Handle activity result from UserInfoActivity.
     * Process the result of the rental flow.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        Log.d(TAG, "onActivityResult: requestCode=$requestCode, resultCode=$resultCode")
        
        if (requestCode == REQUEST_USER_INFO && resultCode == RESULT_OK) {
            // Rental was successful
            Log.d(TAG, "onActivityResult: Rental was successful, resetting UI")
            resetToDefaults()
        }
    }
    
    /**
     * Save the current state when the activity might be recreated.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState: Saving instance state")
        
        // Save current UI state
        outState.putBoolean(KEY_IS_MONTHLY, isMonthly)
        outState.putInt(KEY_QUANTITY, quantity)
        currentInstrument?.let { instrument ->
            outState.putString(KEY_SELECTED_INSTRUMENT, instrument.name)
        }
    }
    
    /**
     * Restore UI state from saved instance
     */
    private fun restoreInstanceState(savedInstanceState: Bundle) {
        Log.d(TAG, "restoreInstanceState: Restoring instance state")
        
        // Restore rental period selection
        isMonthly = savedInstanceState.getBoolean(KEY_IS_MONTHLY, false)
        if (isMonthly) {
            periodGroup.check(R.id.monthlyChip)
        } else {
            periodGroup.check(R.id.weeklyChip)
        }
        
        // Restore quantity
        quantity = savedInstanceState.getInt(KEY_QUANTITY, 1)
        quantityEditText.setText(quantity.toString())
        
        // Restore selected instrument
        val savedInstrumentName = savedInstanceState.getString(KEY_SELECTED_INSTRUMENT)
        savedInstrumentName?.let { name ->
            val categories = InstrumentDataProvider.getCategories()
            for (i in categories.indices) {
                val instrument = InstrumentDataProvider.getInstrumentByCategory(categories[i])
                if (instrument.name == name) {
                    instrumentSpinner.setSelection(i)
                    break
                }
            }
        }
        
        // Update the UI based on restored state
        updateQuantityLabel()
        updatePriceDisplay()
    }
}