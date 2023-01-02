@file:OptIn(ExperimentalCoroutinesApi::class)

package com.mangatmodi.kotlin

import com.google.common.util.concurrent.ThreadFactoryBuilder
import kotlinx.benchmark.Mode
import kotlinx.coroutines.*
import org.openjdk.jmh.annotations.*
import java.lang.Thread.sleep
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Semaphore
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * Purpose of this snippet is to prove that coroutines are not magic. They are an async pattern
 * just like Promises, Futures, Callback etc. They "magically" won't make things non-blocking.
 * Any blocking call will still block the underlying. Yes! Coroutines are scheduled over threads.
 */

suspend fun `coroutines are executed on threads, not on thin air`() {
    withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
        println(Thread.currentThread().name)
    }

    withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
        println(Thread.currentThread().name)
    }

    withContext(CoroutineScope(Executors.newFixedThreadPool(1).asCoroutineDispatcher()).coroutineContext) {
        println(Thread.currentThread().name)
    }
}


fun `coroutines will block the underlying threads`() {
    val lock = Semaphore(1)
    val executor = ThreadPoolExecutor(
        1,
        1,
        10,
        TimeUnit.SECONDS,
        LinkedBlockingQueue(),
        ThreadFactoryBuilder().setNameFormat("test-coroutines-block-%d").build()
    )
    println("lock acquired by ${Thread.currentThread().name}")
    lock.acquire()
    CoroutineScope(executor.asCoroutineDispatcher()).launch {
        println("Scheduled on ${Thread.currentThread().name}")

        // as lock is already locked, the thread will be blocked here
        lock.acquire()
    }
    Thread.getAllStackTraces().keys.forEach { println("State of ${it.name}: ${it.state}") }
}

suspend fun `large number of blocking actions will consume large number of threads`() {
    // As established earlier, coroutines does block the thread if the underlying
    // call is blocking. Making huge number of blocking calls will block all the
    // threads.
    val coroutines = (0 until 1000).map {
        CoroutineScope(Dispatchers.IO).async {
            sleep(10)
        }
    }

    // All the dispatcher threads are waiting.
    Thread.getAllStackTraces().keys
        .filter { it.name.contains("DefaultDispatcher") }
        .groupBy { it.state }
        .forEach { (state, list) -> println("Number of threads $state are ${list.size}") }

    coroutines.joinAll()
}

suspend fun `network IO is always blocking`() {
    // This is hard to prove as it depends on the underlying lib.
    // There are too many but all of them are blocking
}

fun main() {
    runBlocking {
//        `coroutines are executed on threads, not on thin air`()
//        `coroutines will block the underlying threads`()
        `large number of blocking actions will consume large number of threads`()
    }
}

/**
 * All 3 give similar results, however the one with coroutines was poor until we limited the parallelism.
 * This is due to the fact that it uses 64 threads by default which is quite a lot and leads to context switching.
 */

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 10)
@Measurement(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
class `CoroutinesDoesnotImproveThrouputOrLatency` {

    // 24,987 ±(99.9%) 0,458 ms/op [Average]
    @Benchmark
    fun withCoroutines() {
        runBlocking {
            val coroutines = (0 until 100).map {
                CoroutineScope(Dispatchers.IO).async {
                    //CoroutineScope(Dispatchers.IO.limitedParallelism(10)).async {
                    sleep(10)
                }
            }

            coroutines.joinAll()
        }
    }

    // 27,778 ±(99.9%) 1,574 ms/op [Average]
    @Benchmark
    fun withExecutorService() {
        val executors = Executors.newFixedThreadPool(60)
        (0 until 100).map {
            executors.submit { sleep(10) }
        }
        executors.shutdown()
        executors.awaitTermination(60, TimeUnit.SECONDS)
    }

    // 30,235 ±(99.9%) 3,407 ms/op [Average]
    @Benchmark
    fun threadPoolAsDispatcher() {
        val executors = Executors.newFixedThreadPool(60)
        runBlocking {
            val coroutines = (0 until 100).map {
                CoroutineScope(executors.asCoroutineDispatcher()).async {
                    sleep(10)
                }
            }
            coroutines.joinAll()
        }
        executors.shutdown()
        executors.awaitTermination(60, TimeUnit.SECONDS)
    }
}