(ns user
  "Userspace functions you can run by default in your local REPL."
  (:require [covid-tracker-clj.config :refer [env]]
            [covid-tracker-clj.core :refer [start-app]]
            [covid-tracker-clj.db.core]
            [clojure.pprint]
            [clojure.spec.alpha :as s]
            [conman.core :as conman]
            [expound.alpha :as expound]
            [luminus-migrations.core :as migrations]
            [mount.core :as mount]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(add-tap (bound-fn* clojure.pprint/pprint))

(defn start
  "Starts application.
  You'll usually want to run this on startup."
  []
  (mount/start-without #'covid-tracker-clj.core/repl-server))

(defn stop
  "Stops application."
  []
  (mount/stop-except #'covid-tracker-clj.core/repl-server))

(defn restart
  "Restarts application."
  []
  (stop)
  (start))

(defn restart-db
  "Restarts covid-tracker-clj."
  []
  (mount/stop #'covid-tracker-clj.db.core/*db*)
  (mount/start #'covid-tracker-clj.db.core/*db*)
  (binding [*ns* 'covid-tracker-clj.db.core]
    (conman/bind-connection covid-tracker-clj.db.core/*db* "sql/math-queries.sql")))

(defn reset-db
  "Resets database."
  []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate
  "Migrates database up for all outstanding migrations."
  []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback
  "Rollback latest database migration."
  []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration
  "Create a new up and down migration file with a generated timestamp and `name`."
  [name]
  (migrations/create name (select-keys env [:database-url])))
