package it.unibz.butterfly_net.callable_agent.application;

import it.unibz.butterfly_net.callable_agent.core.SeleniumReportDTO;

public record IngestionBodyDTO(
        String pagePath,
        String issues
) {
    public static IngestionBodyDTO from(SeleniumReportDTO seleniumDto) {
        return new IngestionBodyDTO(
                seleniumDto.pagePath(),
                seleniumDto.issues()
        );
    }
}
