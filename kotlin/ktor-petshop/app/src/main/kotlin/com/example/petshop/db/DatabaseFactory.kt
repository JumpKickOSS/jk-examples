package com.example.petshop.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(jdbcUrl: String = "jdbc:h2:mem:ktor_petshop;DB_CLOSE_DELAY=-1;MODE=PostgreSQL") {
        Database.connect(jdbcUrl, driver = "org.h2.Driver", user = "sa", password = "")
        transaction {
            SchemaUtils.create(PetsTable)
        }
    }
}
