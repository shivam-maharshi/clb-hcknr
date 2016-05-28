package org.edu.dto;

/**
 * DTO for Contributor tag in Wikipedia XML dumps.
 * 
 * @author shivam.maharshi
 */
public class ContributorDto {

	private String username;
	private int id;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Contributor [username=" + username + ", id=" + id + "]";
	}
	
}
