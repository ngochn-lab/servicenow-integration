# Pagination and Timeout Management Guide

## Overview
This document describes the pagination implementation and timeout management strategy for the ServiceNow Integration API to prevent data overload and ensure responsive API behavior.

## Pagination Strategy

### Skip/Limit Pattern
We use the **skip/limit** pagination pattern instead of page/size for better control and clarity:

- **`skip`**: Number of records to skip from the beginning (offset)
- **`limit`**: Maximum number of records to return (page size)

### Why Skip/Limit?
1. **Clarity**: More intuitive for API consumers - they know exactly how many records are being skipped
2. **Flexibility**: Easier to implement custom pagination scenarios
3. **Database Efficiency**: Maps directly to SQL `OFFSET` and `LIMIT` clauses
4. **Stateless**: No need to maintain page state between requests

### API Usage Examples

#### Basic Request
```bash
GET /api/servicenow/v1/employee-profiles?skip=0&limit=50
```

#### Navigate to Next Page
```bash
# First page (records 1-50)
GET /api/servicenow/v1/employee-profiles?skip=0&limit=50

# Second page (records 51-100)
GET /api/servicenow/v1/employee-profiles?skip=50&limit=50

# Third page (records 101-150)
GET /api/servicenow/v1/employee-profiles?skip=100&limit=50
```

#### Custom Page Size
```bash
# Get 100 records starting from record 201
GET /api/servicenow/v1/employee-profiles?skip=200&limit=100
```

### Response Structure
```json
{
  "data": [...],
  "pagination": {
    "skip": 0,
    "limit": 50,
    "totalRecords": 1000,
    "totalPages": 20,
    "currentPage": 0,
    "recordsReturned": 50,
    "hasNext": true,
    "hasPrevious": false,
    "nextSkip": 50,
    "previousSkip": null
  },
  "status": {
    "code": 200,
    "message": "Success",
    "timestamp": 1699123456789,
    "processingTimeMs": 234,
    "warnings": []
  }
}
```

## Data Overload Prevention

### 1. Request Limits
- **Maximum limit**: 200 records per request
- **Default limit**: 50 records
- **Validation**: Automatic sanitization of invalid parameters

### 2. Timeout Configuration

#### API Timeouts
- **Standard requests**: 30 seconds
- **Paginated requests**: 45 seconds (extended for larger datasets)
- **Connection timeout**: 5 seconds
- **Read timeout**: 10 seconds

#### Database Timeouts (when implemented)
- **Query timeout**: 10 seconds
- **Paginated query timeout**: 15 seconds
- **Connection pool timeout**: 30 seconds

### 3. Warning Thresholds
- Requests taking longer than 20 seconds trigger a warning
- Large page sizes (>100) generate performance warnings
- Warnings are included in the response metadata

## Best Practices for API Consumers

### 1. Optimal Page Size
- Use 50-100 records per page for best performance
- Smaller pages (25-50) for real-time UI updates
- Larger pages (100-200) for batch processing

### 2. Parallel Processing
```javascript
// DON'T: Sequential pagination
for (let skip = 0; skip < total; skip += limit) {
  await fetch(`/api/employee-profiles?skip=${skip}&limit=${limit}`);
}

// DO: Process pages as they arrive
async function* fetchPages() {
  let skip = 0;
  let hasMore = true;
  
  while (hasMore) {
    const response = await fetch(`/api/employee-profiles?skip=${skip}&limit=${limit}`);
    const data = await response.json();
    
    yield data.data;
    
    hasMore = data.pagination.hasNext;
    skip = data.pagination.nextSkip;
  }
}
```

### 3. Error Handling
```javascript
try {
  const response = await fetch('/api/employee-profiles?skip=0&limit=50');
  const data = await response.json();
  
  if (data.status.warnings?.length > 0) {
    console.warn('API warnings:', data.status.warnings);
    // Consider reducing page size or implementing backoff
  }
  
  // Process data
} catch (error) {
  if (error.name === 'TimeoutError') {
    // Reduce page size and retry
    console.error('Request timeout - consider smaller page size');
  }
}
```

### 4. Caching Strategy
- Cache pages based on skip/limit parameters
- Invalidate cache when data changes
- Consider ETags for conditional requests

## Circuit Breaker Pattern

The API implements a circuit breaker to handle failures gracefully:

1. **Closed State**: Normal operation
2. **Open State**: After 3 failures, circuit opens for 30 seconds
3. **Half-Open State**: Test with limited requests
4. **Recovery**: Return to closed state on success

## Migration from Page/Size

For backward compatibility, a legacy endpoint is available:

```bash
# Legacy (deprecated)
GET /api/servicenow/v1/employee-profiles/legacy?page=2&size=50

# Internally converts to:
GET /api/servicenow/v1/employee-profiles?skip=100&limit=50
```

**Note**: The legacy endpoint is deprecated and will be removed in future versions.

## Performance Considerations

### 1. Database Indexing
Ensure proper indexes on:
- Sort columns (e.g., `created_at`, `id`)
- Filter columns used in WHERE clauses
- Composite indexes for complex queries

### 2. Query Optimization
- Use database-level pagination (LIMIT/OFFSET)
- Avoid loading unnecessary relationships
- Consider cursor-based pagination for very large datasets

### 3. Response Size
- Exclude unnecessary fields from responses
- Implement field selection (`?fields=id,name,email`)
- Consider compression for large responses

## Monitoring and Alerts

### Key Metrics to Monitor
1. **Response Time**: P50, P95, P99 percentiles
2. **Timeout Rate**: Percentage of requests timing out
3. **Page Size Distribution**: Average and max page sizes requested
4. **Circuit Breaker State**: Frequency of circuit opening
5. **Warning Rate**: Percentage of requests with warnings

### Alert Thresholds
- Response time P95 > 10 seconds
- Timeout rate > 1%
- Circuit breaker open > 5 times per hour
- Average page size > 150 records

## Configuration Reference

All timeout values are configurable in `application.properties`:

```properties
# API Timeouts
app.timeout.api.request-timeout-seconds=30
app.timeout.api.paginated-request-timeout-seconds=45
app.timeout.api.warning-threshold-seconds=20

# Database Timeouts
app.timeout.database.query-timeout-seconds=10
app.timeout.database.paginated-query-timeout-seconds=15

# Circuit Breaker
app.timeout.circuit-breaker.failure-threshold=3
app.timeout.circuit-breaker.wait-duration-seconds=30
```

## Future Enhancements

1. **Cursor-based Pagination**: For real-time data with frequent updates
2. **GraphQL Support**: Allow clients to specify exact fields needed
3. **Streaming API**: Server-sent events for large datasets
4. **Async Processing**: WebSockets for long-running queries
5. **Smart Caching**: Predictive prefetching based on usage patterns
