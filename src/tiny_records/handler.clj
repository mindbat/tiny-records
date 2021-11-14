(ns tiny-records.handler
  (:require [cheshire.core :as json]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as resp]))

(defn get-status
  [req]
  (resp/response {:message "Server running"}))

(defn wrap-json
  [handler]
  (fn [request]
    (let [response (handler request)]
      (-> response
          (assoc-in [:headers "Content-Type"] "application/json")
          (update :body json/generate-string)))))

(defroutes app-routes
  (GET "/status" [] get-status)
  (route/not-found {:message "Not Found"}))

(def app
  (-> app-routes
      wrap-json))
