package com.github.eemmiirr.osshowcase

import com.github.eemmiirr.osshowcase.model.Document
import java.lang.IllegalStateException
import org.apache.commons.lang3.ArrayUtils
import org.opensearch.client.opensearch.OpenSearchClient
import org.opensearch.client.opensearch._types.FieldSort
import org.opensearch.client.opensearch._types.FieldValue
import org.opensearch.client.opensearch._types.ShardFailure
import org.opensearch.client.opensearch._types.SortOptions
import org.opensearch.client.opensearch._types.SortOrder
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders
import org.opensearch.client.opensearch._types.query_dsl.TermsQueryField
import org.opensearch.client.opensearch.core.IndexRequest
import org.opensearch.client.opensearch.core.SearchRequest
import org.springframework.stereotype.Component

@Component
class OpenSearchRepository(val openSearchClient: OpenSearchClient) {
    fun persist(entity: Document): String {
        return openSearchClient.index(
            IndexRequest.of {
                it.index("test")
                    .id(entity.id.toString())
                    .document(entity)
            },
        ).id()
    }

    fun search(
        keywords: List<String>,
        vector: List<Float>,
    ): List<String> {
        val response = openSearchClient.search(
            SearchRequest.of {
                it.index("test")
                    .pipeline("nlp-search-pipeline")
                    .sort(
                        SortOptions.of { it.field(FieldSort.of { it.field("indexingDate").order(SortOrder.Desc) }) },
                    )
                    .query(
                        QueryBuilders
                            .hybrid()
                            .queries(
                                QueryBuilders.terms()
                                    .terms(TermsQueryField.of { it.value(keywords.map { FieldValue.of(it) }) })
                                    .field("keywords")
                                    .build()
                                    .toQuery(),
                                QueryBuilders
                                    .knn()
                                    .k(300)
                                    .field("vector")
                                    .vector(ArrayUtils.toPrimitive(vector.toTypedArray()))
                                    .boost(2.0f)
                                    .build()
                                    .toQuery(),
                            )
                            .build()
                            .toQuery(),
                    )
            },
            Document::class.java,
        )

        val failures = response.shards()
            .failures()
            .map { failure: ShardFailure ->
                if (failure.reason().causedBy() != null) {
                    failure.reason().causedBy()!!.reason()
                } else {
                    failure.reason().reason()
                }
            }

        return if (failures.isNotEmpty()) {
            throw IllegalStateException("OpenSearch response contains a failures. Failures: ${failures} ")
        } else {
            response.hits().hits().map { it.id()!! }
        }
    }
}
