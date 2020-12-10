(ns covid-tracker-clj.schemas.math
  (:require [clojure.spec.alpha :as s]))

(s/def ::x int?)
(s/def ::y int?)
(s/def ::total pos-int?)

(def plus-request (s/keys :req-un [::x ::y]))
(def plus-response (s/keys :req-un [::total]))
