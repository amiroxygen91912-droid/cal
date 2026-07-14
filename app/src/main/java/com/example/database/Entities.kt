package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "festivals")
data class FestivalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val month: Int, // 1 to 12
    val day: Int,   // 1 to 31
    val description: String,
    val meaning: String,
    val historyNotes: String,
    val type: String // "festival", "historical_event", "national"
)

@Entity(tableName = "history_topics")
data class HistoryTopicEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val era: String, // "achaemenid", "parthian", "sasanian", "kings", "symbols", "celebrations"
    val content: String,
    val summary: String,
    val visualAsset: String // placeholder descriptor for visual assets/icons
)
