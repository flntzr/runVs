package com.springapp.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.io.File;
import java.net.URL;


public class HibernateUtil
{
	private static final SessionFactory sessionFactory = build();

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	private static SessionFactory build () {
		try {
			Configuration configuration = new Configuration();
			//configuration.addFile("./hibernate.cfg.xml");
			configuration.configure(/*"hibernate.cfg.xml"*/);
			URL location = SessionFactory.class.getProtectionDomain().getCodeSource().getLocation();
			//configuration.configure(cfg);
			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
			return configuration.buildSessionFactory(serviceRegistry);
		}
			catch (Throwable ex) {
			System.err.println("SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

}