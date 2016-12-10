package com.javacoders.websocketizer.parser;

import java.io.File;
import java.util.Collection;

/**
 * This class is responsible for extracting the key service HTTP interface
 * definition information from a JAX-RS application.
 * 
 * @author dedocibula
 * @author shivam.maharshi
 */
public interface ServiceExtractor<K> {

  public Collection<K> extractBlueprints(File projectDir);

}
