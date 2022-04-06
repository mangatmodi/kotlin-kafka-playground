package com.mangatmodi.kotlin

import java.util.*

class Data<T> {
    private val optionalList: MutableList<Optional<T>> = mutableListOf()
    fun insert(t: T) = optionalList.add(Optional.ofNullable(t))
}

fun main() {
    val d = Data<String?>()
    d.insert("hello")
    d.insert(null)
    println(d)
}
