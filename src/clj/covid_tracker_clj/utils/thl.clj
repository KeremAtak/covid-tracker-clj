(ns covid-tracker-clj.utils.thl)

(def empty-provinces {:shapes {:Ahvenanmaa {}
                               :Etelä-Karjala {}
                               :Etelä-Pohjanmaa {}
                               :Etelä-Savo {}
                               :Lappi {}
                               :Kainuu {}
                               :Keski-Pohjanmaa {}
                               :Keski-Suomi {}
                               :Kymenlaakso {}
                               :Pirkanmaa {}
                               :Pohjanmaa {}
                               :Pohjois-Karjala {}
                               :Pohjois-Pohjanmaa {}
                               :Pohjois-Savo {}
                               :Päijät-Häme {}
                               :Satakunta {}
                               :Uusimaa {}
                               :Varsinais-Suomi {}}})

(def provinces {:Ahvenanmaa #{:Brändö :Eckerö :Finström :Föglö :Geta :Hammarland :Jomala :Kumlinge :Kökar :Lemland
                              :Lumparland :Maarianhamina :Saltvik :Sottunga :Sund :Vårdö}
                :Etelä-Karjala #{:Imatra :Lappeenranta :Lemi :Luumäki :Parikkala :Rautjärvi :Ruokolahti
                                 :Savitaipale :Taipalsaari}
                :Etelä-Pohjanmaa #{:Alajärvi :Alavus :Evijärvi :Ilmajoki :Isojoki :Isokyrö :Karijoki :Kauhajoki
                                   :Kauhava :Kuortane :Kurikka :Lappajärvi :Lapua :Seinäjoki :Soini :Teuva
                                   :Vimpeli :Ähtäri}
                :Etelä-Savo #{:Hirvensalmi :Joroinen :Juva :Kangasniemi :Mikkeli :Mäntyharju :Pertunmaa
                              :Pieksämäki :Puumala}
                :Itä-Savo #{:Enonkoski :Rantasalmi :Savonlinna :Sulkava}
                :Kainuu #{:Hyrynsalmi :Kajaani :Kuhmo :Paltamo :Puolanka :Ristijärvi :Sotkamo :Suomussalmi}
                :Kanta-Häme #{:Forssa :Hattula :Hausjärvi :Humppila :Hämeenlinna :Janakkala :Jokioinen :Loppi
                              :Riihimäki :Tammela :Ypäjä}
                :Keski-Pohjanmaa #{:Halsua :Kannus :Kaustinen :Kokkola :Kruunupyy :Lestijärvi :Perho :Reisjärvi
                                   :Toholampi :Veteli}
                :Keski-Suomi #{:Hankasalmi :Joutsa :Jyväskylä :Kannonkoski :Karstula :Keuruu :Kinnula :Kivijärvi
                               :Konnevesi :Kyyjärvi :Laukaa :Luhanka :Multia :Muurame :Petäjävesi :Pihtipudas
                               :Saarijärvi :Toivakka :Uurainen :Viitasaari :Äänekoski}
                :Kymenlaakso #{:Hamina :Kotka :Kouvola :Miehikkälä :Pyhtää :Virolahti}
                :Lappi #{:Enontekiö :Inari :Kemijärvi :Kittilä :Kolari :Muonio :Pelkosenniemi :Pello :Posio :Ranua
                         :Rovaniemi :Salla :Savukoski :Sodankylä :Utsjoki}
                :Länsi-Pohja #{:Kemi :Keminmaa :Simo :Tervola :Tornio :Ylitornio}
                :Pirkanmaa #{:Akaa :Hämeenkyrö :Ikaalinen :Juupajoki :Jämsä :Kangasala :Kihniö :Kuhmoinen
                             :Lempäälä :Mänttä-Vilppula :Nokia :Orivesi :Parkano :Pälkäne :Pirkkala :Ruovesi
                             :Sastamala :Tampere :Urjala :Valkeakoski :Vesilahti :Virrat :Ylöjärvi}
                :Pohjanmaa #{:Kaskinen :Korsnäs :Kristiinankaupunki :Laihia :Luoto :Maalahti :Mustasaari :Närpiö
                             :Pietarsaari :Pedersören-kunta :Uusikaarlepyy :Vaasa :Vöyri}
                :Pohjois-Karjala #{:Heinävesi :Ilomantsi :Joensuu :Juuka :Kitee :Kontiolahti :Outokumpu :Lieksa
                                   :Liperi :Nurmes :Polvijärvi :Rääkkylä :Tohmajärvi}
                :Pohjois-Pohjanmaa #{:Alavieska :Haapajärvi :Haapavesi :Hailuoto :Ii :Kalajoki :Kempele :Kuusamo
                                     :Kärsämäki :Liminka :Lumijoki :Merijärvi :Muhos :Nivala :Oulainen :Oulu
                                     :Pudasjärvi :Pyhäjoki :Pyhäjärvi :Pyhäntä :Raahe :Sievi :Siikajoki :Siikalatva
                                     :Vaala :Taivalkoski :Tyrnävä :Utajärvi :Ylivieska}
                :Pohjois-Savo #{:Iisalmi :Kaavi :Keitele :Kiuruvesi :Kuopio :Lapinlahti :Leppävirta :Pielavesi
                                :Rautalampi :Rautavaara :Siilinjärvi :Sonkajärvi :Suonenjoki :Tervo :Tuusniemi
                                :Varkaus :Vesanto :Vieremä}
                :Päijät-Häme #{:Asikkala :Hartola :Hollola :Heinola :Iitti :Kärkölä :Lahti :Myrskylä :Orimattila
                               :Padasjoki :Pukkila :Sysmä}
                :Satakunta #{:Eura :Eurajoki :Harjavalta :Honkajoki :Huittinen :Jämijärvi :Kankaanpää :Karvia :Kokemäki
                             :Merikarvia :Nakkila :Pomarkku :Pori :Rauma :Siikainen :Säkylä :Ulvila}
                :Uusimaa #{:Askola :Espoo :Hanko :Helsinki :Hyvinkää :Inkoo :Järvenpää :Karkkila :Kauniainen :Kerava
                           :Kirkkonummi :Lapinjärvi :Loviisa :Lohja :Mäntsälä :Nurmijärvi :Pornainen :Porvoo :Raasepori
                           :Sipoo :Siuntio :Tuusula :Vantaa :Vihti}
                :Varsinais-Suomi #{:Aura :Kaarina :Koski-Tl :Kustavi :Kemiönsaari :Laitila :Lieto :Loimaa :Parainen :Marttila
                                   :Masku :Mynämäki :Naantali :Nousiainen :Oripää :Paimio :Punkalaidun :Pyhäranta :Pöytyä
                                   :Raisio :Rusko :Salo :Sauvo :Somero :Taivassalo :Turku :Uusikaupunki :Vehmaa}})

