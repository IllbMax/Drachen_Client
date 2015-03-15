package com.vsis.drachen;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * Class of resource managing. The resources used may vary with the concrete
 * system (client). It contains Media like Images and Texts by default
 */
public class ResourceService {

	public static final String ZIP_DIR_MAP = "map/";
	public static final String ZIP_DIR_MEDIA_IMG = "media/img/";
	public static final String ZIP_DIR_IMG = "img/";
	public static final String ZIP_DIR_MEDIA = "media/";

	public static final String ZIP_RES_STR = "map/strings.xml";

	protected Map<String, String> stringMap;
	protected String zipFile;

	public ResourceService() {
	}

	/**
	 * 
	 * zipfile
	 * 
	 * <code>
	 * <ul>
	 * 	map <ul>  
	 * 		strings.xml
	 *  </ul> 
	 * 	media <ul> 
	 * 		img <ul>
	 * 			img1.png <br> imgXY.jpg
	 * 		</ul>
	 * 		svg<ul>
	 * 			img3.svg
	 * 		</ul>
	 * 	</ul>
	 * </ul>
	 * </code>
	 * 
	 * @param filename
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public final void loadZip(String filename) throws IOException,
			ParserConfigurationException, SAXException {
		zipFile = filename;
		ZipFile file = new ZipFile(filename);
		ZipEntry strMap = file.getEntry(ZIP_RES_STR);
		readXML_StringResouce(file.getInputStream(strMap));

		file.close();
	}

	protected void loadOtherDataFromZip() {
	}

	/**
	 * reads the strings from the XML resource into the Map
	 * 
	 * @param stream
	 *            stream of the string xml
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private void readXML_StringResouce(InputStream stream)
			throws ParserConfigurationException, SAXException, IOException {

		stringMap = new HashMap<String, String>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(stream);

		// optional, but recommended
		// read this -
		// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();
		NodeList stringNodes = doc.getElementsByTagName("string");
		for (int i = 0; i < stringNodes.getLength(); i++) {
			Node node = stringNodes.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element strNode = (Element) node;
				String id = strNode.getAttribute("name");
				String value = strNode.getTextContent();
				stringMap.put(id, value);
			}
		}

	}

	/**
	 * Gets the string with Id id
	 * 
	 * @param id
	 *            idstring of the text/string resource
	 * @return the resource with the id
	 */
	public String getString(String id) {
		if (stringMap != null && stringMap.containsKey(id)) {
			return stringMap.get(id);
		} else
			return ""; // or null ? ..
	}

	/**
	 * Gets the ResourceObject for a Media resource with Id id
	 * 
	 * @param id
	 *            Id of the media resource
	 * @return the resource
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public ResourceStream getMediaResourceStream(String id) throws IOException,
			FileNotFoundException {
		return getMediaResourceStream(id, true);
	}

	/**
	 * Returns the Resource with Id id
	 * 
	 * @param id
	 *            Id of the resource
	 * @param inmedia
	 *            if true the desired resource belongs to the media resources
	 * @return The resourcestream to the resource with Id id (remember to close
	 *         the stream later)
	 * @throws FileNotFoundException
	 *             if the media resource does not exists
	 * @throws IOException
	 */
	public ResourceStream getMediaResourceStream(String id, boolean inmedia)
			throws FileNotFoundException, IOException {
		ZipFile file = new ZipFile(zipFile);
		String resourceName = (inmedia ? ZIP_DIR_MEDIA : "") + id;
		ZipEntry resource = file.getEntry(resourceName);
		if (resource != null)
			return new ResourceStream(file, file.getInputStream(resource));
		else {
			file.close();
			throw new FileNotFoundException();
		}
	}

	/**
	 * Class representing a resource in form of a {@link InputStream}
	 */
	public class ResourceStream implements Closeable {
		private InputStream stream;
		private ZipFile file;

		public InputStream getInputStream() {
			return stream;

		}

		protected ResourceStream(ZipFile file, InputStream stream) {
			this.file = file;
			this.stream = stream;
		}

		public void close() throws IOException {
			// close the zipfile of the stream
			file.close();
		}
	}

	public void dispose() {
		// nothing to dispose
	}
}
