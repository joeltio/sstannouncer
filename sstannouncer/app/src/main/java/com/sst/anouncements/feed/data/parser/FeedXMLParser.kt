package com.sst.anouncements.feed.data.parser

import com.sst.anouncements.feed.model.Feed
import com.sst.anouncements.feed.data.parser.w3dom.w3domParse

// "Interface"ing function
fun parse(XML: String): Feed = w3domParse(XML)
