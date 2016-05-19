package org.edu.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.edu.dto.Contributor;
import org.edu.dto.Page;
import org.edu.dto.Revision;

/**
 * Core parsing logic to read Wikipedia XML dumps and pass objects to consumers.
 * 
 * @author shivam.maharshi
 */
public class StaxParser {

	private static final String PAGE = "page";
	private static final String TITLE = "title";
	private static final String NS = "ns";
	private static final String ID = "id";
	private static final String REVISION = "revision";
	private static final String PARENTID = "parentid";
	private static final String TIMESTAMP = "timestamp";
	private static final String CONTRIBUTOR = "contributor";
	private static final String USERNAME = "username";
	private static final String MINOR = "minor";
	private static final String COMMENT = "comment";
	private static final String MODEL = "model";
	private static final String FORMAT = "format";
	private static final String TEXT = "text";
	private static final String SHA1 = "sha1";

	public static void read(String file) {
		try {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			InputStream in = new FileInputStream(file);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			boolean inRevisionTag = false;
			boolean inPageTag = false;
			boolean inContributorTag = false;
			Page page = null;
			Revision revision = null;
			Contributor contributor = null;

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (startEventIs(event, PAGE)) {
					inPageTag = true;
					page = new Page();
				}
				if (inPageTag) {
					if (startEventIs(event, TITLE)) {
						event = eventReader.nextEvent();
						page.setTitle(event.asCharacters().getData());
					}
					if (startEventIs(event, NS)) {
						event = eventReader.nextEvent();
						page.setNs(Integer.valueOf(event.asCharacters().getData()));
					}
					if (startEventIs(event, ID)) {
						event = eventReader.nextEvent();
						page.setId(Integer.valueOf(event.asCharacters().getData()));
					}
					if (startEventIs(event, REVISION)) {
						inRevisionTag = true;
						revision = new Revision();
						page.setRevision(revision);
					}
					if (inRevisionTag) {
						if (startEventIs(event, ID)) {
							event = eventReader.nextEvent();
							revision.setId(Integer.valueOf(event.asCharacters().getData()));
						}
						if (startEventIs(event, PARENTID)) {
							event = eventReader.nextEvent();
							revision.setParentId(Integer.valueOf(event.asCharacters().getData()));
						}
						if (startEventIs(event, TIMESTAMP)) {
							event = eventReader.nextEvent();
							revision.setTimestamp(event.asCharacters().getData());
						}
						if (startEventIs(event, CONTRIBUTOR)) {
							inContributorTag = true;
							contributor = new Contributor();
							revision.setContributor(contributor);
						}
						if (inContributorTag) {
							if (startEventIs(event, USERNAME)) {
								event = eventReader.nextEvent();
								contributor.setUsername(event.asCharacters().getData());
							}
							if (startEventIs(event, ID)) {
								event = eventReader.nextEvent();
								contributor.setId(Integer.valueOf(event.asCharacters().getData()));
							}
							if (endEventIs(event, CONTRIBUTOR)) {
								inContributorTag = false;
								event = eventReader.nextEvent();
							}
						}
						if (startEventIs(event, MINOR)) {
							event = eventReader.nextEvent();
							revision.setMinor(event.asCharacters().getData());
						}
						if (startEventIs(event, COMMENT)) {
							event = eventReader.nextEvent();
							revision.setComment(event.asCharacters().getData());
						}
						if (startEventIs(event, MODEL)) {
							event = eventReader.nextEvent();
							revision.setModel(event.asCharacters().getData());
						}
						if (startEventIs(event, FORMAT)) {
							event = eventReader.nextEvent();
							revision.setFormat(event.asCharacters().getData());
						}
						if (startEventIs(event, TEXT)) {
							event = eventReader.nextEvent();
							revision.setText(event.asCharacters().getData());
						}
						if (startEventIs(event, SHA1)) {
							event = eventReader.nextEvent();
							revision.setSha1(event.asCharacters().getData());
						}
						if (endEventIs(event, REVISION)) {
							inRevisionTag = false;
							event = eventReader.nextEvent();
						}
					}
					if (endEventIs(event, PAGE)) {
						inPageTag = false;
						event = eventReader.nextEvent();
						// Consumer here.
						System.out.println(page);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		read("WikiDump.xml");
	}

	private static boolean startEventIs(XMLEvent event, String name) {
		return event.isStartElement() && event.asStartElement().getName().getLocalPart().equals(name);
	}

	private static boolean endEventIs(XMLEvent event, String name) {
		return event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(name);
	}

}