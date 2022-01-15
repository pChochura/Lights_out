package com.pointlessapps.lightsout.converters

import androidx.room.TypeConverter
import com.pointlessapps.lightsout.providers.Provider
import com.pointlessapps.lightsout.providers.ProviderWiz
import kotlin.reflect.full.createInstance

class ConverterProvider {

    @TypeConverter
    fun fromProvider(provider: Provider): String {
        val clazz = Provider::class.sealedSubclasses.find { it.isInstance(provider) }
        return clazz?.qualifiedName.toString()
    }

    @TypeConverter
    fun toProvider(qualifiedName: String): Provider {
        val clazz = Provider::class.sealedSubclasses.find { it.qualifiedName == qualifiedName }
        return clazz?.createInstance() ?: ProviderWiz()
    }
}