(defn district-value->province-name [district-value]
  (cond
    (= district-value "Pohjois-Pohjanmaan SHP") "Pohjois-Pohjanmaa"
    (= district-value "Päijät-Hämeen SHP") "Etelä-Karjala"
    (= district-value "Etelä-Karjalan SHP") "Etelä-Savo"
    (= district-value "Vaasan SHP") "Pohjanmaa"
    (= district-value "Kainuun SHP") "Kainuu"
    (= district-value "Ahvenanmaa") "Ahvenanmaa"
    (= district-value "Etelä-Savon SHP") "Etelä-Savo"
    (= district-value "Satakunnan SHP") "Satakunta"
    (= district-value "Itä-Savon SHP") "Itä-Savo"
    (= district-value "Kymenlaakson SHP") "Kymenlaakso"
    (= district-value "Länsi-Pohjan SHP") "Länsi-Pohja"
    (= district-value "Helsingin ja Uudenmaan SHP") "Uusimaa"
    (= district-value "Varsinais-Suomen SHP") "Varsinais-Suomi"
    (= district-value "Kanta-Hämeen SHP") "Kanta-Häme"
    (= district-value "Kaikki Alueet") "Kaikki"
    (= district-value "Pohjois-Savon SHP") "Pohjois-Savo"
    (= district-value "Lapin SHP") "Lappi"
    (= district-value "Etelä-Pohjanmaan SHP") "Etelä-Pohjanmaa"
    (= district-value "Keski-Pohjanmaan SHP") "Keski-Pohjanmaa"
    (= district-value "Pirkanmaan SHP") "Pirkanmaa"
    (= district-value "Keski-Suomen SHP") "Keski-Suomi"
    (= district-value "Pohjois-Karjalan SHP") "Pohjois-Karjala"
    :else "Failed-Value"))
