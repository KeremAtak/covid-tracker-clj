(ns covid-tracker-clj.test.services.thl-helper)

(def provinces {:shapes
                {:Kaikki {:statistics {:infections "38590"}}
                 :Keski-Pohjanmaa {}
                 :Pohjois-Pohjanmaa {}
                 :Uusimaa
                 {:statistics {:infections "22527"}
                  :municipalities
                  {:Helsinki {:statistics {:infections "5012"}}
                   :Espoo {:statistics {:infections "2411"}}}}
                 :Lappi {}
                 :Keski-Suomi {}
                 :Päijät-Häme {}
                 :Pirkanmaa {}
                 :Etelä-Savo
                 {:statistics {:infections "512"}
                  :municipalities
                  {:Juva {:statistics {:infections "66"}}}}
                 :Pohjois-Karjala
                 {:statistics {:infections "410"}
                  :municipalities
                  {:Rääkkylä {:statistics {:infections ".."}}}}
                 :Satakunta {}
                 :Etelä-Karjala {}
                 :Pohjois-Savo {}
                 :Kainuu {}
                 :Kymenlaakso {}
                 :Etelä-Pohjanmaa
                 {:statistics {:infections "409"}
                  :municipalities
                  {:Seinäjoki {:statistics {:infections "129"}}}}
                 :Pohjanmaa {}
                 :Ahvenanmaa {}
                 :Varsinais-Suomi {}}})

(def municipality-cases {:dataset
                         {:value {:10 "66"
                                  :16 "129"
                                  :8 "5012"
                                  :14 "2411"
                                  :50 ".."}
                          :dimension
                          {:hcdmunicipality2020
                           {:category
                            {:index {:445177 10
                                     :444182 8
                                     :447912 16
                                     :447712 14
                                     :444111 50}
                             :label {:445177 "Juva"
                                     :444182 "Helsinki"
                                     :444111 "Rääkkylä"
                                     :447712 "Espoo"
                                     :447912 "Seinäjoki"}}}}}})

(def province-cases {:dataset
                     {:value
                      {:10 "410"
                       :21 "38590"
                       :13 "409"
                       :8 "512"
                       :20 "22527"}
                      :label "Tartuntatautirekisterin COVID-19-tapaukset"
                      :class "dataset"
                      :dimension
                      {:hcdmunicipality2020
                       {:category
                        {:index
                         {:445222 21
                          :445193 20
                          :445155 8
                          :445293 10
                          :445225 13}
                         :label
                         {:445222 "Kaikki Alueet"
                          :445193 "Helsingin ja Uudenmaan SHP"
                          :445155 "Etelä-Savon SHP"
                          :445293 "Pohjois-Karjalan SHP"
                          :445225 "Etelä-Pohjanmaan SHP"}}}
                       :measure
                       {:category
                        {:index {:444833 0}, :label {:444833 "Tapausten lukumäärä"}}}
                       :size [22 1]
                       :id ["hcdmunicipality2020" "measure"]}
                      :version "2.0"}})
