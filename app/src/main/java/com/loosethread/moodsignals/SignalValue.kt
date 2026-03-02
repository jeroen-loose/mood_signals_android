package com.loosethread.moodsignals

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signal_values")
data class SignalValue(
    @PrimaryKey(autoGenerate = true) val id: UShort,
    @ColumnInfo(name = "signal_id") val signalId: UShort,
    @ColumnInfo(name = "score") val score: UByte,
    @ColumnInfo(name = "description") val description: String,
)
