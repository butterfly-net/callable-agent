package it.unibz.butterfly_net.test_agent.application;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import it.unibz.butterfly_net.test_agent.core.Analyzer;
import it.unibz.butterfly_net.test_agent.core.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class HttpServer {
    private final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private final int PORT;
    private final Analyzer analyzer;

    public HttpServer(Analyzer analyzer) {
        String portConfig = null;
        try {
            portConfig = Config.getInstance().property("SERVER_PORT");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.PORT = Integer.parseInt(portConfig);
        this.analyzer = analyzer;
    }

    public void run() {
        Javalin app = Javalin.create();

        app.post("/dispatch", ctx -> {
            AnalysisRequestDTO dto = ctx.bodyAsClass(AnalysisRequestDTO.class);

            String message = String.format("Dispatching report on #%d", dto.projectId());
            logger.info(message);

            analyzer.analyze(dto.projectId(), dto.path());

            ctx.status(HttpStatus.ACCEPTED);
            ctx.json(Map.of("status", "OK"));
        });

        app.start(PORT);
    }
}
