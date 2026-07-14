package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FestivalDao {
    @Query("SELECT * FROM festivals ORDER BY month ASC, day ASC")
    fun getAllFestivals(): Flow<List<FestivalEntity>>

    @Query("SELECT * FROM festivals WHERE month = :month ORDER BY day ASC")
    fun getFestivalsForMonth(month: Int): Flow<List<FestivalEntity>>

    @Query("SELECT * FROM festivals WHERE month = :month AND day = :day")
    fun getFestivalsForDay(month: Int, day: Int): Flow<List<FestivalEntity>>

    @Query("""
        SELECT * FROM festivals 
        WHERE name LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR meaning LIKE '%' || :query || '%' 
        OR historyNotes LIKE '%' || :query || '%'
    """)
    fun searchFestivals(query: String): Flow<List<FestivalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(festivals: List<FestivalEntity>)
}

@Dao
interface HistoryTopicDao {
    @Query("SELECT * FROM history_topics")
    fun getAllTopics(): Flow<List<HistoryTopicEntity>>

    @Query("SELECT * FROM history_topics WHERE era = :era")
    fun getTopicsByEra(era: String): Flow<List<HistoryTopicEntity>>

    @Query("SELECT * FROM history_topics WHERE id = :id")
    fun getTopicById(id: Int): Flow<HistoryTopicEntity?>

    @Query("""
        SELECT * FROM history_topics 
        WHERE title LIKE '%' || :query || '%' 
        OR content LIKE '%' || :query || '%' 
        OR summary LIKE '%' || :query || '%'
    """)
    fun searchTopics(query: String): Flow<List<HistoryTopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(topics: List<HistoryTopicEntity>)
}
