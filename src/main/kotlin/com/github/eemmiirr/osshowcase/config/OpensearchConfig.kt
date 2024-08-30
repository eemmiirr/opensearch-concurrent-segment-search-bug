package com.github.eemmiirr.osshowcase.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.opensearch.client.RestClient
import org.opensearch.client.RestClientBuilder
import org.opensearch.client.json.jackson.JacksonJsonpMapper
import org.opensearch.client.opensearch.OpenSearchClient
import org.opensearch.client.transport.rest_client.RestClientTransport
import org.opensearch.spring.boot.autoconfigure.RestClientBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpensearchConfig {
    @Bean
    fun openSearchClient(
        restClient: RestClient,
        objectMapper: ObjectMapper,
    ): OpenSearchClient {
        val transport = RestClientTransport(restClient, JacksonJsonpMapper(objectMapper))
        return OpenSearchClient(transport)
    }

    @Bean
    fun restClientBuilderCustomizer(): RestClientBuilderCustomizer {
        return RestClientBuilderCustomizer { builder: RestClientBuilder ->
            builder.setCompressionEnabled(true)
        }
    }
}
