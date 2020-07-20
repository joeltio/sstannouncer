package sst.com.anouncements.http

import java.net.HttpURLConnection
import java.net.URL

class DefaultWebservice : Webservice {
    override fun get(url: String): String {
        return URL(url).readText()
    }

    override fun headLastModified(url: String): Long {
        return with(URL(url).openConnection() as HttpURLConnection) {
            requestMethod = "HEAD"
            lastModified
        }
    }
}