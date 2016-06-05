package org.edu.core;

import org.edu.dto.PageDto;
import org.edu.mapper.Mapper;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

/**
 * Dumps the data into DB using Hibernate.
 * 
 * @author shivam.maharshi
 */
public class DBDumper implements Runnable {

	private Session s;
	private PageDto pd;
	private boolean isExecuting;

	public DBDumper(Session session) {
		this.s = session;
	}
	
	public void setConsumerObj(PageDto pd) {
		this.pd = pd;
	}
	
	public boolean isExecuting() {
		return isExecuting;
	}

	@Override
	public void run() {
		try {
			isExecuting = true;
			s.beginTransaction();
			s.save(Mapper.mapP(pd));
			s.save(Mapper.mapR(pd));
			s.save(Mapper.mapT(pd));
			s.getTransaction().commit();
			s.flush();
		} catch (Exception e) {
			if (e instanceof ConstraintViolationException) {
				XMLParser.addFailedTitle("V | " + pd.getTitle());
			} else {
				XMLParser.addFailedTitle(pd.getTitle());
			}
		} finally {
			isExecuting = false;
		}
	}

}
