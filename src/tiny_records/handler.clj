(ns tiny-records.handler
  (:require [cheshire.core :as json]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.request :as req]
            [ring.util.response :as resp]
            [tiny-records.record :as rec]))

(defn is-json-request?
  [request]
  (= "application/json"
     (req/content-type request)))

(defn wrap-json-params
  "Middleware to parse json-body requests into :params key."
  [handler]
  (fn [request]
    (if (is-json-request? request)
      (let [json-params (json/parse-string (req/body-string request)
                                           true)]
        (handler (assoc request :json-params json-params)))
      (handler request))))

(defn wrap-json-body
  "Middleware to coerce responses to json."
  [handler]
  (fn [request]
    (let [response (handler request)]
      (-> response
          (assoc-in [:headers "Content-Type"] "application/json")
          (update :body json/generate-string)))))

(defn get-status
  "Handler for checking status of the server."
  [request]
  (resp/response {:message "Server running"}))

(defn create-record
  "Handler for creating a new record based on a parsed request."
  [record-line]
  (if (rec/valid-record? record-line)
    (let [new-record (rec/add-to-current-records! record-line)]
      (resp/created "" (rec/format-for-output new-record)))
    (resp/bad-request {:message "Must send a valid record in the body!"})))

(defn get-current-records
  "Get a list of the current records sorted by a single field."
  [sorting-field]
  (resp/response {:records (rec/get-sorted-records-by-field
                            sorting-field)}))

(defroutes app-routes
  "Defines all the routes for the rest api."
  (GET "/records/color" [] (get-current-records :favorite-color))
  (GET "/records/birthdate" [] (get-current-records :date-of-birth))
  (GET "/records/name" [] (get-current-records :last-name))
  (POST "/records" {{:keys [record-line]} :json-params}
        (create-record record-line))
  (GET "/status" [] get-status)
  (route/not-found (resp/not-found {:message "Not Found"})))

(def app
  "This is the entrypoint for the web server."
  (-> app-routes
      wrap-json-params
      wrap-json-body))
