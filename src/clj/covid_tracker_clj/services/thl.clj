(ns covid-tracker-clj.services.thl
  (:require [babashka.curl :as curl]
            [muuntaja.core :as m]
            [covid-tracker-clj.config :as config]
            [covid-tracker-clj.utils.thl :as thl-utils]))

(defn thl-cases->locations [thl-cases]
  (get-in thl-cases [:dataset :dimension :hcdmunicipality2020 :category :label]))

(defn thl-cases->value [thl-cases]
  (get-in thl-cases [:dataset :value]))

(defn thl-cases->index [thl-cases]
  (get-in thl-cases [:dataset :dimension :hcdmunicipality2020 :category :index]))

(defn in-province?
  "Returns province's keyword if it has the municipality."
  [[province-kwd municipalities] municipality-name]
  (when (contains? municipalities municipality-name)
    province-kwd))

(defn municipality-name->municipality-kwd [municipality-name]
  ;; this is pretty ugly code.. consider not using keywords as names
  (cond
    (= "Pedersören kunta" municipality-name) :Pedersören-kunta
    (= "Koski Tl" municipality-name) :Koski-Tl
    :else (keyword municipality-name)))

(defn thl-cases->mapped-municipalities [municipality-cases]
  (let [indeces (thl-cases->index municipality-cases)
        value (thl-cases->value municipality-cases)
        municipalities (thl-cases->locations municipality-cases)]
    (reduce (fn [accu [index-kwd index-value]]
              (let [keywordized-index-value (keyword (str index-value))
                    infection-count (keywordized-index-value value)
                    municipality-kwd (municipality-name->municipality-kwd (index-kwd municipalities))
                    province-kwd (some #(in-province? % municipality-kwd) thl-utils/provinces)]
                (assoc-in accu [:shapes province-kwd :municipalities municipality-kwd :statistics :infections] infection-count)))
            thl-utils/empty-provinces
            indeces)))

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
  (let [indeces (thl-cases->index province-cases)
        infections (thl-cases->value province-cases)
        hospital-districts (thl-cases->locations province-cases)
        provinces (hospital-districts->provinces hospital-districts)]
    (reduce (fn [accu [index-kwd index-value]]
              (let [keywordized-index-value (keyword (str index-value))
                    infection-count (keywordized-index-value infections)
                    province (index-kwd provinces)
                    province-kwd (keyword province)]
                (assoc-in accu [:shapes province-kwd :statistics :infections] infection-count)))
            mapped-municipalities
            indeces)))

(defn provinces-and-total-deaths->provinces-with-deaths
  "Inserts the total deaths to provinces."
  [{:keys [provinces province-deaths]}]
  (let [deaths (thl-cases->value province-deaths)
        deaths-count (val (first deaths))]
    (assoc-in provinces [:shapes :Kaikki :statistics :deaths] deaths-count)))

(defn thl-cases->provinces [municipality-cases province-cases province-deaths]
  (let [mapped-municipalities (thl-cases->mapped-municipalities municipality-cases)
        provinces-with-total-infections (provinces->provinces-with-total-infections
                                         {:mapped-municipalities mapped-municipalities
                                          :province-cases province-cases})]
    (provinces-and-total-deaths->provinces-with-deaths {:province-deaths province-deaths
                                                        :provinces provinces-with-total-infections})))

(defn curl-thl [address]
  (m/decode "application/json" (:body (curl/get address))))

(defn get-thl-infections []
  (let [municipality-cases (curl-thl (:thl-municipality-infections config/env))
        province-cases (curl-thl (:thl-province-infections config/env))
        province-deaths (curl-thl (:thl-province-deaths config/env))]
    (thl-cases->provinces municipality-cases province-cases province-deaths)))
