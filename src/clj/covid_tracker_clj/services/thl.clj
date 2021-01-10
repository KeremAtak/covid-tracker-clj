(ns covid-tracker-clj.services.thl
  (:require [babashka.curl :refer [get]]
            [muuntaja.core :as m]
            [covid-tracker-clj.config :refer [env]]
            [covid-tracker-clj.utils.thl :as thl-utils]))

(defn thl-cases->locations [thl-cases]
  (->> thl-cases :dataset :dimension :hcdmunicipality2020 :category :label))

(defn thl-cases->infections [thl-cases]
  (->> thl-cases :dataset :value))

(defn thl-cases->index [thl-cases]
  (->> thl-cases :dataset :dimension :hcdmunicipality2020 :category :index))

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
      (cond
        (= (str municipality-kwd) ":Koski Tl") :Koski-Tl
        (= (str municipality-kwd) ":Pedersören Kunta") :Pedersören-Kunta
        :else (do (println "Failure: " (str municipality-kwd))
                  :failure)))))

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

(defn insert-location-1 [{:keys [infection-count location mapped-locations]}]
  (let [keywordized-location (keyword location)]
    (into mapped-locations {keywordized-location
                            {:statistics {:infections infection-count}}})))


(defn insert-location-2 [{:keys [infection-count location mapped-locations]}]
  (let [keywordized-location (keyword location)]
    (assoc-in mapped-locations [:shapes keywordized-location :statistics :infections] infection-count)))

(defn locations-and-infections->mapped-locations-1
  "Recursively returns a map which tells the total infection count of each location"
  [{:keys [index infections locations mapped-locations]}]
  (if (empty? index)
    mapped-locations
    (let [[index-kwd index-value] (first index)
          keywordized-index-value (keyword (str index-value))
          infection-count (keywordized-index-value infections)
          location (index-kwd locations)
          rest-index (rest index)
          new-mapped-locations (insert-location-1 {:index-value index-value
                                                 :infection-count infection-count
                                                 :location location
                                                 :mapped-locations mapped-locations})]
      (locations-and-infections->mapped-locations-1
       {:index rest-index
        :infections infections
        :locations locations
        :mapped-locations new-mapped-locations}))))


(defn locations-and-infections->mapped-locations-2
  "Recursively returns a map which tells the total infection count of each location"
  [{:keys [index infections locations mapped-locations]}]
  (if (empty? index)
    mapped-locations
    (let [[index-kwd index-value] (first index)
          keywordized-index-value (keyword (str index-value))
          infection-count (keywordized-index-value infections)
          location (index-kwd locations)
          rest-index (rest index)
          new-mapped-locations (insert-location-2 {:index-value index-value
                                                   :infection-count infection-count
                                                   :location location
                                                   :mapped-locations mapped-locations})]
      (locations-and-infections->mapped-locations-2
       {:index rest-index
        :infections infections
        :locations locations
        :mapped-locations new-mapped-locations}))))

(defn hospital-district->province
  "Replaces hospital district name with province's name."
  [[district-kwd district-value]]
  (let [province-name (thl-utils/district-value->province-name district-value)]
    [district-kwd province-name]))

(defn hospital-districts->provinces
  "Replaces hospital district names with provinces' names. Länsi-Pohja and Itä-Savo get different names."
  [hospital-districts]
  (into {} (map hospital-district->province hospital-districts)))

(defn provinces->provinces-with-total-infections
  "Adds each provinces' total infection count."
  [{:keys [province-cases mapped-provinces]}]
  (let [index (thl-cases->index province-cases)
        infections (thl-cases->infections province-cases)
        hospital-districts (thl-cases->locations province-cases)
        provinces (hospital-districts->provinces hospital-districts)]
    (locations-and-infections->mapped-locations-2
     {:index index
      :infections infections
      :locations provinces
      :mapped-locations mapped-provinces})))

(defn thl-cases->provinces [{:keys [municipality-cases province-cases]}]
  (let [index (thl-cases->index municipality-cases)
        infections (thl-cases->infections municipality-cases)
        municipalities (thl-cases->locations municipality-cases)
        mapped-municipalities (locations-and-infections->mapped-locations-1
                               {:index index
                                :infections infections
                                :locations municipalities
                                :mapped-locations thl-utils/empty-provinces})
        mapped-provinces (mapped-municipalities->provinces {:municipalities mapped-municipalities
                                                            :provinces thl-utils/empty-provinces})
        provinces-with-total-infections (provinces->provinces-with-total-infections {:province-cases province-cases
                                                                                     :mapped-provinces mapped-provinces})]
    (clojure.pprint/pprint provinces-with-total-infections)
    provinces-with-total-infections))

(defn curl-thl [address]
  (->> address get :body (m/decode "application/json")))

(defn get-thl-infections []
  (let [thl-municipality-cases (curl-thl (:thl-municipality-infections env))
        thl-province-cases (curl-thl (:thl-province-infections env))
        thl-deaths (curl-thl "https://sampo.thl.fi/pivot/prod/fi/epirapo/covid19case/fact_epirapo_covid19case.json?row=hcdmunicipality2020-445222&column=dateweek20200101-509030&filter=measure-492118")
        provinces (thl-cases->provinces {:municipality-cases thl-municipality-cases
                                         :province-cases thl-province-cases})]
    provinces))
