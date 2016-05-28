package org.edu.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Persistence class for Page table in MediaWiki DB. 
 * 
 * @author shivam.maharshi
 */
@Entity
@Table (name="Page")
public class Page implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int pageId;
	private int pageNamespace;
	private String pageTitle;
	private String pageRestrictions;
	private int pageIsRedirect;
	private int pageIsNew;
	private double pageRandom;
	private String touched;
	private String pageLinksUpdated;
	private int pageLatest;
	private int pageLen;
	private String pageContentModel;
	private String pageLang;
	
	public Page() {
		super();
	}

	public Page(int pageId, int pageNamespace, String pageTitle, String pageRestrictions, int pageIsRedirect,
			int pageIsNew, double pageRandom, String touched, String pageLinksUpdated, int pageLatest, int pageLen,
			String pageContentModel, String pageLang) {
		super();
		this.pageId = pageId;
		this.pageNamespace = pageNamespace;
		this.pageTitle = pageTitle;
		this.pageRestrictions = pageRestrictions;
		this.pageIsRedirect = pageIsRedirect;
		this.pageIsNew = pageIsNew;
		this.pageRandom = pageRandom;
		this.touched = touched;
		this.pageLinksUpdated = pageLinksUpdated;
		this.pageLatest = pageLatest;
		this.pageLen = pageLen;
		this.pageContentModel = pageContentModel;
		this.pageLang = pageLang;
	}

	@Id
	@Column(name = "page_id", unique = true, nullable = false)
	public int getPageId() {
		return pageId;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}

	@Column(name = "page_namespace", unique = false, nullable = false)
	public int getPageNamespace() {
		return pageNamespace;
	}

	public void setPageNamespace(int pageNamespace) {
		this.pageNamespace = pageNamespace;
	}

	@Column(name = "page_title", unique = false, nullable = false)
	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	@Column(name = "page_restrictions", unique = false, nullable = false)
	public String getPageRestrictions() {
		return pageRestrictions;
	}

	public void setPageRestrictions(String pageRestrictions) {
		this.pageRestrictions = pageRestrictions;
	}

	@Column(name = "page_is_redirect", unique = false, nullable = false)
	public int getPageIsRedirect() {
		return pageIsRedirect;
	}

	public void setPageIsRedirect(int pageIsRedirect) {
		this.pageIsRedirect = pageIsRedirect;
	}

	@Column(name = "page_is_new", unique = false, nullable = false)
	public int getPageIsNew() {
		return pageIsNew;
	}

	public void setPageIsNew(int pageIsNew) {
		this.pageIsNew = pageIsNew;
	}

	@Column(name = "page_random", unique = false, nullable = false)
	public double getPageRandom() {
		return pageRandom;
	}

	public void setPageRandom(double pageRandom) {
		this.pageRandom = pageRandom;
	}

	@Column(name = "page_touched", unique = false, nullable = false)
	public String getTouched() {
		return touched;
	}

	public void setTouched(String touched) {
		this.touched = touched;
	}

	@Column(name = "page_links_updated", unique = false, nullable = false)
	public String getPageLinksUpdated() {
		return pageLinksUpdated;
	}

	public void setPageLinksUpdated(String pageLinksUpdated) {
		this.pageLinksUpdated = pageLinksUpdated;
	}

	@Column(name = "page_latest", unique = false, nullable = false)
	public int getPageLatest() {
		return pageLatest;
	}

	public void setPageLatest(int pageLatest) {
		this.pageLatest = pageLatest;
	}

	@Column(name = "page_len", unique = false, nullable = false)
	public int getPageLen() {
		return pageLen;
	}

	public void setPageLen(int pageLen) {
		this.pageLen = pageLen;
	}

	@Column(name = "page_content_mode", unique = false, nullable = false)
	public String getPageContentModel() {
		return pageContentModel;
	}

	public void setPageContentModel(String pageContentModel) {
		this.pageContentModel = pageContentModel;
	}

	@Column(name = "page_lang", unique = false, nullable = false)
	public String getPageLang() {
		return pageLang;
	}

	public void setPageLang(String pageLang) {
		this.pageLang = pageLang;
	}
	
}
