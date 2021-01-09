(ns covid-tracker-clj.test.services.thl-helper)

(def provinces {:shapes
                {:Keski-Pohjanmaa {}
                 :Pohjois-Pohjanmaa {}
                 :Uusimaa
                 {:municipalities
                  {:Helsinki {:statistics {:infections "5012"}}
                   :Espoo {:statistics {:infections "2411"}}}}
                 :Lappi {}
                 :Keski-Suomi {}
                 :Päijät-Häme {}
                 :Pirkanmaa {}
                 :Etelä-Savo
                 {:municipalities {:Juva {:statistics {:infections "66"}}}}
                 :Pohjois-Karjala
                 {:municipalities
                  {:Rääkkylä {:statistics {:infections ".."}}}}
                 :Satakunta {}
                 :Etelä-Karjala {}
                 :Pohjois-Savo {}
                 :Kainuu {}
                 :Kymenlaakso {}
                 :Etelä-Pohjanmaa
                 {:municipalities
                  {:Seinäjoki {:statistics {:infections "129"}}}}
                 :Pohjanmaa {}
                 :Ahvenanmaa {}
                 :Varsinais-Suomi {}}})

(def thl-cases {:dataset
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
