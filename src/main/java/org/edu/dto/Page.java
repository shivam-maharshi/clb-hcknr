package org.edu.dto;

/**
 * DTO for Page tag in Wikipedia XML dumps.
 * 
 * @see {@link Revision}
 * @author shivam.maharshi
 */
public class Page {

	private String title;
	private int ns;
	private int id;
	private Revision revision;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getNs() {
		return ns;
	}

	public void setNs(int ns) {
		this.ns = ns;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Revision getRevision() {
		return revision;
	}

	public void setRevision(Revision revision) {
		this.revision = revision;
	}

	@Override
	public String toString() {
		return "Page [title=" + title + ", ns=" + ns + ", id=" + id + ", revision=" + revision + "]";
	}
	
}
