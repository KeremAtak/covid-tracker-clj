(ns covid-tracker-clj.services.thl
  (:require [babashka.curl :refer [get]]
            [muuntaja.core :as m]
            [covid-tracker-clj.config :refer [env]]))

(defn get-thl-infections []
  (->> 
   (:thl-url env)
   get
   :body
   (m/decode "application/json")))
