(ns covid-tracker-clj.routes.thl
  (:require [covid-tracker-clj.schemas.thl :refer [thl-all-response]]
            [covid-tracker-clj.services.thl-api :refer [get-thl-infections]]))

(defn thl-routes []
  ["/thl"
   {:swagger {:tags ["thl"]}}
   ["/infections"
    ["/all"
     {:get {:summary "gets all infections"
            ;; :responses {200 {:body thl-all-response}}
            :handler (fn [_]
                       (let [data (get-thl-infections)]
                         (println "dsadsadsa")
                         (clojure.pprint/pprint data)
                         {:status 200
                          :body "DasDSAdASDSA"}
                         #_(print "dadada: " (:body @data))))}}]]])
