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
import java.util.Map;

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

    public HttpRequest createPostRequest(String url, String jsonBody) {
        return createPostRequest(url, jsonBody, Map.of());
    }

    public HttpRequest createPostRequest(String url, String jsonBody, Map<String, String> headers) {
        HttpRequest.Builder builder = HttpRequest
                .newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

        headers.forEach(builder::header);

        return builder.build();
    }

    @Override
    public void communicate(SeleniumReportDTO reportDTO) {
        String bodyJson = prepareRequestBodyAsJson(reportDTO);

        HttpRequest request = createPostRequest(ingestionApiAddress, bodyJson, Map.of(
                authHeaderKey, authHeaderValue,
                projectHeaderKey, String.valueOf(reportDTO.projectId())
        ));

        sendRequest(request);
    }

    private String prepareRequestBodyAsJson(SeleniumReportDTO reportDTO) {
        ObjectMapper mapper = new ObjectMapper();
        String bodyJson = null;
        try {
            bodyJson = mapper.writeValueAsString(IngestionBodyDTO.from(reportDTO));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return bodyJson;
    }

    public void sendRequest(HttpRequest request) {
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
