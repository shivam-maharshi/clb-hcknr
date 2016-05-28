package org.edu.core;

import org.edu.persistence.Page;
import org.edu.persistence.Revision;
import org.edu.persistence.Text;
import org.edu.utils.HibernateUtil;
import org.hibernate.Session;

public class DBDumper {
	
	public static boolean add(Page p, Revision r, Text t) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.save(p);
		session.save(r);
		session.save(t);
		session.getTransaction().commit();
		System.out.println("Entry made");
		return true;
	}
	
	public static void main(String[] args) {
		
	}

}
