package com.example.instrumentsrental.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages credits balance storage and operations
 */
class CreditsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Get the current credits balance
     */
    fun getCreditsBalance(): Int {
        return prefs.getInt(KEY_CREDITS, DEFAULT_CREDITS)
    }
    
    /**
     * Add the specified amount to the credits balance
     */
    fun addCredits(amount: Int): Int {
        val currentBalance = getCreditsBalance()
        val newBalance = currentBalance + amount
        prefs.edit().putInt(KEY_CREDITS, newBalance).apply()
        return newBalance
    }
    
    /**
     * Set the credits balance to a specific value
     */
    fun setCreditsBalance(amount: Int) {
        prefs.edit().putInt(KEY_CREDITS, amount).apply()
    }
    
    companion object {
        private const val PREFS_NAME = "credits_prefs"
        private const val KEY_CREDITS = "credits_balance"
        private const val DEFAULT_CREDITS = 5000
    }
} 