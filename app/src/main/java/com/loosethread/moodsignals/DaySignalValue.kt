package com.loosethread.moodsignals

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_signal_values")
data class DaySignalValue(
    @PrimaryKey(autoGenerate = true) val id: UInt,
    @ColumnInfo(name = "day_id") val dayId: UShort,
    @ColumnInfo(name = "signal_id") val signalId: UShort,
    @ColumnInfo(name = "signal_value_id") val signalValueId: UShort,
)
