package sstinc.sstannouncer.Feed;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XML {
    private String rawXml;
    private Document xmlDocument;
    public XML() {
        this.rawXml = "";
    }

    private XML(String rawXml) throws IOException, ParserConfigurationException, SAXException {
        this.rawXml = rawXml;
        this.xmlDocument = createDocument(convertStringToStream(this.rawXml));
    }

    private String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private InputStream convertStringToStream(String s) {
        return new ByteArrayInputStream(s.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    private String convertNodeToString(Node n) throws TransformerException {
        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(n), new StreamResult(writer));
        return writer.toString();
    }

    private XPathExpression createXPath(String xpathString) throws XPathExpressionException {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        return xpath.compile(xpathString);
    }

    private Document createDocument(InputStream is) throws IOException, ParserConfigurationException,
            SAXException{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(is);
    }

    public String getRawXML() {
        return this.rawXml;
    }

    public void fetch(String urlString) throws IOException, ParserConfigurationException,
            SAXException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.connect();
        this.rawXml = convertStreamToString(urlConnection.getInputStream());
        this.xmlDocument = createDocument(convertStringToStream(this.rawXml));
    }

    public String xpathString(String xpathString) throws XPathExpressionException {
        XPathExpression xpath = createXPath(xpathString);
        return (String) xpath.evaluate(this.xmlDocument, XPathConstants.STRING);
    }

    public XML xpathNode(String xpathString) throws XPathExpressionException, TransformerException,
            IOException, ParserConfigurationException, SAXException {
        XPathExpression xpath = createXPath(xpathString);

        Node node = (Node) xpath.evaluate(this.xmlDocument, XPathConstants.NODE);
        String nodeXml = convertNodeToString(node);

        return new XML(nodeXml);
    }
}
