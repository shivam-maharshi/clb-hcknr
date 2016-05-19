package org.edu.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO for Revision tag in Wikipedia XML dumps.
 * 
 * @see {@link Contributor}
 * @author shivam.maharshi
 */
public class Revision {

	private static Map<String, String> fieldLookup = new HashMap<String, String>();

	private int id;
	private int parentId;
	private String timestamp;
	private Contributor contributor;
	private String comment;
	private String model;
	private String format;
	private String text;
	private String sha1;

}
