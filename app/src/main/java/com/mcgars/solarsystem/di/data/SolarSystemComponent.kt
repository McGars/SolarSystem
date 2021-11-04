package com.mcgars.solarsystem.di.data

import com.mcgars.solarsystem.data.repository.SolarSystemRepository
import com.mcgars.solarsystem.domain.usecase.SolarSystemUseCase
import dagger.Binds
import dagger.Component
import dagger.Module
import javax.inject.Singleton

fun createSolarSystemComponent(): SolarSystemComponent =
    DaggerSolarSystemComponent.builder().build()

@Component(
    modules = [SolarSystemComponentModule::class]
)
@Singleton
interface SolarSystemComponent : SolarSystemComponentApi

@Module
abstract class SolarSystemComponentModule {

    @Binds
    @Singleton
    abstract fun bindSolarSystemUseCase(impl: SolarSystemUseCase.Impl): SolarSystemUseCase

    @Binds
    @Singleton
    abstract fun bindSolarSystemRepository(impl: SolarSystemRepository.Impl): SolarSystemRepository

}