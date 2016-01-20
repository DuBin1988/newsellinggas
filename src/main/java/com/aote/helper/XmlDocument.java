package com.aote.helper;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

/**
 * xml文档对象，加载一个xml文档
 */
public class XmlDocument {

	public XmlDocument() {
	}

	public String path = "";

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * XML文档
	 */
	public Document document = null;

	
	public Document getDocument() {
		try {
			if (this.document == null) {
				ClassLoader loader = Thread.currentThread()
						.getContextClassLoader();
				InputStream is = loader.getResourceAsStream(this.path);
				DocumentBuilder builder = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();
				document = builder.parse(is);
//				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//				DocumentBuilder db = dbf.newDocumentBuilder();
//				document = db.parse(new File(this.path));
 			}
			return this.document;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
