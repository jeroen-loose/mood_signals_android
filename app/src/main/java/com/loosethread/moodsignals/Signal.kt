package com.loosethread.moodsignals

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signals")
data class Signal(
    @PrimaryKey(autoGenerate = true) val id: UShort,
    @ColumnInfo(name = "description") val description: String,
)
