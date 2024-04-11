package it.unibz.butterfly_net.test_agent;

import it.unibz.butterfly_net.test_agent.application.HttpClient;
import it.unibz.butterfly_net.test_agent.application.HttpServer;
import it.unibz.butterfly_net.test_agent.core.Analyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Component {
    private static final Logger logger = LoggerFactory.getLogger(Component.class);

    public static void main(String[] args) throws IOException {
        logger.info("Running");

        HttpClient httpClient = new HttpClient();
        Analyzer analyzer = new Analyzer(httpClient);
        new HttpServer(analyzer).run();
    }
}
