package com.example.petshop.db

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseFactory {
    const val DEFAULT_URL = "jdbc:h2:mem:ktor_petshop;DB_CLOSE_DELAY=-1;MODE=PostgreSQL"

    fun init(jdbcUrl: String = DEFAULT_URL) {
        Database.connect(jdbcUrl, driver = "org.h2.Driver", user = "sa", password = "")
        transaction { SchemaUtils.create(PetsTable) }
    }
}
