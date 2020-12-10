(ns covid-tracker-clj.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[covid-tracker-clj started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[covid-tracker-clj has shut down successfully]=-"))
   :middleware identity})
