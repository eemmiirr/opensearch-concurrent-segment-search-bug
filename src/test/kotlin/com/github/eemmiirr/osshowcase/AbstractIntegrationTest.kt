package com.github.eemmiirr.osshowcase

import com.google.common.io.Resources
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.opensearch.client.opensearch.OpenSearchClient
import org.opensearch.client.opensearch._types.Conflicts
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders
import org.opensearch.client.opensearch.core.DeleteByQueryRequest
import org.opensearch.client.opensearch.indices.RefreshRequest
import org.opensearch.testcontainers.OpensearchContainer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.GenericContainer
import org.testcontainers.lifecycle.Startables
import org.testcontainers.utility.DockerImageName
import java.util.concurrent.atomic.AtomicBoolean

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = ["spring.main.allow-bean-definition-overriding=true"],
    classes = [Application::class],
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension::class)
abstract class AbstractIntegrationTest {
    private val opensearchInitialized: AtomicBoolean = AtomicBoolean(false)

    companion object {
        private val opensearchContainer: GenericContainer<*> =
            OpensearchContainer(
                DockerImageName.parse("opensearchproject/opensearch:2.16.0")
                    .asCompatibleSubstituteFor("opensearchproject/opensearch"),
            )
                .withAccessToHost(true)
                .withExposedPorts(9200)
                .withReuse(true)

        init {
            Startables.deepStart(opensearchContainer).join()
        }

        @JvmStatic
        @DynamicPropertySource
        fun initializeContainerProps(registry: DynamicPropertyRegistry) {
            registry.add("opensearch.uris") {
                "http://${opensearchContainer.host}:${opensearchContainer.getMappedPort(9200)}"
            }
        }
    }

    @Autowired
    protected lateinit var openSearchClient: OpenSearchClient

    @BeforeEach
    fun setUp() {
        val osWebClient: WebTestClient =
            WebTestClient.bindToServer()
                .baseUrl("http://${opensearchContainer.host}:${opensearchContainer.getMappedPort(9200)}")
                .build()

        if (opensearchInitialized.compareAndSet(false, true)) {
            osWebClient.put()
                .uri("/_cluster/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(IOUtils.toByteArray(Resources.getResource("index_config.json")))
                .exchange()
                .expectStatus()
                .isOk()

            osWebClient.put().uri("/test").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(IOUtils.toByteArray(Resources.getResource("test_index.json")))
                .exchange()
                .expectStatus()
                .isOk()

            osWebClient.put()
                .uri("/_search/pipeline/nlp-search-pipeline")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(IOUtils.toByteArray(Resources.getResource("normalization_processor.json")))
                .exchange()
                .expectStatus()
                .isOk()
        }

        deleteAllDocuments("test")
    }

    protected fun deleteAllDocuments(index: String) {
        openSearchClient.deleteByQuery(
            DeleteByQueryRequest.of { dbq: DeleteByQueryRequest.Builder ->
                dbq
                    .index(index)
                    .query(QueryBuilders.matchAll().build().toQuery())
                    .conflicts(Conflicts.Abort)
                    .refresh(true)
                    .waitForCompletion(true)
            },
        )
//        openSearchClient.indices().forcemerge {
//            it.index(index)
//                .flush(true)
//                .maxNumSegments(3L)
//                .onlyExpungeDeletes(false)
//        }
    }

    protected fun refreshIndexes() {
        openSearchClient.indices().refresh(
            RefreshRequest.Builder()
                .index("_all")
                .allowNoIndices(false)
                .build(),
        )
    }
}
