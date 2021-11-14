(ns tiny-records.test.handler
  (:require [clojure.test :refer :all]
            [cheshire.core :as json]
            [clj-http.client :as http]
            [tiny-records.handler :as handler]
            [ring.adapter.jetty :as ring-jetty]))

(defmacro with-server [app options & body]
  `(let [server# (ring-jetty/run-jetty ~app ~(assoc options :join? false))]
     (try
       ~@body
       (finally (.stop server#)))))

(deftest t-get-status
  (with-server handler/app {:port 3000}
    (let [response (http/get "http://localhost:3000/status")]
      (is (= 200 (:status response)))
      (is (= "Server running"
             (:message (json/parse-string (:body response) true)))))))
