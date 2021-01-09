(ns covid-tracker-clj.services.thl
  (:require [babashka.curl :refer [get]]
            [muuntaja.core :as m]
            [covid-tracker-clj.config :refer [env]]
            [covid-tracker-clj.utils.thl :as thl-utils]))

(defn municipality-to-province [{:keys [municipality province-kwd provinces]}]
  (let [[municipality-kwd municipality-data] municipality]
    (assoc-in provinces [:shapes province-kwd :municipalities municipality-kwd] municipality-data)))

(defn in-province?
  "Returns province's keyword if it has the municipality."
  [[province-kwd municipalities] municipality-name]
  (if (contains? municipalities municipality-name)
    province-kwd
    false))

(defn mapped-municipality->province-keyword
  "Returns the first found matching province keyword"
  [municipality]
  (let [municipality-kwd (first municipality)
        provinces thl-utils/provinces]
    (if-let [province-kwd (some #(in-province? % municipality-kwd) provinces)]
      province-kwd
      ;; This is pretty ugly code.. reconsider using names as keywords
      (if (= (str municipality-kwd) ":Koski Tl")
        :Koski-Tl
        :Pedersören-Kunta))))

(defn mapped-municipalities->provinces [{:keys [municipalities provinces]}]
  (if (empty? municipalities)
    provinces
    (let [municipality (first municipalities)
          rest-municipalities (rest municipalities)
          province-kwd (mapped-municipality->province-keyword municipality)
          new-provinces (municipality-to-province {:municipality municipality
                                                   :province-kwd province-kwd
                                                   :provinces provinces})]
      (mapped-municipalities->provinces {:municipalities rest-municipalities
                                         :provinces new-provinces}))))

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

(defn thl-cases->provinces [thl-cases]
  (let [index (thl-cases->index thl-cases)
        infections (thl-cases->infections thl-cases)
        municipalities (thl-cases->municipalities thl-cases)
        mapped-municipalities (municipalities-and-infections->mapped-municipalities
                               {:index index
                                :infections infections
                                :municipalities municipalities
                                :mapped-municipalities {}})
        provinces (mapped-municipalities->provinces {:municipalities mapped-municipalities
                                                     :provinces thl-utils/empty-provinces})]
    provinces))

(defn curl-thl [address]
  (->> address get :body (m/decode "application/json")))

(defn get-thl-infections []
  (let [thl-cases (curl-thl (:thl-infections env))
        thl-deaths (curl-thl "https://sampo.thl.fi/pivot/prod/fi/epirapo/covid19case/fact_epirapo_covid19case.json?row=hcdmunicipality2020-445222&column=dateweek20200101-509030&filter=measure-492118")
        provinces (thl-cases->provinces thl-cases)]
    provinces))
