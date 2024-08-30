# OpenSearch Concurrent Segment Search Bug Showcase

This project aims to demonstrate a bug in OpenSearch 2.16 that occurs specifically during a hybrid search 
when concurrent segment search is enabled. The only workaround I found to bypass this issue with 
concurrent segment search is to perform a forced merge operation.

The bug is triggered when executing the same search on the same dataset after purging and 
re-inserting the documents between runs. The test that highlights this issue 
is `OpenSearchRepositoryTest.triggerConcurrentSegmentSearchBug`.

## How to run
```bash
./gradlew clean build
```

```bash
execute from IDE `OpenSearchRepositoryTest.trigger concurrent segment search bug`
```

## How to make it pass
- Disable `search.concurrent_segment_search.enabled` in `resources/index_config`
- Uncomment lines 109-114 in `AbstractIntegrationTest`

## License
Apache License, Version 2.0
