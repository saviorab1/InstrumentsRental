package com.example.instrumentsrental.model

import android.os.Parcel
import android.os.Parcelable

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
data class RentalDetails(
    val instrumentName: String,
    val instrumentDescription: String,
    val instrumentImageRes: Int,
    val rentalPeriod: String,
    val totalPrice: Int,
    val isMonthly: Boolean,
    val quantity: Int
) : Parcelable {
    
    // Constructor that takes a Parcel and reads the data in the same order it was written
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),  // Convert byte to boolean
        parcel.readInt()
    )
    
    // Write object's data to the parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(instrumentName)
        parcel.writeString(instrumentDescription)
        parcel.writeInt(instrumentImageRes)
        parcel.writeString(rentalPeriod)
        parcel.writeInt(totalPrice)
        parcel.writeByte(if (isMonthly) 1 else 0)  // Convert boolean to byte
        parcel.writeInt(quantity)
    }
    
    // Return a bitmask indicating the set of special object types in this Parcelable
    override fun describeContents(): Int {
        return 0
    }
    
    // Static CREATOR field required for Parcelable implementation
    companion object CREATOR : Parcelable.Creator<RentalDetails> {
        // Create a new instance of the Parcelable class, from a Parcel
        override fun createFromParcel(parcel: Parcel): RentalDetails {
            return RentalDetails(parcel)
        }
        
        // Create a new array of the Parcelable class
        override fun newArray(size: Int): Array<RentalDetails?> {
            return arrayOfNulls(size)
        }
    }
} 