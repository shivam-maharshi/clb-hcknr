package org.edu.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.edu.dto.ContributorDto;
import org.edu.dto.PageDto;
import org.edu.dto.RevisionDto;
import org.edu.utils.FileUtil;
import org.edu.utils.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;

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
	
	public static void addFailedTitle(String title) {
		failedTitles.add(title);
	}
	
	public static void addVoilationTitle(String title) {
		voilationTitles.add(title);
	}

	public static void read(String file, String fail) {
		try {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// To read the complete big data in text. Otherwise it doesn't.
			inputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);
			InputStream in = new FileInputStream(file);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF-8");
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
					try {
						HibernateUtil.closeSessionFactory();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
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
							if(!endEventIs(event, TITLE))
							page.setTitle(event.asCharacters().getData());
						}
						if (startEventIs(event, NS)) {
							event = eventReader.nextEvent();
							if(!endEventIs(event, NS))
							page.setNs(Integer.valueOf(event.asCharacters().getData()));
						}
						if (startEventIs(event, ID)) {
							event = eventReader.nextEvent();
							if(!endEventIs(event, ID))
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
								if(!endEventIs(event, ID))
								revision.setId(Integer.valueOf(event.asCharacters().getData()));
							}
							if (startEventIs(event, PARENTID)) {
								event = eventReader.nextEvent();
								if(!endEventIs(event, PARENTID))
								revision.setParentId(Integer.valueOf(event.asCharacters().getData()));
							}
							if (startEventIs(event, TIMESTAMP)) {
								event = eventReader.nextEvent();
								if(!endEventIs(event, TIMESTAMP))
								revision.setTimestamp(event.asCharacters().getData());
							}
							if (startEventIs(event, CONTRIBUTOR)) {
								inContributorTag = true;
								contributor = new ContributorDto();
								revision.setContributor(contributor);
							}
							if (startEventIs(event, MINOR)) {
								event = eventReader.nextEvent();
								if(!endEventIs(event, MINOR))
									revision.setMinor(event.asCharacters().getData());
							}
							if (startEventIs(event, COMMENT)) {
								event = eventReader.nextEvent();
								if(!endEventIs(event, COMMENT))
								revision.setComment(event.asCharacters().getData());
							}
							if (startEventIs(event, MODEL)) {
								event = eventReader.nextEvent();
								if(!endEventIs(event, MODEL))
								revision.setModel(event.asCharacters().getData());
							}
							if (startEventIs(event, FORMAT)) {
								event = eventReader.nextEvent();
								if(!endEventIs(event, FORMAT))
								revision.setFormat(event.asCharacters().getData());
							}
							if (startEventIs(event, TEXT)) {
								Attribute att =  event.asStartElement().getAttributeByName(new QName(BYTES));
								if(att!=null) {
									page.setLength(Integer.valueOf(att.getValue()));
								}
								event = eventReader.nextEvent();
								if(!endEventIs(event, TEXT))
								revision.setText(event.asCharacters().getData());
							}
							if (startEventIs(event, SHA1)) {
								event = eventReader.nextEvent();
								if(!endEventIs(event, SHA1))
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
								if(!endEventIs(event, USERNAME))
								contributor.setUsername(event.asCharacters().getData());
							}
							if (startEventIs(event, ID)) {
								event = eventReader.nextEvent();
								if(!endEventIs(event, ID))
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
						// event = eventReader.nextEvent(); - TODO: For speed.
						// Consumer here.
						try {
							ConsumerQueue.add(page);
						} catch (Exception e) {
							if(e instanceof ConstraintViolationException) {
								voilationTitles.add("V | " + page.getTitle());
							} else {
								failedTitles.add(page.getTitle());
								continue;
							}
//							try {
//								HibernateUtil.closeSessionFactory();
//							} catch (Exception e1) {
//								e1.printStackTrace();
//							}
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
		long ts = System.currentTimeMillis();
			read("C:/Users/Sam/Downloads/elwiki-20160501-pages-meta-history.xml", "failed.txt");
			System.out.println("Time taken (in mins) : "+(System.currentTimeMillis() - ts)/60000);
	}

	private static boolean startEventIs(XMLEvent event, String name) {
		return event.isStartElement() && event.asStartElement().getName().getLocalPart().equals(name);
	}

	private static boolean endEventIs(XMLEvent event, String name) {
		return event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(name);
	}

}