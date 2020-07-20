package com.sst.anouncements.feed.data.parser.w3dom

import org.w3c.dom.Document
import org.w3c.dom.Node
import com.sst.anouncements.feed.model.Entry
import com.sst.anouncements.feed.model.Feed
import java.util.*


// Parse parts of the feed from the document
private fun parseFeedId(document: Document) = document.xpathString("/feed/id/text()")

private fun parseEntry(entryNode: Node) =
    Entry(
        entryNode.xpathString("./id/text()"),
        entryNode.xpathDate("./published/text()"),
        entryNode.xpathDate("./updated/text()"),
        entryNode.xpathString("./author/name/text()"),
        entryNode.xpathString("./link[@rel='alternate']/@href"),
        entryNode.xpathString("./title/text()"),
        entryNode.xpathString("./content/text()")
    )

private fun parseEntries(document: Document): List<Entry> {
    val nodeList = document.xpathNodeList("/feed/entry")
    return List(nodeList.length) {
        parseEntry(nodeList.item(it))
    }
}

private fun parseLastUpdated(document: Document): Date = document.xpathDate("/feed/updated/text()")

private fun parseCategories(document: Document): List<String> {
    val nodeList = document.xpathNodeList("/feed/category")
    return List(nodeList.length) {
        nodeList.item(it).xpathString("/category/@term")
    }
}

private fun parseTitle(document: Document) = document.xpathString("/feed/title/text()")

private fun parseSubtitle(document: Document) = document.xpathString("/feed/subtitle/text()")


fun w3domParse(XML: String): Feed {
    val document = createXMLDocument(XML)

    val id = parseFeedId(document)
    val entries = parseEntries(document)
    val lastUpdated = parseLastUpdated(document)
    val categories = parseCategories(document)
    val title = parseTitle(document)
    val subtitle = parseSubtitle(document)
    return Feed(id, entries, lastUpdated, categories, title, subtitle)
}
