package com.github.eemmiirr.osshowcase

import com.github.eemmiirr.osshowcase.model.Document
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.stream.IntStream

class OpenSearchRepositoryTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var openSearchRepository: OpenSearchRepository

    @Test
    fun `trigger concurrent segment search bug`() {
        for (i in 1..100) {
            println("Iteration $i")

            val id1 =
                openSearchRepository.persist(
                    Document(keywords = listOf("keyword 1"), vector = generateVector(1024, 1.0f)),
                )
            val id2 =
                openSearchRepository.persist(
                    Document(keywords = listOf("keyword 2"), vector = generateVector(1024, 1.0f)),
                )
            val id3 =
                openSearchRepository.persist(
                    Document(keywords = listOf("keyword 3"), vector = generateVector(1024, 1.0f)),
                )
            refreshIndexes()

            val result = openSearchRepository.search(listOf("keyword 2", "keyword 3"), vector = generateVector(1024, 1.0f))

            assertThat(result).isEqualTo(listOf(id3, id2, id1))
            deleteAllDocuments("test")
        }
    }

    protected fun generateVector(
        count: Int,
        number: Float,
    ): List<Float> {
        return IntStream.range(0, count).mapToObj { number }.toList()
    }
}
