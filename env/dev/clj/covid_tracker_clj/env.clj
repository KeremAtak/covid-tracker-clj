(ns covid-tracker-clj.env
  (:require [covid-tracker-clj.dev-middleware :refer [wrap-dev]]
            [clojure.tools.logging :as log]
            [selmer.parser :as parser]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[covid-tracker-clj started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[covid-tracker-clj has shut down successfully]=-"))
   :middleware wrap-dev})
