package com.example.petshop.di

import com.example.petshop.db.ExposedPetRepository
import com.example.petshop.domain.PetRepository
import org.koin.dsl.module

val appModule = module { single<PetRepository> { ExposedPetRepository() } }
