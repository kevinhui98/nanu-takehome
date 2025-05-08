package com.example.nanutakehome

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform