package com.github.eemmiirr.osshowcase

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(
    exclude = [
        ElasticsearchDataAutoConfiguration::class,
    ],
)
class Application

fun main() {
    runApplication<Application>()
}
