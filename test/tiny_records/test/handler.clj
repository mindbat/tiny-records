(ns tiny-records.test.handler
  (:require [clojure.test :refer :all]
            [cheshire.core :as json]
            [clj-http.client :as http]
            [tiny-records.handler :as handler]
            [tiny-records.record :as rec]
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

(deftest t-create-record
  (testing "should accept pipe-delimited records"
    (with-server handler/app {:port 3000}
      (let [test-record "the-owl|archimedes|wise@owl.com|brown|287-04-06"
            test-body (json/generate-string {:record-line test-record})
            response (http/post "http://localhost:3000/records"
                                {:content-type :json
                                 :body test-body})
            response-body (json/parse-string (:body response)
                                             true)]
        (is (= 201
               (:status response)))
        (is (= "the-owl"
               (:last-name response-body)))
        (is (= 1 (count @rec/current-records)))
        (is (= "the-owl"
               (:last-name (first @rec/current-records)))))))
  (testing "should accept comma-delimited records"
    (with-server handler/app {:port 3000}
      (let [test-record "the-owl,archimedes,wise@owl.com,brown,287-04-06"
            test-body (json/generate-string {:record-line test-record})
            response (http/post "http://localhost:3000/records"
                                {:content-type :json
                                 :body test-body})
            response-body (json/parse-string (:body response)
                                             true)]
        (is (= 201
               (:status response)))
        (is (= "the-owl"
               (:last-name response-body)))
        (is (= 1 (count @rec/current-records)))
        (is (= "the-owl"
               (:last-name (first @rec/current-records)))))))
  (testing "should accept space-delimited records"
    (with-server handler/app {:port 3000}
      (let [test-record "the-owl archimedes wise@owl.com brown 287-04-06"
            test-body (json/generate-string {:record-line test-record})
            response (http/post "http://localhost:3000/records"
                                {:content-type :json
                                 :body test-body})
            response-body (json/parse-string (:body response)
                                             true)]
        (is (= 201
               (:status response)))
        (is (= "the-owl"
               (:last-name response-body)))
        (is (= 1 (count @rec/current-records)))
        (is (= "the-owl"
               (:last-name (first @rec/current-records)))))))
  (testing "should reject non-json bodies"
    (with-server handler/app {:port 3000}
      (let [response (http/post "http://localhost:3000/records"
                                {:throw-exceptions false
                                 :body "this is not json"})]
        (is (= 400
               (:status response)))
        (is (= "Must send a valid record in the body!"
               (:message (json/parse-string (:body response) true)))))))
  (testing "should reject empty body"
    (with-server handler/app {:port 3000}
      (let [test-body (json/generate-string {:record-line ""})
            response (http/post "http://localhost:3000/records"
                                {:throw-exceptions false
                                 :content-type :json
                                 :body test-body})]
        (is (= 400
               (:status response)))
        (is (= "Must send a valid record in the body!"
               (:message (json/parse-string (:body response) true)))))))
  (testing "should reject malformed records"
    (with-server handler/app {:port 3000}
      (let [test-record "this-is-a-bad-record"
            test-body (json/generate-string {:record-line test-record})
            response (http/post "http://localhost:3000/records"
                                {:throw-exceptions false
                                 :content-type :json
                                 :body test-body})]
        (is (= 400
               (:status response)))
        (is (= "Must send a valid record in the body!"
               (:message (json/parse-string (:body response) true))))))))
