(ns tiny-records.handler
  (:require [cheshire.core :as json]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as resp]))

(defn get-status
  "Handler for checking status of the server."
  [req]
  (resp/response {:message "Server running"}))

(defn wrap-json-body
  "Middleware to coerce responses to json."
  [handler]
  (fn [request]
    (let [response (handler request)]
      (-> response
          (assoc-in [:headers "Content-Type"] "application/json")
          (update :body json/generate-string)))))

(defroutes app-routes
  "Defines all the routes for the rest api."
  (GET "/status" [] get-status)
  (route/not-found (resp/not-found {:message "Not Found"})))

(def app
  "This is the entrypoint for the web server."
  (-> app-routes
      wrap-json-body))
