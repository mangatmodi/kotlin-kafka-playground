package com.mangatmodi.kotlin

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

//handle and whenComplete symantics
fun main() {
    val executor = Executors.newFixedThreadPool(20)
    val future = supplyAsync({ 1 }, executor)
        .thenApply { println(Thread.currentThread().name) }
        .handle { _, _ ->
            println(Thread.currentThread().name)
            error("From handle")
        }
        .whenComplete { _, _ ->
            println(Thread.currentThread().name)
            error("From whenComplete")
        }
        .handle { _, th -> println(th.message) }

    executor.submit { future.join() }
    executor.shutdown()
    executor.awaitTermination(100, TimeUnit.SECONDS)
}