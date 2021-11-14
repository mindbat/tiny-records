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

(deftest t-basic-routes
  (testing "should have a status endpoint with json response"
    (with-server handler/app {:port 3000}
      (let [response (http/get "http://localhost:3000/status")]
        (is (= 200 (:status response)))
        (is (= "application/json"
               (get-in response [:headers "Content-Type"])))
        (is (= "Server running"
               (:message (json/parse-string (:body response) true)))))))
  (testing "should 404 for random requests"
    (with-server handler/app {:port 3000}
      (let [response (http/get "http://localhost:3000/pinfeathers"
                               {:throw-exceptions false})]
        (is (= 404 (:status response)))
        (is (= "application/json"
               (get-in response [:headers "Content-Type"])))
        (is (= "Not Found"
               (:message (json/parse-string (:body response) true))))))))
