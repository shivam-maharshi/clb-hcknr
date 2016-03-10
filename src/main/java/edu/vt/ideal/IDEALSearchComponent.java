package edu.vt.ideal;

import org.apache.log4j.Logger;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;

import java.io.IOException;
import java.util.Date;

/**
 * Author: dedocibula
 * Created on: 10.3.2016.
 */
public class IDEALSearchComponent extends SearchComponent {
    private static Logger logger = Logger.getLogger(IDEALSearchComponent.class);

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        // do nothing
    }

    @Override
    public void process(ResponseBuilder rb) throws IOException {
        logger.info(String.format("[ %s ] - IDEAL Search Component invoked", new Date()));
        // TODO actual work
    }

    @Override
    public String getDescription() {
        return "IDEAL search component for CS5604";
    }

    @Override
    public String getSource() {
        return "https://github.com/shivam-maharshi/IDEAL";
    }
}
