package com.appunite.loudius.domain

import android.content.SharedPreferences

class FakeSharedPreferences : SharedPreferences {

    private val map = mutableMapOf<String, Any?>()

    private inner class Editor : SharedPreferences.Editor {
        private val updates = mutableMapOf<String, Any?>()
        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            updates[key] = value
            return this
        }

        override fun putStringSet(key: String, value: MutableSet<String>?): SharedPreferences.Editor =
            TODO("Not yet implemented")

        override fun putInt(key: String, value: Int): SharedPreferences.Editor =
            TODO("Not yet implemented")

        override fun putLong(key: String, value: Long): SharedPreferences.Editor =
            TODO("Not yet implemented")

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor =
            TODO("Not yet implemented")

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor =
            TODO("Not yet implemented")

        override fun remove(key: String?): SharedPreferences.Editor = TODO("Not yet implemented")

        override fun clear(): SharedPreferences.Editor = TODO("Not yet implemented")

        override fun commit(): Boolean {
            apply()
            return true
        }

        override fun apply() {
            map.putAll(updates)
        }

    }
    override fun getAll(): MutableMap<String, *> = TODO("Not yet implemented")

    override fun getString(key: String?, defaultValue: String?): String? = map[key] as String? ?: defaultValue

    override fun getStringSet(p0: String?, p1: MutableSet<String>?): MutableSet<String>? =
        TODO("Not yet implemented")

    override fun getInt(key: String?, defaultValue: Int): Int = TODO("Not yet implemented")

    override fun getLong(key: String?, defaultValue: Long): Long = TODO("Not yet implemented")

    override fun getFloat(key: String?, defaultValue: Float): Float = TODO("Not yet implemented")

    override fun getBoolean(key: String?, defaultValue: Boolean): Boolean = TODO("Not yet implemented")

    override fun contains(key: String?): Boolean = TODO("Not yet implemented")

    override fun edit(): SharedPreferences.Editor = Editor()

    override fun registerOnSharedPreferenceChangeListener(p0: SharedPreferences.OnSharedPreferenceChangeListener?): Unit =
        TODO("Not yet implemented")

    override fun unregisterOnSharedPreferenceChangeListener(p0: SharedPreferences.OnSharedPreferenceChangeListener?): Unit =
        TODO("Not yet implemented")

}