(ns covid-tracker-clj.routes.api
    (:require [covid-tracker-clj.middleware.exception :as exception]
              [covid-tracker-clj.middleware.formats :as formats]
              [covid-tracker-clj.routes.files :refer [file-routes]]
              [covid-tracker-clj.routes.health :refer [health-routes]]
              [covid-tracker-clj.routes.math :refer [math-routes]]
              [covid-tracker-clj.routes.thl :refer [thl-routes]]
              [reitit.coercion.spec :as spec-coercion]
              [reitit.ring.coercion :as coercion]
              [reitit.ring.middleware.multipart :as multipart]
              [reitit.ring.middleware.muuntaja :as muuntaja]
              [reitit.ring.middleware.parameters :as parameters]
              [reitit.swagger :as swagger]
              [reitit.swagger-ui :as swagger-ui]))

(defn api-routes []
  ["/api"
   {:coercion spec-coercion/coercion
    :muuntaja formats/instance
    :swagger {:id ::api}
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 exception/exception-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware]}

   ;; swagger documentation
   ["" {:no-doc true
        :swagger {:info {:title "my-api"
                         :description "https://cljdoc.org/d/metosin/reitit"}}}

    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]

    ["/api-docs/*"
     {:get (swagger-ui/create-swagger-ui-handler
            {:url "/api/swagger.json"
             :config {:validator-url nil}})}]]
   (file-routes)
   (health-routes)
   (math-routes)
   (thl-routes)])
