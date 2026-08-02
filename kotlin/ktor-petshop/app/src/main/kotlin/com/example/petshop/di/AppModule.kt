package com.example.petshop.di

import com.example.petshop.PetRepository
import com.example.petshop.db.ExposedPetRepository
import org.koin.dsl.module

val appModule = module {
    single<PetRepository> { ExposedPetRepository() }
}
