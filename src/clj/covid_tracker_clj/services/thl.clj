(ns covid-tracker-clj.services.thl
  (:require [babashka.curl :refer [get]]
            [muuntaja.core :as m]
            [covid-tracker-clj.config :refer [env]]))

(defn curl-thl [address]
  (->> 
   address
   get
   :body
   (m/decode "application/json")))

(defn get-thl-infections []
  (curl-thl (:thl-url env)))
