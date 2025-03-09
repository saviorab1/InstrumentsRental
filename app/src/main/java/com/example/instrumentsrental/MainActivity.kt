package com.example.instrumentsrental

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
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
        
        setupSpinner()
        setupPeriodRadioButtons()
        setupQuantityInput()
    }
    
    override fun onResume() {
        super.onResume()
        // Update credits display when returning to this activity
        updateCreditsDisplay()
    }
    
    private fun updateCreditsDisplay() {
        // Update the menu item text if available
        creditsMenuItem?.title = "$${creditsManager.getCreditsBalance()}"
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
    
    private fun setupPeriodRadioButtons() {
        periodGroup.setOnCheckedChangeListener { _, checkedId ->
            isMonthly = checkedId == R.id.monthlyChip
            updateQuantityLabel()
            updatePriceDisplay()
        }
    }
    
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
    
    private fun updateQuantityLabel() {
        quantityLabel.text = "Number of ${if (isMonthly) "months" else "weeks"}:"
    }
    
    private fun updatePriceDisplay() {
        val instrument = currentInstrument ?: return
        
        val basePrice = if (isMonthly) instrument.price * 4 else instrument.price
        val totalPrice = basePrice * quantity
        
        totalPriceTextView.text = "$totalPrice credits total"
        pricePerPeriodTextView.text = "(${basePrice} credits per ${if (isMonthly) "month" else "week"})"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        creditsMenuItem = menu.findItem(R.id.action_credits)
        updateCreditsDisplay() // Set initial credits value
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_credits -> {
                // Launch the Credits Activity
                val intent = Intent(this, CreditsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}