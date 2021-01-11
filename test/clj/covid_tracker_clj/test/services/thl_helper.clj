(ns covid-tracker-clj.test.services.thl-helper)

(def provinces {:shapes
                {:Kaikki {:statistics {:infections "38590"
                                       :deaths "513"}}
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
                 :Etelä-Savo {}
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
                 :Pohjanmaa
                 {:statistics {:infections "331"}
                  :municipalities
                  {:Pedersören-kunta {:statistics {:infections ".."}}}}
                 :Ahvenanmaa {}
                 :Varsinais-Suomi
                 {:statistics {:infections "512"}
                  :municipalities
                  {:Koski-Tl {:statistics {:infections "66"}}}}}})

(def municipality-cases {:dataset
                         {:value {:10 "66"
                                  :16 "129"
                                  :8 "5012"
                                  :14 "2411"
                                  :50 ".."
                                  :102 ".."}
                          :dimension
                          {:hcdmunicipality2020
                           {:category
                            {:index {:445177 10
                                     :444182 8
                                     :447912 16
                                     :447712 14
                                     :446221 102
                                     :444111 50}
                             :label {:445177 "Koski Tl"
                                     :444182 "Helsinki"
                                     :444111 "Rääkkylä"
                                     :447712 "Espoo"
                                     :446221 "Pedersören kunta"
                                     :447912 "Seinäjoki"}}}}}})

(def province-cases {:dataset
                     {:value
                      {:10 "410"
                       :21 "38590"
                       :13 "409"
                       :2 "331"
                       :8 "512"
                       :20 "22527"}
                      :dimension
                      {:hcdmunicipality2020
                       {:category
                        {:index
                         {:445222 21
                          :445193 20
                          :445155 8
                          :445293 10
                          :445225 13
                          :445212 2}
                         :label
                         {:445222 "Kaikki Alueet"
                          :445193 "Helsingin ja Uudenmaan SHP"
                          :445155 "Varsinais-Suomen SHP"
                          :445293 "Pohjois-Karjalan SHP"
                          :445212 "Vaasan SHP"
                          :445225 "Etelä-Pohjanmaan SHP"}}}}}})

(def province-death-cases {:dataset
                           {:value {:21 "513"}
                            :dimension
                            {:hcdmunicipality2020
                             {:category
                              {:index
                               {:445170 2
                                :445223 11
                                :445206 3
                                :445175 9
                                :445043 7
                                :445222 21
                                :444996 16
                                :445190 18
                                :445282 4
                                :445230 15
                                :445224 19
                                :445285 12
                                :445193 20
                                :445014 5
                                :445155 8
                                :445293 10
                                :445131 0
                                :445079 14
                                :445197 1
                                :445225 13
                                :445178 6
                                :445101 17}
                               :label
                               {:445170 "Satakunnan SHP"
                                :445223 "Pohjois-Savon SHP"
                                :445206 "Kanta-Hämeen SHP"
                                :445175 "Itä-Savon SHP"
                                :445043 "Etelä-Karjalan SHP"
                                :445222 "Kaikki Alueet"
                                :444996 "Pohjois-Pohjanmaan SHP"
                                :445190 "Länsi-Pohjan SHP"
                                :445282 "Pirkanmaan SHP"
                                :445230 "Keski-Pohjanmaan SHP"
                                :445224 "Lapin SHP"
                                :445285 "Keski-Suomen SHP"
                                :445193 "Helsingin ja Uudenmaan SHP"
                                :445014 "Päijät-Hämeen SHP"
                                :445155 "Etelä-Savon SHP"
                                :445293 "Pohjois-Karjalan SHP"
                                :445131 "Ahvenanmaa"
                                :445079 "Vaasan SHP"
                                :445197 "Varsinais-Suomen SHP"
                                :445225 "Etelä-Pohjanmaan SHP"
                                :445178 "Kymenlaakson SHP"
                                :445101 "Kainuun SHP"}}}}}})
