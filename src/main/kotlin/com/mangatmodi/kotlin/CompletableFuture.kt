package com.mangatmodi.kotlin

import java.lang.Thread.sleep
import java.util.concurrent.CompletableFuture

fun main() {
    val result = CompletableFuture.allOf(
        CompletableFuture.supplyAsync { 1 },
        CompletableFuture.supplyAsync {
            sleep(1000)
            error("Failure1")
        },
        CompletableFuture.supplyAsync { error("Failure2") },
    )

    runCatching { result.join() }
        .onFailure {
            //TODO: Failure metrics
            println(it)
        }
        .onSuccess {
            //TODO: Success metrics
            println(it)
        }
}