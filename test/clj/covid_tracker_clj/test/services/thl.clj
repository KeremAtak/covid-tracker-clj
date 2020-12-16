(ns covid-tracker-clj.test.services.thl
  (:require [clojure.test :refer :all]
            [covid-tracker-clj.services.thl :as thl-service]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'covid-tracker-clj.config/env
                 #'covid-tracker-clj.handler/app-routes)
    (f)))

(deftest test-thl
  (testing "curl from thl"
    (let [test-url "https://sampo.thl.fi/pivot/prod/api/epirapo/covid19case.json"
          test-data (thl-service/curl-thl test-url)]
          (is (contains? test-data :updated)))))
