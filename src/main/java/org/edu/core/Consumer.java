package org.edu.core;

import java.util.ArrayList;
import java.util.List;

import org.edu.dto.PageDto;
import org.edu.utils.HibernateUtil;
import org.hibernate.HibernateException;

/**
 * Consumes the events generated while parsing XML dumps. In our case these
 * events are a successful record read and corresponding new DTO object
 * creation.
 * 
 * @author shivam.maharshi
 */
public class Consumer {

	private static int workerId = 0;
	private static int noOfWorkers = 8;
	private static List<Thread> workers = new ArrayList<Thread>();
	private static List<DBDumper> dumpers = new ArrayList<DBDumper>();

	public static void consume(int noOfWorkers) throws HibernateException, Exception {
		Consumer.noOfWorkers = noOfWorkers;
		for (int n = 0; n < noOfWorkers; n++) {
			DBDumper dumper = new DBDumper(HibernateUtil.getSessionFactory().openSession());
			dumpers.add(dumper);
			workers.add(new Thread(dumper));
		}
		while (true) {
			PageDto pd = ConsumerQueue.poll();
			if (pd != null) {
				int workerId = getNextWorkerId();
				while (dumpers.get(workerId).isExecuting()) {
					workerId = getNextWorkerId();
				}
				consume(workerId, pd);
			}
		}
	}

	public static int getNextWorkerId() {
		++workerId;
		if (workerId == noOfWorkers)
			workerId = 0;
		return workerId;
	}

	public static void consume(int workerId, PageDto pd) throws Exception {
		DBDumper dumper = dumpers.get(workerId);
		// Re-confirming dumper isn't executing.
		if (!dumper.isExecuting()) {
			Thread t = workers.get(workerId);
			dumper.setConsumerObj(pd);
			t.start();
		}
	}

	public static void main(String[] args) throws HibernateException, Exception {
		//consume(8);
		while(true)
		System.out.println(getNextWorkerId());
	}

}
