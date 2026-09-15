package me.riafy.finlog.data.models

data class Category(
    val id: Long,
    val name: String,
    val iconKey: String,
    val colorHex: String,
    val sortOrder: Long,
    val isDefault: Boolean
)

/**
 * The category set a fresh install seeds the database with. Users can rename,
 * recolor or add to these afterward - this list only decides what's there on
 * day one.
 */
object DefaultCategories {

    data class Seed(val name: String, val iconKey: String, val colorHex: String)

    val seeds = listOf(
        Seed("Food", CategoryIconKey.FOOD, "#E8734D"),
        Seed("Transport", CategoryIconKey.TRANSPORT, "#4C8DFF"),
        Seed("Shopping", CategoryIconKey.SHOPPING, "#B05FD1"),
        Seed("Bills", CategoryIconKey.BILLS, "#E8A33D"),
        Seed("Entertainment", CategoryIconKey.ENTERTAINMENT, "#D64B8F"),
        Seed("Health", CategoryIconKey.HEALTH, "#2FA8A0"),
        Seed("Travel", CategoryIconKey.TRAVEL, "#4FB669"),
        Seed("Subscriptions", CategoryIconKey.SUBSCRIPTIONS, "#6B7FE0"),
        Seed("Education", CategoryIconKey.EDUCATION, "#3DA5D9"),
        Seed("Personal", CategoryIconKey.PERSONAL, "#8A93A6"),
        Seed("Other", CategoryIconKey.OTHER, "#9AA3B8")
    )

    /** "Other" is where a deleted category's expenses land, so it can't be removed. */
    const val FALLBACK_NAME = "Other"
}

/** The swatches offered when a user picks a colour for their own category - the same set the defaults are drawn from. */
object CategoryColorPalette {
    val hexColors = listOf(
        "#E8734D", "#4C8DFF", "#B05FD1", "#E8A33D", "#D64B8F",
        "#2FA8A0", "#4FB669", "#6B7FE0", "#3DA5D9", "#8A93A6", "#9AA3B8"
    )
}

/** String keys stored in the database and mapped to an ImageVector by CategoryIcons. */
object CategoryIconKey {
    const val FOOD = "food"
    const val TRANSPORT = "transport"
    const val SHOPPING = "shopping"
    const val BILLS = "bills"
    const val ENTERTAINMENT = "entertainment"
    const val HEALTH = "health"
    const val TRAVEL = "travel"
    const val SUBSCRIPTIONS = "subscriptions"
    const val EDUCATION = "education"
    const val PERSONAL = "personal"
    const val OTHER = "other"
}
