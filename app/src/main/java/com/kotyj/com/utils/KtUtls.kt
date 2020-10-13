package com.kotyj.com.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat


//TODO 扩展函数
//时间戳转日期字符串
fun Long.toYMD(): String = SimpleDateFormat("yyyy-MM-dd").format(this)


//日期字符串转时间戳
fun String.getTimeCurr(): Long = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this).time

fun String.getTimeCurrYMD(): Long = SimpleDateFormat("yyyy-MM-dd").parse(this).time


inline fun <reified T : Any> getList(json: String): T {
    return Gson().fromJson(json, object : TypeToken<T>() {}.type)
}

//TODO 关键字 inline内联函数
// 不在传入 T 的class，inline 的作用就是将函数插入到被调用处，配合 reified 就可以获取到 T 真正的类型
inline fun <reified T : Any> fromJson(json: String): T {
    return Gson().fromJson(json, T::class.java)
}














