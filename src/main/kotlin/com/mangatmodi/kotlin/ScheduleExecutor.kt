package com.mangatmodi.kotlin

import com.google.common.util.concurrent.ThreadFactoryBuilder
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.random.Random


/*
* PROS
* ====
* 1. No coroutines. Easy to understand paradigm
* 2. Part of the standard lib
* CONS
* ====
* 1. Unhandled Exception silently kill the thread and make the app stuck
* 2. No way to specify the queue size.
* */
fun main() {
    val scheduler = ScheduledThreadPoolExecutor(
        1,
        ThreadFactoryBuilder().build(),
        ThreadPoolExecutor.CallerRunsPolicy(),
    )

    scheduler.scheduleAtFixedRate(
        {
            if (Random(1000000).nextInt(10) < 5) {
                println("called")
            } else {
                error("Some Error")
            }
        },
        0,
        2,
        TimeUnit.SECONDS,
    )


    Runtime.getRuntime().addShutdownHook(
        Thread {
            println("Shutting down")
            scheduler.shutdownNow()
        }
    )
}