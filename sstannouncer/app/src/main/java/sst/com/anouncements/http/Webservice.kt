package sst.com.anouncements.http

interface Webservice {
    fun get(url: String): String
    fun headLastModified(url: String): Long
}