@file:OptIn(ObsoleteCoroutinesApi::class)

package com.mangatmodi.kotlin

import com.google.common.util.concurrent.ThreadFactoryBuilder
import kotlinx.coroutines.*
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors


suspend fun await(duration: Duration, fn: () -> CompletableFuture<Unit>) =
    withContext(CoroutineScope(newSingleThreadContext("Ktor Context")).coroutineContext) {
        withTimeout(duration.toMillis()) {
            println("awaiting in ${Thread.currentThread().name}")
            fn()
        }
    }

fun main() {
    runBlocking {
        await(Duration.ofSeconds(10)) {
            CompletableFuture.supplyAsync(
                {
                    println("Sleeping in ${Thread.currentThread().name}")
                    Thread.sleep(2000)
                },
                Executors.newSingleThreadExecutor(ThreadFactoryBuilder().setNameFormat("db-thread-%d").build())
            )
        }
    }
}