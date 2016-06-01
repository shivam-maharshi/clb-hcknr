package org.edu.mapper;

import org.edu.dto.PageDto;
import org.edu.dto.RevisionDto;
import org.edu.persistence.Page;
import org.edu.persistence.Revision;
import org.edu.persistence.Text;

/**
 * Maps the DTO created from XMLs to Entities used in persistence layers for DB
 * operations.
 * 
 * @author shivam.maharshi
 */
public class Mapper {

	public static Page mapP(PageDto pd) {
		Page p = new Page();
		RevisionDto rd = pd.getRevision();
		p.setPageContentModel(rd.getModel());
		p.setPageId(pd.getId());
		p.setPageIsNew(0);
		p.setPageIsRedirect(0);
		p.setPageLang(null);
		p.setPageLatest(rd.getId());
		p.setPageLen(14); //TODO: Correct.
		p.setPageLinksUpdated(null);
		p.setPageNamespace(pd.getNs());
		p.setPageRandom(Math.random()); // TODO: ?
		p.setPageTitle(pd.getTitle().replace(" ", "_"));
		p.setTouched("0"); // TODO:?
		p.setPageRestrictions("");
		return p;
	}

	public static Revision mapR(PageDto pd) {
		Revision r = new Revision();
		RevisionDto rd = pd.getRevision();
		r.setDeleted(0);
		r.setMinorEdit(0); // TODO: rd.getMinorEdit
		r.setRevComment(rd.getComment());
		r.setRevContentFormat(rd.getFormat());
		r.setRevContentMode(rd.getModel());
		r.setRevId(rd.getId());
		r.setRevLen(50); // TODO: Correct
		r.setRevPage(pd.getId());
		r.setRevParentId(rd.getParentId());
		r.setRevSha1(rd.getSha1());
		r.setRevTextId(rd.getId());
		r.setRevUser(rd.getContributor().getId());
		r.setTimestamp(""); // TODO: Correct
		r.setUserText(rd.getContributor().getUsername());
		return r;
	}

	public static Text mapT(PageDto pd) {
		Text t = new Text();
		RevisionDto rd = pd.getRevision();
		t.setOldId(rd.getId());
		t.setOldText(rd.getText());
		t.setOldFlags("utf-8");
		return t;
	}

}
