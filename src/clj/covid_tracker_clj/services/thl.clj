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

(defn in-province?
  "Returns province's keyword if it has the municipality."
  [[province-kwd municipalities] municipality-name]
  (if (contains? municipalities municipality-name)
    province-kwd
    false))

(defn mapped-municipality->province-keyword
  "Returns the first found matching province keyword"
  [municipality]
  (let [provinces thl-utils/provinces]
    (if-let [province-kwd (some #(in-province? % municipality) provinces)]
      province-kwd
      (cond
        (= (str municipality) ":Koski Tl") :Koski-Tl
        (= (str municipality) ":Pedersören kunta") :Pedersören-Kunta
        :else :failure))))

(defn insert-statistics
  "Inserts statistics to a position pointed by map-keywords."
  [{:keys [infection-count map-keywords mapped-locations]}]
  (assoc-in mapped-locations map-keywords infection-count))

(defn municipalities-and-infections->mapped-municipalities
  "Recursively returns a map which tells the total infection count of each municipality."
  [{:keys [index infections municipalities mapped-municipalities]}]
  (if (empty? index)
    mapped-municipalities
    (let [[index-kwd index-value] (first index)
          keywordized-index-value (keyword (str index-value))
          infection-count (keywordized-index-value infections)
          municipality (index-kwd municipalities)
          municipality-kwd (keyword municipality)
          province-kwd (mapped-municipality->province-keyword municipality-kwd)
          rest-index (rest index)
          new-mapped-municipalities (insert-statistics
                                     {:infection-count infection-count
                                      :map-keywords [:shapes province-kwd :municipalities municipality-kwd :statistics :infections]
                                      :mapped-locations mapped-municipalities})]
      (municipalities-and-infections->mapped-municipalities
       {:index rest-index
        :infections infections
        :municipalities municipalities
        :mapped-municipalities new-mapped-municipalities}))))

(defn provinces-and-infections->mapped-provinces
  "Recursively returns a map which tells the total infection count of each province."
  [{:keys [index infections provinces mapped-provinces]}]
  (if (empty? index)
    mapped-provinces
    (let [[index-kwd index-value] (first index)
          keywordized-index-value (keyword (str index-value))
          infection-count (keywordized-index-value infections)
          province (index-kwd provinces)
          province-kwd (keyword province)
          rest-index (rest index)
          new-mapped-provinces (insert-statistics
                                {:infection-count infection-count
                                 :map-keywords [:shapes province-kwd :statistics :infections]
                                 :mapped-locations mapped-provinces})]
      (provinces-and-infections->mapped-provinces
       {:index rest-index
        :infections infections
        :provinces provinces
        :mapped-provinces new-mapped-provinces}))))

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
  [{:keys [province-cases mapped-municipalities]}]
  (let [index (thl-cases->index province-cases)
        infections (thl-cases->infections province-cases)
        hospital-districts (thl-cases->locations province-cases)
        provinces (hospital-districts->provinces hospital-districts)]
    (provinces-and-infections->mapped-provinces
     {:index index
      :infections infections
      :provinces provinces
      :mapped-provinces mapped-municipalities})))

(defn thl-cases->provinces [{:keys [municipality-cases province-cases]}]
  (let [index (thl-cases->index municipality-cases)
        infections (thl-cases->infections municipality-cases)
        municipalities (thl-cases->locations municipality-cases)
        mapped-municipalities (municipalities-and-infections->mapped-municipalities
                               {:index index
                                :infections infections
                                :municipalities municipalities
                                :mapped-municipalities thl-utils/empty-provinces})
        provinces-with-total-infections (provinces->provinces-with-total-infections
                                         {:province-cases province-cases
                                          :mapped-municipalities mapped-municipalities})]
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
