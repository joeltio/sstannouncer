package com.sst.anouncements.feed.data.parser.w3dom

import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory


// Create XML from String
fun createXMLDocument(XML: String): Document {
    val dbFactory = DocumentBuilderFactory.newInstance()
    val dBuilder = dbFactory.newDocumentBuilder()
    return dBuilder.parse(InputSource(XML.byteInputStream()))
}

// Create XPathExpression object from XPath
fun createXPath(XPath: String): XPathExpression {
    val xPathfactory = XPathFactory.newInstance()
    val xpath = xPathfactory.newXPath()
    return xpath.compile(XPath)
}


// General XPath functions (with different return types)
private fun xpathAnyString(o: Any, XPath: String): String {
    val xpathExpression = createXPath(XPath)
    return xpathExpression.evaluate(o, XPathConstants.STRING) as String
}

private fun xpathAnyNodeList(o: Any, XPath: String): NodeList {
    val xpathExpression = createXPath(XPath)
    return xpathExpression.evaluate(o, XPathConstants.NODESET) as NodeList
}

private fun xpathAnyDate(o: Any, XPath: String) = parseDate(xpathAnyString(o, XPath))


// Use extension functions for Document and Node
fun Document.xpathString(XPath: String): String = xpathAnyString(this, XPath)
fun Document.xpathNodeList(XPath: String) = xpathAnyNodeList(this, XPath)
fun Document.xpathDate(XPath: String) = xpathAnyDate(this, XPath)

fun Node.xpathString(XPath: String): String = xpathAnyString(this, XPath)
fun Node.xpathNodeList(XPath: String) = xpathAnyNodeList(this, XPath)
fun Node.xpathDate(XPath: String) = xpathAnyDate(this, XPath)

