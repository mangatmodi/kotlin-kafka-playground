package com.mangatmodi.kotlin

import java.util.concurrent.CompletableFuture


fun main() {
    val future = CompletableFuture.supplyAsync { 1 }
        .whenComplete { _, _ -> error("Error in Handle")  }
        println(future.isCompletedExceptionally)
}