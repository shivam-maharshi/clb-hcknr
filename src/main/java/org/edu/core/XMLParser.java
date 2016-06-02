package org.edu.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.RollbackException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.edu.dto.ContributorDto;
import org.edu.dto.PageDto;
import org.edu.dto.RevisionDto;
import org.edu.utils.FileUtil;
import org.edu.utils.HibernateUtil;
import org.hibernate.HibernateException;

/**
 * Core parsing logic to read Wikipedia XML dumps and pass objects to consumers.
 * This is a STAX parser.
 * 
 * @see {@link PageDto}, {@link RevisionDto}, {@link ContributorDto}
 * @author shivam.maharshi
 */
public class XMLParser {

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
	private static final String BYTES = "bytes";
	private static final String MEDIAWIKI = "mediawiki";
	private static final List<String> failedTitles = new ArrayList<String>();
	private static final List<String> voilationTitles = new ArrayList<String>();

	public static void read(String file, String fail) {
		try {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			inputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);
			InputStream in = new FileInputStream(file);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			boolean inRevisionTag = false;
			boolean inPageTag = false;
			boolean inContributorTag = false;
			PageDto page = null;
			RevisionDto revision = null;
			ContributorDto contributor = null;

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if(endEventIs(event, MEDIAWIKI)) {
					FileUtil.write(failedTitles, fail);
					FileUtil.write(voilationTitles, fail);
					break;
				}
				if (startEventIs(event, PAGE)) {
					inPageTag = true;
					page = new PageDto();
				}
				// Page tag starts
				if (inPageTag) {
					if (!inRevisionTag) {
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
							revision = new RevisionDto();
							page.setRevision(revision);
						}
					} else if (inRevisionTag) {
						// Revision tag starts
						if (!inContributorTag) {
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
								contributor = new ContributorDto();
								revision.setContributor(contributor);
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
								page.setLength(Integer.valueOf(event.asStartElement().getAttributeByName(new QName(BYTES)).getValue()));
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
						} else if (inContributorTag) {
							// Contributor tag starts
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
					}
					if (endEventIs(event, PAGE)) {
						inPageTag = false;
						event = eventReader.nextEvent();
						// Consumer here.
						try {
							if (!XMLConsumer.consume(page)) {
								failedTitles.add(page.getTitle());
							}
						} catch (Exception e) {
							if(e instanceof RollbackException) {
								voilationTitles.add("V | " + page.getTitle());
							} else {
								failedTitles.add(page.getTitle());
								continue;
							}
							try {
								HibernateUtil.closeSessionFactory();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws HibernateException, Exception {
			read("WikiDump.xml", "failed.txt");
	}

	private static boolean startEventIs(XMLEvent event, String name) {
		return event.isStartElement() && event.asStartElement().getName().getLocalPart().equals(name);
	}

	private static boolean endEventIs(XMLEvent event, String name) {
		return event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(name);
	}

}