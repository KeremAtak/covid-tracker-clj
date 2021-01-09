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
  (testing "curl-thl"
    (let [test-url "https://sampo.thl.fi/pivot/prod/api/epirapo/covid19case.json"
          test-data (thl-service/curl-thl test-url)]
      (is (contains? test-data :updated))))
  (testing "thl-cases->mapped-municipalities"
    (let [thl-cases {:dataset
                     {:value {:10 "66"
                              :16 "129"
                              :8 "5012"
                              :50 ".."}
                      :dimension
                      {:hcdmunicipality2020
                       {:category
                        {:index {:445177 10
                                 :444182 8
                                 :447912 16
                                 :444111 50}
                         :label {:445177 "Juva"
                                 :444182 "Helsinki"
                                 :444111 "Rääkkylä"
                                 :447912 "Seinäjoki"}}}}}}
          mapped-municipalities {:Juva {:statistics {:infections "66"}}
                                 :Helsinki {:statistics {:infections "5012"}}
                                 :Seinäjoki {:statistics {:infections "129"}}
                                 :Rääkkylä {:statistics {:infections ".."}}}]
      (is (= mapped-municipalities
             (thl-service/thl-cases->mapped-municipalities thl-cases))))))
