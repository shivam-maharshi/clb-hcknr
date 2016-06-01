package org.edu.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Persistence class for Text table in MediaWiki DB.
 * 
 * @author shivam.maharshi
 */
@Entity
@Table(name = "text")
public class Text implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int oldId;
	private String oldText;
	private String oldFlags;
	
	public Text() {
		super();
	}

	public Text(int oldId, String oldText, String oldFlags) {
		super();
		this.oldId = oldId;
		this.oldText = oldText;
		this.oldFlags = oldFlags;
	}

	@Id
	@Column(name = "old_id", unique = true, nullable = false)
	public int getOldId() {
		return oldId;
	}

	public void setOldId(int oldId) {
		this.oldId = oldId;
	}

	@Column(name = "old_text", unique = false, nullable = false, length =	16777215)
	public String getOldText() {
		return oldText;
	}

	public void setOldText(String oldText) {
		this.oldText = oldText;
	}

	@Column(name = "old_flags", unique = false, nullable = false)
	public String getOldFlags() {
		return oldFlags;
	}

	public void setOldFlags(String oldFlags) {
		this.oldFlags = oldFlags;
	}
	
}
