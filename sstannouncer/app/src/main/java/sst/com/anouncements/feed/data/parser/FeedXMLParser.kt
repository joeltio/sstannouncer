package sst.com.anouncements.feed.data.parser

import sst.com.anouncements.feed.data.Feed
import sst.com.anouncements.feed.data.parser.w3dom.w3domParse

// "Interface"ing function
fun parse(XML: String): Feed = w3domParse(XML)
