package com.snoop.expensetracker.data.portability

import android.content.ContentResolver
import android.net.Uri
import com.snoop.expensetracker.data.db.AppDatabase
import com.snoop.expensetracker.data.db.entities.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataPortabilityManager @Inject constructor(private val db: AppDatabase) {
    suspend fun exportCsv(resolver: ContentResolver, uri: Uri) {
        val txs = db.transactionDao().getAllOnce()
        resolver.openOutputStream(uri)?.bufferedWriter()?.use { out ->
            out.appendLine("id,date,amount_rupees,type,merchant,category,source_app,bank,status,confidence,note,deleted")
            val cats = db.categoryDao().getAllOnce().associateBy { it.id }
            val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            txs.forEach { t ->
                val row = listOf(
                    t.id.toString(), fmt.format(Date(t.createdAt)), "%.2f".format(Locale.US, t.amountMinor / 100.0),
                    t.direction.name, t.merchantResolved ?: t.merchantRaw, cats[t.categoryId]?.name.orEmpty(),
                    t.sourceApp, t.bank, t.status.name, t.confidence.toString(), t.note.orEmpty(), (t.deletedAt != null).toString()
                )
                out.appendLine(row.joinToString(",") { csv(it) })
            }
        } ?: error("Unable to open export destination")
    }

    suspend fun backup(resolver: ContentResolver, uri: Uri) {
        val root = JSONObject().apply {
            put("format", "snoop-backup")
            put("version", 2)
            put("createdAt", System.currentTimeMillis())
            put("categories", JSONArray(db.categoryDao().getAllOnce().map { JSONObject().apply { put("id",it.id); put("name",it.name); put("icon",it.icon); put("color",it.color); put("isCustom",it.isCustom) } }))
            put("transactions", JSONArray(db.transactionDao().getAllOnce().map { JSONObject().apply { put("id",it.id); put("amountMinor",it.amountMinor); put("direction",it.direction.name); put("merchantRaw",it.merchantRaw); put("merchantResolved",it.merchantResolved); put("categoryId",it.categoryId); put("sourceApp",it.sourceApp); put("bank",it.bank); put("rawText",it.rawText); put("confidence",it.confidence); put("status",it.status.name); put("dedupeKey",it.dedupeKey); put("deletedAt",it.deletedAt); put("createdAt",it.createdAt); put("note",it.note); put("suggestedCategory",it.suggestedCategory); put("suggestedCategoryConfidence",it.suggestedCategoryConfidence) } }))
            put("budgets", JSONArray(db.budgetDao().getAllOnce().map { JSONObject().apply { put("id",it.id); put("categoryId",it.categoryId); put("limitMinor",it.limitMinor); put("period",it.period.name); put("startDate",it.startDate); put("warningNotifiedPeriodStart",it.warningNotifiedPeriodStart); put("overNotifiedPeriodStart",it.overNotifiedPeriodStart) } }))
            put("merchantMappings", JSONArray(db.merchantMappingDao().getAllOnce().map { JSONObject().apply { put("merchantKey",it.merchantKey); put("categoryId",it.categoryId); put("learnedAt",it.learnedAt) } }))
            db.balanceDao().getAnchorOnce()?.let { anchor ->
                put("balanceAnchor", JSONObject().apply {
                    put("balanceMinor", anchor.balanceMinor)
                    put("anchoredAt", anchor.anchoredAt)
                })
            }
        }
        resolver.openOutputStream(uri)?.bufferedWriter()?.use { it.write(root.toString(2)) } ?: error("Unable to open backup destination")
    }

    suspend fun restore(resolver: ContentResolver, uri: Uri) {
        val text = resolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: error("Unable to read backup")
        val root = JSONObject(text)
        require(root.optString("format") == "snoop-backup") { "Not a Snoop backup" }
        require(root.optInt("version") in 1..2) { "Unsupported backup version" }
        val categories = mutableListOf<CategoryEntity>()
        val txs = mutableListOf<TransactionEntity>()
        val budgets = mutableListOf<BudgetEntity>()
        val mappings = mutableListOf<MerchantMappingEntity>()
        val balanceAnchor = root.optJSONObject("balanceAnchor")?.let {
            BalanceAnchorEntity(
                balanceMinor = it.getLong("balanceMinor"),
                anchoredAt = it.getLong("anchoredAt")
            )
        }
        root.getJSONArray("categories").let { a -> for (i in 0 until a.length()) { val o=a.getJSONObject(i); categories += CategoryEntity(o.getLong("id"),o.getString("name"),o.getString("icon"),o.getString("color"),o.getBoolean("isCustom")) } }
        root.getJSONArray("transactions").let { a -> for (i in 0 until a.length()) { val o=a.getJSONObject(i); txs += TransactionEntity(o.getLong("id"),o.getLong("amountMinor"),TransactionDirection.valueOf(o.getString("direction")),o.getString("merchantRaw"),o.optString("merchantResolved").ifBlank{null},if(o.isNull("categoryId"))null else o.getLong("categoryId"),o.getString("sourceApp"),o.getString("bank"),o.getString("rawText"),o.getDouble("confidence").toFloat(),TransactionStatus.valueOf(o.getString("status")),o.optString("dedupeKey").ifBlank{null},if(o.isNull("deletedAt"))null else o.getLong("deletedAt"),o.getLong("createdAt"),o.optString("note").ifBlank{null},o.optString("suggestedCategory").ifBlank{null},o.optDouble("suggestedCategoryConfidence",0.0).toFloat()) } }
        root.getJSONArray("budgets").let { a -> for (i in 0 until a.length()) { val o=a.getJSONObject(i); budgets += BudgetEntity(o.getLong("id"),o.getLong("categoryId"),o.getLong("limitMinor"),BudgetPeriod.valueOf(o.getString("period")),o.getLong("startDate"),if(o.isNull("warningNotifiedPeriodStart"))null else o.getLong("warningNotifiedPeriodStart"),if(o.isNull("overNotifiedPeriodStart"))null else o.getLong("overNotifiedPeriodStart")) } }
        root.getJSONArray("merchantMappings").let { a -> for (i in 0 until a.length()) { val o=a.getJSONObject(i); mappings += MerchantMappingEntity(o.getString("merchantKey"),o.getLong("categoryId"),o.getLong("learnedAt")) } }
        db.runInTransaction {
            kotlinx.coroutines.runBlocking {
                db.budgetDao().deleteAll(); db.merchantMappingDao().deleteAll(); db.transactionDao().deleteAll(); db.categoryDao().deleteAll(); db.balanceDao().clear()
                db.categoryDao().replaceAll(categories); db.transactionDao().insertAll(txs); db.budgetDao().replaceAll(budgets); db.merchantMappingDao().upsertAll(mappings)
                if (balanceAnchor != null) db.balanceDao().upsertAnchor(balanceAnchor)
            }
        }
    }

    private fun csv(v: String): String = if (v.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) "\"${v.replace("\"", "\"\"")}\"" else v
}
