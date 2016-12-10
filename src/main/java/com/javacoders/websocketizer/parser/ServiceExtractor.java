package com.javacoders.websocketizer.parser;

import java.io.File;
import java.util.Collection;

import com.javacoders.websocketizer.ServiceBlueprint;

/**
 * This interface is responsible for extracting the {@link ServiceBlueprint}
 * from the input service.
 * 
 * @author shivam.maharshi
 */
public interface ServiceExtractor<K> {

  public Collection<K> extractBlueprints(File projectDir);

}
