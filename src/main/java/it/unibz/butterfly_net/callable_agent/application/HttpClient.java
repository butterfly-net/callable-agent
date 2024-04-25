package it.unibz.butterfly_net.callable_agent.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.butterfly_net.callable_agent.core.ExternalCommunicator;
import it.unibz.butterfly_net.callable_agent.core.SeleniumReportDTO;
import it.unibz.butterfly_net.callable_agent.core.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClient implements ExternalCommunicator {
    private final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    private final String ingestionApiAddress;
    private final String authHeaderKey;
    private final String authHeaderValue;
    private final String projectHeaderKey;

    public HttpClient() throws IOException {
        Config config = Config.getInstance();
        ingestionApiAddress = config.property("INGESTION_API_URL");
        authHeaderKey = config.property("AUTH_HEADER_KEY");
        authHeaderValue = config.property("AUTH_HEADER_VALUE");
        projectHeaderKey = config.property("PROJECT_HEADER_KEY");
    }

    @Override
    public void communicate(SeleniumReportDTO reportDTO) {
        ObjectMapper mapper = new ObjectMapper();
        String bodyJson = null;
        try {
            bodyJson = mapper.writeValueAsString(IngestionBodyDTO.from(reportDTO));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(ingestionApiAddress))
                .header(authHeaderKey, authHeaderValue)
                .header(projectHeaderKey, String.valueOf(reportDTO.projectId()))
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
        try {
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("request sent");
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
