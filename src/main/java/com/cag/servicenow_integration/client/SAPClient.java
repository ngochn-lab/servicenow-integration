package com.cag.servicenow_integration.client;

import com.cag.servicenow_integration.enums.ResponseCode;
import com.cag.servicenow_integration.exception.InternalServerException;
import com.cag.servicenow_integration.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SAPClient {
    private final WebClient webClient;

    public SAPClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public <T> T getRequest(String uri, ParameterizedTypeReference<T> reference) {
        return executeRequest(webClient.get(), uri, reference);
    }

    public <T> T putRequest(String uri, Object body, ParameterizedTypeReference<T> reference) {
        return executeRequest(webClient.put(), uri, body, reference);
    }

    private <T> T executeRequest(
            WebClient.RequestBodyUriSpec urlSpec,
            String uri,
            Object bodyValue,
            ParameterizedTypeReference<T> reference
    ) {
        WebClient.RequestHeadersSpec<?> responseSpec = urlSpec
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bodyValue);
        return returnResponseBody(responseSpec, reference);
    }

    private <T> T executeRequest(
            WebClient.RequestHeadersUriSpec<?> urlSpec,
            String uri,
            ParameterizedTypeReference<T> reference
    ) {
        WebClient.RequestHeadersSpec<?> responseSpec = urlSpec
                .uri(uri);
        return returnResponseBody(responseSpec, reference);
    }

    private <T> T returnResponseBody(
            WebClient.RequestHeadersSpec<?> responseSpec,
            ParameterizedTypeReference<T> reference
    ) {
        return responseSpec
                .retrieve()
                .bodyToMono(reference)
                .onErrorResume(this::handleWebClientErrors)
                .block();
    }

    public <T> Mono<T> handleWebClientErrors(Throwable ex) {
        if (ex instanceof WebClientResponseException.NotFound notFoundEx) {
            log.warn("Resource not found: {}", notFoundEx.getMessage());
            return Mono.error(new NotFoundException(
                    notFoundEx.getMessage(),
                    ResponseCode.NOT_FOUND.getCode(),
                    String.valueOf(HttpStatus.NOT_FOUND.value())
            ));
        }

        return Mono.error(new InternalServerException(
                ex.getMessage(),
                ResponseCode.ERROR.getCode(),
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ));
    }
}
