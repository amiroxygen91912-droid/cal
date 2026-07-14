package com.example.repository

import com.example.database.FestivalDao
import com.example.database.FestivalEntity
import com.example.database.HistoryTopicDao
import com.example.database.HistoryTopicEntity
import kotlinx.coroutines.flow.Flow

class CalendarRepository(
    private val festivalDao: FestivalDao,
    private val historyTopicDao: HistoryTopicDao
) {
    // Festivals
    fun getAllFestivals(): Flow<List<FestivalEntity>> = festivalDao.getAllFestivals()

    fun getFestivalsForMonth(month: Int): Flow<List<FestivalEntity>> =
        festivalDao.getFestivalsForMonth(month)

    fun getFestivalsForDay(month: Int, day: Int): Flow<List<FestivalEntity>> =
        festivalDao.getFestivalsForDay(month, day)

    fun searchFestivals(query: String): Flow<List<FestivalEntity>> =
        festivalDao.searchFestivals(query)

    // History Topics
    fun getAllHistoryTopics(): Flow<List<HistoryTopicEntity>> = historyTopicDao.getAllTopics()

    fun getHistoryTopicsByEra(era: String): Flow<List<HistoryTopicEntity>> =
        historyTopicDao.getTopicsByEra(era)

    fun getHistoryTopicById(id: Int): Flow<HistoryTopicEntity?> =
        historyTopicDao.getTopicById(id)

    fun searchHistoryTopics(query: String): Flow<List<HistoryTopicEntity>> =
        historyTopicDao.searchTopics(query)
}
