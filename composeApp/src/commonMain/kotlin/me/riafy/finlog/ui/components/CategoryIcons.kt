package me.riafy.finlog.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector
import me.riafy.finlog.data.models.CategoryIconKey

/** Maps the icon key stored on a [me.riafy.finlog.data.models.Category] to the vector drawn for it. */
object CategoryIcons {

    private val icons: Map<String, ImageVector> = mapOf(
        CategoryIconKey.FOOD to Icons.Filled.Restaurant,
        CategoryIconKey.TRANSPORT to Icons.Filled.DirectionsCar,
        CategoryIconKey.SHOPPING to Icons.Filled.ShoppingBag,
        CategoryIconKey.BILLS to Icons.Filled.Receipt,
        CategoryIconKey.ENTERTAINMENT to Icons.Filled.Movie,
        CategoryIconKey.HEALTH to Icons.Filled.LocalHospital,
        CategoryIconKey.TRAVEL to Icons.Filled.Flight,
        CategoryIconKey.SUBSCRIPTIONS to Icons.Filled.Repeat,
        CategoryIconKey.EDUCATION to Icons.Filled.School,
        CategoryIconKey.PERSONAL to Icons.Filled.Person,
        CategoryIconKey.OTHER to Icons.Filled.Category
    )

    private val fallback = Icons.Filled.Favorite

    fun iconFor(iconKey: String): ImageVector = icons[iconKey] ?: fallback

    /** Icons offered when a user creates or edits their own category. */
    val assignable: List<Pair<String, ImageVector>> = icons.toList()
}
