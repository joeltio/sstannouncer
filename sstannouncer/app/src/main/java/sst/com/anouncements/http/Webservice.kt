package sst.com.anouncements.http

interface Webservice {
    fun get(url: String): String
}