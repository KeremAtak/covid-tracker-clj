(ns covid-tracker-clj.test.services.thl
  (:require [clojure.test :refer :all]
            [covid-tracker-clj.services.thl :as thl-service]
            [covid-tracker-clj.test.services.thl-helper :as thl-helper]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'covid-tracker-clj.config/env
                 #'covid-tracker-clj.handler/app-routes)
    (f)))

(deftest test-thl
  (testing "curl-thl"
    (let [test-url "https://sampo.thl.fi/pivot/prod/api/epirapo/covid19case.json"
          test-data (thl-service/curl-thl test-url)]
      (is (contains? test-data :updated))))
  (testing "thl-cases->provinces"
    (let [municipality-cases thl-helper/municipality-cases
          province-cases thl-helper/province-cases
          province-death-cases thl-helper/province-death-cases
          provinces thl-helper/provinces]
      (is (= provinces
             (thl-service/thl-cases->provinces {:municipality-cases municipality-cases
                                                :province-cases province-cases
                                                :province-deaths province-death-cases}))))))
