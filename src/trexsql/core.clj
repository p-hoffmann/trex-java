(ns trexsql.core
  "Main entry point for Trexsql - Clojure DuckDB library."
  (:require [trexsql.db :as db]
            [trexsql.config :as config]
            [trexsql.extensions :as ext]
            [trexsql.servers :as servers])
  (:gen-class))

(def ^:private shutdown-promise (promise))
(def ^:private current-database (atom nil))

(defn add-shutdown-hook!
  "Register a JVM shutdown hook for graceful cleanup."
  [cleanup-fn]
  (.addShutdownHook
   (Runtime/getRuntime)
   (Thread. ^Runnable cleanup-fn)))

(defn shutdown!
  "Gracefully shutdown the database and any running servers."
  [database]
  (println "\n\nShutting down...")
  (when database
    (db/close! database))
  (reset! current-database nil))

(defn init
  "Initialize DuckDB database with extensions loaded.
   Config map can include :extensions-path to override default.
   Returns TrexsqlDatabase record."
  ([]
   (init {}))
  ([config]
   (let [merged-config (merge config/default-config config)
         extensions-path (config/get-extensions-path merged-config)
         conn (db/create-connection)
         loaded (ext/load-extensions conn extensions-path)
         database (-> (db/make-database conn merged-config)
                      (assoc :extensions-loaded loaded))]
     (reset! current-database database)
     database)))

(defn init-with-servers
  "Initialize DuckDB database and start Trexas/PgWire servers.
   Requires TREX_SQL_PASSWORD environment variable.
   Config map can override server ports, paths, etc.
   Returns TrexsqlDatabase record with servers running."
  ([]
   (init-with-servers {}))
  ([config]
   (let [database (init config)
         merged-config (merge config/default-config config)
         database-with-servers (servers/start-servers! database merged-config)]
     (reset! current-database database-with-servers)
     (servers/print-server-status merged-config)
     database-with-servers)))

(defn is-running?
  "Check if servers are currently running."
  [database]
  (boolean (:servers-running? database)))

(defn query
  "Execute a SQL query and return results.
   Wrapper around db/query for convenience."
  [database sql]
  (db/query database sql))

(defn execute!
  "Execute a non-query SQL statement.
   Wrapper around db/execute! for convenience."
  [database sql]
  (db/execute! database sql))

(defn loaded-extensions
  "Return set of loaded extension names."
  [database]
  (ext/loaded-extensions database))

(defn- print-help-and-exit
  "Print help message and exit."
  []
  (println config/help-text)
  (System/exit 0))

(defn- print-errors-and-exit
  "Print validation errors and exit."
  [errors]
  (binding [*out* *err*]
    (doseq [err errors]
      (println (str "Error: " err))))
  (System/exit 1))

(defn -main
  "Main entry point for standalone server mode."
  [& args]
  (let [{:keys [options errors summary]} (config/parse-args args)]
    (when (:help options)
      (print-help-and-exit))
    (when (seq errors)
      (print-errors-and-exit errors))
    (println "\uD83E\uDD95 Starting TREX")
    (let [database (init-with-servers options)]
      (add-shutdown-hook!
       #(shutdown! @current-database))
      @shutdown-promise)))
