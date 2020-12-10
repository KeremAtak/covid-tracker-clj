(ns covid-tracker-clj.schemas.files
    (:require [clojure.spec.alpha :as s]
              [reitit.ring.middleware.multipart :as multipart]))

(s/def ::file multipart/temp-file-part)
(s/def ::name string?)
(s/def ::size int?)

(def upload-request (s/keys :req-un [::file]))
(def upload-response (s/keys :req-un [::name ::size]))
