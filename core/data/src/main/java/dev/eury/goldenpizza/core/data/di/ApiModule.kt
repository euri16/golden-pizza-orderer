package dev.eury.goldenpizza.core.data.di

import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.eury.goldenpizza.core.data.api.PizzaApi
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal class ApiModule {
    @Provides
    @Reusable
    fun pizzaApi(retrofit: Retrofit) : PizzaApi =
        retrofit.create(PizzaApi::class.java)
}