package me.riafy.finlog.data.repo

import me.riafy.finlog.data.local.database.toDomain
import me.riafy.finlog.data.models.DefaultPaymentMethods
import me.riafy.finlog.data.models.PaymentMethod
import me.riafy.finlog.database.FinlogDatabase

class PaymentMethodRepository(private val database: FinlogDatabase) {

    private val queries = database.paymentMethodQueries

    suspend fun getAll(): List<PaymentMethod> = queries.selectAll().executeAsList().map { it.toDomain() }

    suspend fun findById(id: Long): PaymentMethod? = queries.selectById(id).executeAsOneOrNull()?.toDomain()

    suspend fun seedDefaultsIfNeeded() {
        if (queries.selectCount().executeAsOne() > 0) return
        DefaultPaymentMethods.names.forEachIndexed { index, name ->
            queries.insert(name = name, sort_order = index.toLong(), is_default = 1L)
        }
    }

    suspend fun add(name: String): Long {
        val nextSortOrder = (queries.selectAll().executeAsList().maxOfOrNull { it.sort_order } ?: -1L) + 1L
        queries.insert(name = name, sort_order = nextSortOrder, is_default = 0L)
        return queries.lastInsertRowId().executeAsOne()
    }

    suspend fun update(id: Long, name: String) {
        queries.update(name = name, id = id)
    }

    suspend fun delete(id: Long) {
        queries.deleteById(id)
    }
}
