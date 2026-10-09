package com.fitlife.app.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "water")
data class WaterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val day: Long,        // LocalDate.toEpochDay()
    val time: Long,       // epoch millis
    val amountMl: Int
)

@Dao
interface WaterDao {
    @Insert suspend fun insert(e: WaterEntity)

    @Query("SELECT COALESCE(SUM(amountMl),0) FROM water WHERE day = :day")
    fun observeTotal(day: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(amountMl),0) FROM water WHERE day = :day")
    suspend fun totalOnce(day: Long): Int

    @Query("DELETE FROM water WHERE id = (SELECT id FROM water WHERE day = :day ORDER BY time DESC LIMIT 1)")
    suspend fun deleteLast(day: Long)
}

@Database(entities = [WaterEntity::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun waterDao(): WaterDao

    companion object {
        @Volatile private var inst: AppDb? = null
        fun get(ctx: Context): AppDb = inst ?: synchronized(this) {
            inst ?: Room.databaseBuilder(ctx.applicationContext, AppDb::class.java, "fitlife.db")
                .build().also { inst = it }
        }
    }
}
