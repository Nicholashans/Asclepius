package com.dicoding.asclepius.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(history: History)

    @Delete
    fun delete(history: History)

    @Query("SELECT * from History ORDER BY historyId ASC")
    fun getAllHistory(): LiveData<List<History>>

    @Query("SELECT EXISTS(SELECT 1 FROM History WHERE historyId = :historyId)")
    fun isHistory(historyId: String) : LiveData<Boolean>

}