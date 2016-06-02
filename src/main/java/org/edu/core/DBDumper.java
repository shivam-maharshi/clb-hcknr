package org.edu.core;

import org.edu.persistence.Page;
import org.edu.persistence.Revision;
import org.edu.persistence.Text;
import org.edu.utils.HibernateUtil;
import org.hibernate.Session;

/**
 * Dumps the data into DB using Hibernate.
 * 
 * @author shivam.maharshi
 */
public class DBDumper {

	public static boolean add(Page p, Revision r, Text t) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.save(p);
		session.save(r);
		session.save(t);
		session.getTransaction().commit();
		session.flush();
		session.close();
		System.out.println("Entry made");
		return true;
	}

}
