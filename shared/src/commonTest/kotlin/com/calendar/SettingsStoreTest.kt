package com.calendar

import com.calendar.ui.AppSettings
import com.calendar.ui.KeyValueStore
import com.calendar.ui.SettingsStore
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsStoreTest {
    private class MemoryStore : KeyValueStore {
        val m = HashMap<String, Any>()
        override fun getString(key: String) = m[key] as String?
        override fun getBoolean(key: String, default: Boolean) = m[key] as Boolean? ?: default
        override fun getLong(key: String, default: Long) = m[key] as Long? ?: default
        override fun putString(key: String, value: String) { m[key] = value }
        override fun putBoolean(key: String, value: Boolean) { m[key] = value }
        override fun putLong(key: String, value: Long) { m[key] = value }
    }

    @Test fun savesAndLoadsSettings() {
        val store = SettingsStore(MemoryStore())
        assertEquals(AppSettings.AUTO, store.load().skinId)
        store.save(AppSettings(skinId = "apple"))
        assertEquals("apple", store.load().skinId)
    }
}
