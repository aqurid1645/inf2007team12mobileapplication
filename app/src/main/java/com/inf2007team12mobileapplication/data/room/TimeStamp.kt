package com.inf2007team12mobileapplication.data.room

import androidx.room.TypeConverter
import com.google.firebase.Timestamp

class TimestampConverter {
    @TypeConverter
    fun fromTimestamp(value: Timestamp?): Long? {
        return value?.toDate()?.time
    }

    @TypeConverter
    fun toTimestamp(value: Long?): Timestamp? {
        return value?.let { Timestamp(it / 1000, 0) }
    }
}
