(ns covid-tracker-clj.routes.files
    (:require [covid-tracker-clj.schemas.files :refer [upload-request upload-response]]
              [covid-tracker-clj.services.file :refer [get-picture]]))

(defn file-routes []
   ;; swagger documentation
  ["/files"
   {:swagger {:tags ["files"]}}
   ["/upload"
    {:post {:summary "upload a file"
            :parameters {:multipart upload-request}
            :responses {200 {:body upload-response}}
            :handler (fn [{{{:keys [file]} :multipart} :parameters}]
                       {:status 200
                        :body {:name (:filename file)
                               :size (:size file)}})}}]
   ["/download"
    {:get {:summary "downloads a file"
           :swagger {:produces ["image/png"]}
           :handler (fn [_]
                      {:status 200
                       :headers {"Content-Type" "image/png"}
                       :body (get-picture)})}}]])
