package com.example.petshop.db

import org.jetbrains.exposed.v1.core.Table

object PetsTable : Table("pets") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 80)
    val species = varchar("species", 40)
    override val primaryKey = PrimaryKey(id)
}
