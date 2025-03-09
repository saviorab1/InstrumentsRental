package com.example.instrumentsrental.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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