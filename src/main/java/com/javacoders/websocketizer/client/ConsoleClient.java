package com.javacoders.websocketizer.client;

import java.io.File;
import java.util.Collection;

import com.javacoders.websocketizer.ServiceBlueprint;
import com.javacoders.websocketizer.cogen.CodeGenerator;
import com.javacoders.websocketizer.parser.ServiceExtractor;

/**
 * Takes input from command line and auto-generates code for the Web Service to
 * be converted into a Web Socket Service.
 * 
 * @author shivam.maharshi
 */
public class ConsoleClient {

	public static void main(String[] args) {
		String in = null;
		for (String arg : args) {
			if (arg != null && !arg.isEmpty() && arg.startsWith("--input=")) {
				in = arg.split("=")[1];
			}
		}
		if (in != null) {
			File projectDir = new File(in);
			ServiceExtractor extractor = new ServiceExtractor();
			Collection<ServiceBlueprint> blueprints = extractor.extractBlueprints(projectDir);
			for (ServiceBlueprint sb : blueprints)
				CodeGenerator.generate(sb);
		} else {
			System.out.println("Please enter a valid ");
		}
	}

}
