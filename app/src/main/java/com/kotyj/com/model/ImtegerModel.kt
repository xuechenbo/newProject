package com.kotyj.com.model

data class ImtegerModel(
        val CREATE_TIME: CREATETIME,
        val ID: String,
        val POINT: String,
        val status: String,
        val type: String
)

data class CREATETIME(
        val date: Int,
        val day: Int,
        val hours: Int,
        val minutes: Int,
        val month: Int,
        val nanos: Int,
        val seconds: Int,
        val time: Long,
        val timezoneOffset: Int,
        val year: Int
)