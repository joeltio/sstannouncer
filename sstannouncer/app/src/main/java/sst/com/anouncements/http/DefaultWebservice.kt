package sst.com.anouncements.http

import java.net.URL

class DefaultWebservice : Webservice {
    override fun get(url: String): String {
        return URL(url).readText()
    }
}