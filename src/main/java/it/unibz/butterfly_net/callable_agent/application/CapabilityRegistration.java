package it.unibz.butterfly_net.callable_agent.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.butterfly_net.callable_agent.core.utils.Config;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.Map;
import java.util.Set;

public class CapabilityRegistration {
    private final HttpClient httpClient;
    private final String agentManagerAddress;

    public CapabilityRegistration(HttpClient httpClient) {
        try {
            this.agentManagerAddress = Config.getInstance().property("AGENT_MANAGER_ADDRESS");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.httpClient = httpClient;
    }

    public void register() {
        HttpRequest request = this.httpClient.createPostRequest(
                agentManagerAddress,
                capabilityDescriptor()
        );

        this.httpClient.sendRequest(request);
    }

    private String capabilityDescriptor() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(Map.of(
                    "name", "callable-agent",
                    "capability", Map.of(
                            "parserDescriptor", Map.of(
                                    "type", "js_engine",
                                    "payload", "console.log(1);"
                            ),
                            "parserInputDescriptors", Set.of(
                                    Map.of(
                                            "type", "Long",
                                            "name", "projectId",
                                            "required", true
                                    ),
                                    Map.of(
                                            "type", "String",
                                            "name", "path",
                                            "required", true
                                    )
                            )
                    )
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
