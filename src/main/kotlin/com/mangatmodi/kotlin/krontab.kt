package com.mangatmodi.kotlin

import dev.inmo.krontab.doInfinity
import kotlinx.coroutines.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/*
* PROS
* ====
* 1. Unhandled Exception will kill the couroutine and hence the app.
* 2. Fully customize the queue
* 3. Crontab syntax
* CONS
* ====
* 1. Less popular library
* 2. Coroutines?
* */

fun main() {
    val task = ThreadPoolExecutor(
        1,
        1,
        Long.MAX_VALUE,
        TimeUnit.MILLISECONDS,
        ArrayBlockingQueue(1, true),
        ThreadPoolExecutor.CallerRunsPolicy()
    )

    val scope = CoroutineScope(task.asCoroutineDispatcher())
    scope.launch {
        doInfinity("/2 * * * *") {
            error("Failure")
        }
    }
        .invokeOnCompletion {
            println("Unhandled exception")
            scope.cancel()
            task.shutdownNow()
        }


    Runtime.getRuntime().addShutdownHook(
        Thread {
            println("Shutting down")
            scope.cancel()
            task.shutdownNow()
        }
    )
}