package org.edu.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * Hibernate utility to provide common functions like creating session or
 * factory or shutting down clean ups.
 * 
 * @author shivam.maharshi
 */
public class HibernateUtil {

	private static SessionFactory sessionFactory;

	private static SessionFactory buildSessionFactory()  throws Exception {
		// Create the SessionFactory from hibernate.cfg.xml
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.cfg.xml");
		System.out.println("Hibernate Configuration loaded");
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties()).build();
		System.out.println("Hibernate serviceRegistry created");
		SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		return sessionFactory;
	}

	public static SessionFactory getSessionFactory()  throws Exception {
		if (sessionFactory == null || sessionFactory.isClosed())
			sessionFactory = buildSessionFactory();
		return sessionFactory;
	}
	
	public static void closeSessionFactory() throws Exception {
		if (sessionFactory != null || !sessionFactory.isClosed())
			sessionFactory.close();
	}
}