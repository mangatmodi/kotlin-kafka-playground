package com.mangatmodi.ktor

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.netty.handler.codec.http.HttpServerCodec

// KTOR-4129 Ktor Server: Bad Request when using Authentication header.
// https://youtrack.jetbrains.com/issue/KTOR-4129

fun main() {
    embeddedServer(Netty, 8080, configure = {

        // This solves it!
        httpServerCodec = {
            //15 KB each
            HttpServerCodec(15360, 15360, 15360)
        }
    }) {
        routing {
            get("ping") {
                call.respondText("pong", ContentType.Text.Plain, HttpStatusCode.OK)
            }
        }
    }.apply {
        start()
        Runtime.getRuntime().addShutdownHook(
            Thread {
                stop(10, 100)
            }
        )
    }
}