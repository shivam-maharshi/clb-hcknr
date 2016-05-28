package org.edu.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Persistence class for Revision table in MediaWiki DB. 
 * 
 * @author shivam.maharshi
 */
@Entity
@Table (name = "revision")
public class Revision implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int revId;
	private int revPage;
	private int revTextId;
	private String revComment;
	private int revUser;
	private String userText;
	private String timestamp;
	private int minorEdit;
	private int deleted;
	private int revLen;
	private int revParentId;
	private String revSha1;
	private String revContentMode;
	private String revContentFormat;
	
	public Revision() {
		super();
	}

	public Revision(int revId, int revPage, int revTextId, String revComment, int revUser, String userText,
			String timestamp, int minorEdit, int deleted, int revLen, int revParentId, String revSha1,
			String revContentMode, String revContentFormat) {
		super();
		this.revId = revId;
		this.revPage = revPage;
		this.revTextId = revTextId;
		this.revComment = revComment;
		this.revUser = revUser;
		this.userText = userText;
		this.timestamp = timestamp;
		this.minorEdit = minorEdit;
		this.deleted = deleted;
		this.revLen = revLen;
		this.revParentId = revParentId;
		this.revSha1 = revSha1;
		this.revContentMode = revContentMode;
		this.revContentFormat = revContentFormat;
	}

	@Id
	@Column(name = "rev_id", unique = true, nullable = false)
	public int getRevId() {
		return revId;
	}

	public void setRevId(int revId) {
		this.revId = revId;
	}

	@Column(name = "rev_page", unique = false, nullable = false)
	public int getRevPage() {
		return revPage;
	}

	public void setRevPage(int revPage) {
		this.revPage = revPage;
	}

	@Column(name = "rev_text_id", unique = false, nullable = false)
	public int getRevTextId() {
		return revTextId;
	}

	public void setRevTextId(int revTextId) {
		this.revTextId = revTextId;
	}

	@Column(name = "rev_comment", unique = false, nullable = false)
	public String getRevComment() {
		return revComment;
	}

	public void setRevComment(String revComment) {
		this.revComment = revComment;
	}

	@Column(name = "rev_user", unique = false, nullable = false)
	public int getRevUser() {
		return revUser;
	}

	public void setRevUser(int revUser) {
		this.revUser = revUser;
	}

	@Column(name = "rev_user_text", unique = false, nullable = false)
	public String getUserText() {
		return userText;
	}

	public void setUserText(String userText) {
		this.userText = userText;
	}

	@Column(name = "rev_timestamp", unique = false, nullable = false)
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Column(name = "rev_minor_edit", unique = false, nullable = false)
	public int getMinorEdit() {
		return minorEdit;
	}

	public void setMinorEdit(int minorEdit) {
		this.minorEdit = minorEdit;
	}

	@Column(name = "rev_deleted", unique = false, nullable = false)
	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	@Column(name = "rev_len", unique = false, nullable = false)
	public int getRevLen() {
		return revLen;
	}

	public void setRevLen(int revLen) {
		this.revLen = revLen;
	}

	@Column(name = "rev_parent_id", unique = false, nullable = false)
	public int getRevParentId() {
		return revParentId;
	}

	public void setRevParentId(int revParentId) {
		this.revParentId = revParentId;
	}

	@Column(name = "rev_sha1", unique = false, nullable = false)
	public String getRevSha1() {
		return revSha1;
	}

	public void setRevSha1(String revSha1) {
		this.revSha1 = revSha1;
	}

	@Column(name = "rev_content_model", unique = false, nullable = false)
	public String getRevContentMode() {
		return revContentMode;
	}

	public void setRevContentMode(String revContentMode) {
		this.revContentMode = revContentMode;
	}

	@Column(name = "rev_content_format", unique = false, nullable = false)
	public String getRevContentFormat() {
		return revContentFormat;
	}

	public void setRevContentFormat(String revContentFormat) {
		this.revContentFormat = revContentFormat;
	}
	
}
