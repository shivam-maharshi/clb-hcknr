package org.edu.core;

import org.edu.dto.PageDto;
import org.edu.mapper.Mapper;

/**
 * Consumes the events generated while parsing XML dumps. In our case these
 * events are a successful record read and corresponding new DTO object
 * creation.
 * 
 * @author shivam.maharshi
 */
public class XMLConsumer {
	
	public static boolean consume(PageDto pd) {
		return DBDumper.add(Mapper.mapP(pd), Mapper.mapR(pd), Mapper.mapT(pd));
	}
	
}
