package it.unibz.butterfly_net.test_agent.core;

public record SeleniumReportDTO(
        Long projectId,
        Long timestamp,
        String pagePath,
        String issues
) {
}
