package org.edu.core;

import java.util.LinkedList;
import java.util.Queue;

import org.edu.dto.PageDto;

/**
 * This class is responsible for storing the parsed XML element DTO's which is
 * consumed by {@link Consumer}.
 * 
 * @author shivam.maharshi
 */
public class ConsumerQueue {
	
	private static Queue<PageDto> queue = new LinkedList<PageDto>();
	
	public static void add(PageDto pd) {
		queue.add(pd);
	}
	
	public static PageDto poll() {
		return queue.poll();
	}
	
	public static boolean isEmpty() {
		return queue.isEmpty();
	}

}
