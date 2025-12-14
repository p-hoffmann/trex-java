# Trexsql - Clojure DuckDB Library for TREX

A JVM Clojure library wrapping DuckDB v1.4.0 via the official JDBC driver. Provides both standalone server mode (replicating existing bao behavior) and embeddable library mode with Java-friendly API.

## Prerequisites

- JDK 17 or later
- Leiningen (for development) or just the uberjar
- DuckDB extensions in `node_modules/@trex/` or custom path
- `TREX_SQL_PASSWORD` environment variable (for server mode)

## Quick Start

### Standalone Server

```bash
export TREX_SQL_PASSWORD=your_password
java -jar trexsql.jar
```

### Library Mode (Clojure)

```clojure
(require '[trexsql.core :as trexsql])

(def db (trexsql/init {:extensions-path "./extensions"}))
(trexsql/execute! db "CREATE TABLE users (id INTEGER, name VARCHAR)")
(trexsql/query db "SELECT * FROM users")
(trexsql/shutdown! db)
```

### Library Mode (Java)

```java
import com.trex.Trexsql;
import java.util.*;

Map<String, Object> config = new HashMap<>();
Object db = Trexsql.init(config);
Trexsql.execute(db, "CREATE TABLE users (id INTEGER, name VARCHAR)");
List<Map<String, Object>> results = Trexsql.query(db, "SELECT * FROM users");
Trexsql.shutdown(db);
```

## Development

```bash
cd trexsql/trexsql

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
