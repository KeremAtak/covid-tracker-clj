(ns covid-tracker-clj.services.thl
  (:require [babashka.curl :refer [get]]
            [muuntaja.core :as m]
            [covid-tracker-clj.config :refer [env]]))

(defn curl-thl [address]
  (->> address get :body (m/decode "application/json")))

(defn insert-municipality [{:keys [infection-count municipality mapped-municipalities]}]
  (let [keywordized-municipality (keyword municipality)]
    (into mapped-municipalities {keywordized-municipality
                                 {:statistics {:infections infection-count}}})))

(defn municipalities-and-infections->mapped-municipalities
  "Recursively returns a map which tells the total infection count of each municipality"
  [{:keys [index infections municipalities mapped-municipalities]}]
  (if (empty? index)
    mapped-municipalities
    (let [[index-kwd index-value] (first index)
          keywordized-index-value (keyword (str index-value))
          infection-count (keywordized-index-value infections)
          municipality (index-kwd municipalities)
          rest-index (rest index)
          new-mapped-municipalities (insert-municipality {:index-value index-value
                                                          :infection-count infection-count
                                                          :municipality municipality
                                                          :mapped-municipalities mapped-municipalities})]
      (municipalities-and-infections->mapped-municipalities
       {:index rest-index
        :infections infections
        :municipalities municipalities
        :mapped-municipalities new-mapped-municipalities}))))

(defn thl-cases->municipalities [thl-cases]
  (->> thl-cases :dataset :dimension :hcdmunicipality2020 :category :label))

(defn thl-cases->infections [thl-cases]
  (->> thl-cases :dataset :value))

(defn thl-cases->index [thl-cases]
  (->> thl-cases :dataset :dimension :hcdmunicipality2020 :category :index))

(defn thl-cases->mapped-municipalities [thl-cases]
  (let [index (thl-cases->index thl-cases)
        infections (thl-cases->infections thl-cases)
        municipalities (thl-cases->municipalities thl-cases)
        mapped-municipalities (municipalities-and-infections->mapped-municipalities
                               {:index index
                                :infections infections
                                :municipalities municipalities
                                :mapped-municipalities {}})]
    mapped-municipalities))

(defn get-thl-infections []
  (let [thl-cases (curl-thl (:thl-infections env))
        thl-deaths (curl-thl "https://sampo.thl.fi/pivot/prod/fi/epirapo/covid19case/fact_epirapo_covid19case.json?row=hcdmunicipality2020-445222&column=dateweek20200101-509030&filter=measure-492118")
        mapped-municipalities (thl-cases->mapped-municipalities thl-cases)]
    mapped-municipalities))
