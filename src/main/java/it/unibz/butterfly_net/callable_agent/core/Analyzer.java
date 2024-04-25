package it.unibz.butterfly_net.callable_agent.core;

import java.time.Instant;

public class Analyzer {
    private final ExternalCommunicator externalCommunicator;

    public Analyzer(ExternalCommunicator externalCommunicator) {
        this.externalCommunicator = externalCommunicator;
    }

    public void analyze(Long projectId, String url) {
        String[] parts = url.split("/");
        String path = parts[parts.length - 1];

        String issues = "foo bar baz";
        long now = Instant.now().getEpochSecond();

        SeleniumReportDTO dto = new SeleniumReportDTO(projectId, now, path, issues);
        externalCommunicator.communicate(dto);
    }
}
