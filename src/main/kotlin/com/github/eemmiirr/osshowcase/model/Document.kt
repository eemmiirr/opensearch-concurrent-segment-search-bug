package com.github.eemmiirr.osshowcase.model

import java.time.Instant
import java.util.UUID

data class Document(
    val id: UUID = UUID.randomUUID(),
    val keywords: List<String>,
    val vector: List<Float>,
    val indexingDate: Instant = Instant.now(),
)
