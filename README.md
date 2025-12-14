# TrexSQL - JVM Library for TREX

A JVM library providing standalone server mode and embeddable library mode with Java-friendly API.

## Prerequisites

- JDK 17 or later
- TrexSQL extensions in `node_modules/@trex/` or custom path
- `TREX_SQL_PASSWORD` environment variable (for server mode)

## Quick Start

### Standalone Server

```bash
export TREX_SQL_PASSWORD=your_password
java -jar trexsql.jar
```

### Library Mode (Java)

```java
import com.trex.Trexsql;
import java.util.*;

// Initialize
Map<String, Object> config = new HashMap<>();
Object db = Trexsql.init(config);

// Create cache from source database
Map<String, Object> cacheConfig = new HashMap<>();
cacheConfig.put("database-code", "my_source");
cacheConfig.put("schema-name", "cdm");
cacheConfig.put("jdbc-url", "jdbc:sqlserver://host:1433");
cacheConfig.put("username", "user");
cacheConfig.put("password", "pass");
cacheConfig.put("dialect", "sql server");

Trexsql.createCache(db, cacheConfig, event -> {
    System.out.println("Progress: " + event.get("phase") + " " + event.get("table"));
});

Trexsql.shutdown(db);
```

## Development

```bash
# Run tests
lein test

# Start REPL
lein repl

# Run standalone
lein run -- --help

# Build uberjar
lein uberjar
```

## CLI Options

```
--trexas-host HOST        Trexas server host (default: 0.0.0.0)
--trexas-port PORT        Trexas server port (default: 9876)
--pgwire-host HOST        PgWire server host (default: 0.0.0.0)
--pgwire-port PORT        PgWire server port (default: 5433)
--main-path PATH          Path to main service directory (default: ./main)
--event-worker-path PATH  Path to event worker directory
--tls-cert PATH           Path to TLS certificate file
--tls-key PATH            Path to TLS private key file
--tls-port PORT           TLS port (default: 9443)
--enable-inspector        Enable inspector
--inspector-type TYPE     Inspector type (default: inspect)
--inspector-host HOST     Inspector host (default: 0.0.0.0)
--inspector-port PORT     Inspector port (default: 9229)
--allow-main-inspector    Allow inspector in main worker
-h, --help                Show this help message
```

## License

Apache License 2.0
