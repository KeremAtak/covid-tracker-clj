(ns covid-tracker-clj.services.thl
  (:require [babashka.curl :refer [get]]
            [muuntaja.core :as m]
            [covid-tracker-clj.config :refer [env]]
            [covid-tracker-clj.utils.thl :as thl-utils]))

(defn thl-cases->locations [thl-cases]
  (->> thl-cases :dataset :dimension :hcdmunicipality2020 :category :label))

(defn thl-cases->value [thl-cases]
  (->> thl-cases :dataset :value))

(defn thl-cases->index [thl-cases]
  (->> thl-cases :dataset :dimension :hcdmunicipality2020 :category :index))

(defn insert-statistics
  "Inserts statistics to a position pointed by map-keywords."
  [{:keys [case-count map-keywords mapped-locations]}]
  (assoc-in mapped-locations map-keywords case-count))

(defn in-province?
  "Returns province's keyword if it has the municipality."
  [[province-kwd municipalities] municipality-name]
  (if (contains? municipalities municipality-name)
    province-kwd
    false))

(defn municipality-kwd->province-kwd
  "Returns the first found matching province keyword"
  [municipality-kwd]
  (let [provinces thl-utils/provinces]
    (some #(in-province? % municipality-kwd) provinces)))

(defn municipality-name->municipality-kwd [municipality-name]
  ;; this is pretty ugly code.. consider not using keywords as names
  (cond
    (= "Pedersören kunta" municipality-name) :Pedersören-kunta
    (= "Koski Tl" municipality-name) :Koski-Tl
    :else (keyword municipality-name)))

(defn municipalities-and-infections->mapped-municipalities
  "Recursively returns a map which tells the total infection count of each municipality."
  [{:keys [index infections municipalities mapped-municipalities]}]
  (if (empty? index)
    mapped-municipalities
    (let [[index-kwd index-value] (first index)
          keywordized-index-value (keyword (str index-value))
          infection-count (keywordized-index-value infections)
          municipality-name (index-kwd municipalities)
          municipality-kwd (municipality-name->municipality-kwd municipality-name)
          province-kwd (municipality-kwd->province-kwd municipality-kwd)
          rest-index (rest index)
          new-mapped-municipalities (insert-statistics
                                     {:case-count infection-count
                                      :map-keywords [:shapes province-kwd :municipalities municipality-kwd :statistics :infections]
                                      :mapped-locations mapped-municipalities})]
      (municipalities-and-infections->mapped-municipalities
       {:index rest-index
        :infections infections
        :municipalities municipalities
        :mapped-municipalities new-mapped-municipalities}))))

(defn thl-cases->mapped-municipalities [municipality-cases]
  (let [index (thl-cases->index municipality-cases)
        value (thl-cases->value municipality-cases)
        municipalities (thl-cases->locations municipality-cases)]
    (municipalities-and-infections->mapped-municipalities
     {:index index
      :infections value
      :municipalities municipalities
      :mapped-municipalities thl-utils/empty-provinces})))

(defn provinces-and-infections->provinces-with-total-infections
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
                                {:case-count infection-count
                                 :map-keywords [:shapes province-kwd :statistics :infections]
                                 :mapped-locations mapped-provinces})]
      (provinces-and-infections->provinces-with-total-infections
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
        value (thl-cases->value province-cases)
        hospital-districts (thl-cases->locations province-cases)
        provinces (hospital-districts->provinces hospital-districts)]
    (provinces-and-infections->provinces-with-total-infections
     {:index index
      :infections value
      :provinces provinces
      :mapped-provinces mapped-municipalities})))

(defn provinces-and-total-deaths->provinces-with-deaths
  "Inserts the total deaths to provinces."
  [{:keys [provinces province-deaths]}]
  (let [deaths (thl-cases->value province-deaths)
        deaths-count (val (first deaths))]
    (insert-statistics
     {:case-count deaths-count
      :map-keywords [:shapes :Kaikki :statistics :deaths]
      :mapped-locations provinces})))

(defn thl-cases->provinces [{:keys [municipality-cases province-cases province-deaths]}]
  (let [mapped-municipalities (thl-cases->mapped-municipalities municipality-cases)
        provinces-with-total-infections (provinces->provinces-with-total-infections
                                         {:mapped-municipalities mapped-municipalities
                                          :province-cases province-cases})]
    (provinces-and-total-deaths->provinces-with-deaths {:province-deaths province-deaths
                                                        :provinces provinces-with-total-infections})))

(defn curl-thl [address]
  (->> address get :body (m/decode "application/json")))

(defn get-thl-infections []
  (let [thl-municipality-cases (curl-thl (:thl-municipality-infections env))
        thl-province-cases (curl-thl (:thl-province-infections env))
        thl-province-deaths (curl-thl (:thl-province-deaths env))]
    (thl-cases->provinces {:municipality-cases thl-municipality-cases
                           :province-cases thl-province-cases
                           :province-deaths thl-province-deaths})))
