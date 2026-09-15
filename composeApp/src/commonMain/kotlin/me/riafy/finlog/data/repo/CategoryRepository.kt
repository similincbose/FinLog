package me.riafy.finlog.data.repo

import me.riafy.finlog.data.local.database.toDomain
import me.riafy.finlog.data.models.Category
import me.riafy.finlog.data.models.DefaultCategories
import me.riafy.finlog.database.FinlogDatabase

class CategoryRepository(private val database: FinlogDatabase) {

    private val queries = database.categoryQueries

    suspend fun getAll(): List<Category> = queries.selectAll().executeAsList().map { it.toDomain() }

    suspend fun findById(id: Long): Category? = queries.selectById(id).executeAsOneOrNull()?.toDomain()

    /** Inserts the starter category list the first time the app runs with an empty table. */
    suspend fun seedDefaultsIfNeeded() {
        if (queries.selectCount().executeAsOne() > 0) return
        DefaultCategories.seeds.forEachIndexed { index, seed ->
            queries.insert(
                name = seed.name,
                icon_key = seed.iconKey,
                color_hex = seed.colorHex,
                sort_order = index.toLong(),
                is_default = 1L
            )
        }
    }

    suspend fun add(name: String, iconKey: String, colorHex: String): Long {
        val nextSortOrder = (queries.selectAll().executeAsList().maxOfOrNull { it.sort_order } ?: -1L) + 1L
        queries.insert(name = name, icon_key = iconKey, color_hex = colorHex, sort_order = nextSortOrder, is_default = 0L)
        return queries.lastInsertRowId().executeAsOne()
    }

    suspend fun update(id: Long, name: String, iconKey: String, colorHex: String) {
        queries.update(name = name, icon_key = iconKey, color_hex = colorHex, id = id)
    }

    /**
     * Deletes a category, moving any expenses under it to "Other" first so
     * deleting a category never orphans or silently deletes someone's spending
     * history. The fallback category itself can't be deleted.
     */
    suspend fun delete(id: Long) {
        val all = queries.selectAll().executeAsList()
        val target = all.find { it.id == id } ?: return
        val fallback = all.find { it.name == DefaultCategories.FALLBACK_NAME && it.id != id }

        if (target.name == DefaultCategories.FALLBACK_NAME || fallback == null) return

        database.expenseQueries.reassignCategory(fallback.id, id)
        queries.deleteById(id)
    }
}
