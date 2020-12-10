(ns covid-tracker-clj.routes.math
  (:require [covid-tracker-clj.services.math :refer [sum]]
            [covid-tracker-clj.schemas.math :refer [plus-request plus-response]]))


(defn math-routes []
   ;; swagger documentation
  ["/math"
   {:swagger {:tags ["math"]}}
   ["/plus"
    {:get {:summary "plus with spec query parameters"
           :parameters {:query plus-request}
           :responses {200 {:body plus-response}}
           :handler (fn [{{:keys [query]} :parameters}]
                      {:status 200
                       :body {:total (sum query)}})}
     :post {:summary "plus with spec body parameters"
            :parameters {:body plus-request}
            :responses {200 {:body plus-response}}
            :handler (fn [{{:keys [body]} :parameters}]
                       {:status 200
                        :body {:total (sum body)}})}}]])

