package com.loosethread.moodsignals

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "days")
data class Day(
    @PrimaryKey(autoGenerate = true) val id: UShort,
    @ColumnInfo(name = "date") val date: LocalDate,
)
