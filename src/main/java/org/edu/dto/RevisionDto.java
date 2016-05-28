package org.edu.dto;

/**
 * DTO for Revision tag in Wikipedia XML dumps.
 * 
 * @see {@link ContributorDto}
 * @author shivam.maharshi
 */
public class RevisionDto {

	private int id;
	private int parentId;
	private String timestamp;
	private ContributorDto contributor;
	private String minor;
	private String comment;
	private String model;
	private String format;
	private String text;
	private String sha1;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public ContributorDto getContributor() {
		return contributor;
	}

	public void setContributor(ContributorDto contributor) {
		this.contributor = contributor;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSha1() {
		return sha1;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}

	public String getMinor() {
		return minor;
	}

	public void setMinor(String minor) {
		this.minor = minor;
	}

	@Override
	public String toString() {
		return "Revision [id=" + id + ", parentId=" + parentId + ", timestamp=" + timestamp + ", contributor="
				+ contributor + ", minor=" + minor + ", comment=" + comment + ", model=" + model + ", format=" + format
				+ ", text=" + text + ", sha1=" + sha1 + "]";
	}

}
