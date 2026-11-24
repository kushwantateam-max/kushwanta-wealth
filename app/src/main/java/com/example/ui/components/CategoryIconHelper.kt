package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.model.AccountEntity
import com.example.data.model.AccountType

object CategoryIconHelper {

    fun iconFor(iconName: String?): ImageVector {
        return when (iconName) {
            "restaurant" -> Icons.Default.Restaurant
            "laptop" -> Icons.Default.Laptop
            "shopping_bag" -> Icons.Default.ShoppingBag
            "trending_up" -> Icons.AutoMirrored.Filled.TrendingUp
            "payments" -> Icons.Default.Payments
            "local_grocery_store" -> Icons.Default.LocalGroceryStore
            "home" -> Icons.Default.Home
            "movie" -> Icons.Default.Movie
            "directions_car" -> Icons.Default.DirectionsCar
            "fitness_center" -> Icons.Default.FitnessCenter
            "medical_services" -> Icons.Default.MedicalServices
            "pets" -> Icons.Default.Pets
            "flight" -> Icons.Default.Flight
            "school" -> Icons.Default.School
            "card_giftcard" -> Icons.Default.CardGiftcard
            "build" -> Icons.Default.Build
            "auto_awesome" -> Icons.Default.AutoAwesome
            "qr_code_2" -> Icons.Default.QrCode2
            else -> Icons.Default.SwapHoriz
        }
    }

    fun isUpiWallet(account: AccountEntity): Boolean {
        val low = account.name.lowercase()
        return low.contains("upi") ||
            low.contains("gpay") ||
            low.contains("phonepe") ||
            low.contains("paytm") ||
            low.contains("bhim")
    }

    fun accountIconFor(account: AccountEntity): ImageVector {
        if (isUpiWallet(account)) return Icons.Default.QrCode2
        return when (account.type) {
            AccountType.BANK -> Icons.Default.AccountBalance
            AccountType.CREDIT_CARD -> Icons.Default.CreditCard
            AccountType.CASH -> Icons.Default.Payments
            AccountType.INVESTMENT -> Icons.AutoMirrored.Filled.TrendingUp
        }
    }
}
