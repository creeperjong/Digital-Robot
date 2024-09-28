package com.example.digitalrobot.util

import com.google.gson.internal.LinkedTreeMap
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

fun <T: Any> getPropertyValue(
    obj: Any,
    propertyName: String,
    expectedType: KClass<T>
): T? {
    val prop = obj::class.memberProperties.find { it.name == propertyName }

    return prop?.let {
        val value = it.getter.call(obj)
        if (expectedType.isInstance(value)) {
            @Suppress("UNCHECKED_CAST")
            value as T
        } else {
            null
        }
    }
}

fun <T : Any> getNestedPropertyValue(
    obj: Any,
    propertyPath: String,
    expectedType: KClass<T>
): T? {
    val props = propertyPath.split('.')

    var currentValue: Any? = obj
    for (propertyName in props) {
        val property = currentValue?.let {
            it::class.memberProperties.find { prop -> prop.name == propertyName }
        }
        currentValue = property?.getter?.call(currentValue) ?: return null
    }

    return if (expectedType.isInstance(currentValue)) {
        @Suppress("UNCHECKED_CAST")
        currentValue as T
    } else {
        null
    }
}

fun <T : Any> getValueFromLinkedTreeMap(
    map: LinkedTreeMap<*, *>,
    key: String,
    expectedType: KClass<T>
): T? {
    val value = map[key] ?: return null

    return if (expectedType.isInstance(value)) {
        @Suppress("UNCHECKED_CAST")
        value as T
    } else {
        null
    }
}

fun <T : Any> getNestedValueFromLinkedTreeMap(
    map: LinkedTreeMap<*, *>,
    nestedKey: String,
    expectedType: KClass<T>
): T? {
    val keys = nestedKey.split('.')

    var currentMap: Any? = map
    for (key in keys) {
        currentMap = (currentMap as? LinkedTreeMap<*, *>)?.get(key) ?: return null
    }

    return if (expectedType.isInstance(currentMap)) {
        @Suppress("UNCHECKED_CAST")
        currentMap as T
    } else {
        null
    }
}