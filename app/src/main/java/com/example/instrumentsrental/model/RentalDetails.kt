package com.example.instrumentsrental.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * RentalDetails - Data class representing details of an instrument rental.
 * Implements Parcelable to enable passing between activities.
 *
 * @property instrumentName Name of the instrument being rented
 * @property instrumentDescription Description of the instrument
 * @property instrumentImageRes Resource ID for the instrument image
 * @property rentalPeriod String representation of the rental period (e.g., "2 weeks")
 * @property totalPrice Total cost of the rental in credits
 * @property isMonthly Whether the rental period is in months (true) or weeks (false)
 * @property quantity Number of weeks/months for the rental period
 */
@Parcelize
data class RentalDetails(
    val instrumentName: String,
    val instrumentDescription: String,
    val instrumentImageRes: Int,
    val rentalPeriod: String,
    val totalPrice: Int,
    val isMonthly: Boolean,
    val quantity: Int
) : Parcelable 