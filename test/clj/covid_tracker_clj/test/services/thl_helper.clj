(ns covid-tracker-clj.test.services.thl-helper)

(def mapped-municipalities {:Juva {:statistics {:infections "66"}}
                            :Helsinki {:statistics {:infections "5012"}}
                            :Seinäjoki {:statistics {:infections "129"}}
                            :Rääkkylä {:statistics {:infections ".."}}})

(def thl-cases {:dataset
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
                            :447912 "Seinäjoki"}}}}}})
