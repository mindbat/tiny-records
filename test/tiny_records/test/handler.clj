(ns tiny-records.test.handler
  (:require [clojure.test :refer :all]
            [cheshire.core :as json]
            [clj-http.client :as http]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as ring-jetty]
            [tiny-records.handler :as handler]
            [tiny-records.record :as rec]
            [tiny-records.test.common :as common]))

(use-fixtures :each
  common/reset-records-fixture
  common/with-server-fixture)

(deftest t-basic-routes
  (testing "should have a status endpoint with json response"
    (let [response (http/get "http://localhost:3000/status")]
      (is (= 200 (:status response)))
      (is (= "application/json"
             (get-in response [:headers "Content-Type"])))
      (is (= "Server running"
             (:message (json/parse-string (:body response) true))))))
  (testing "should 404 for random requests"
    (let [response (http/get "http://localhost:3000/pinfeathers"
                             {:throw-exceptions false})]
      (is (= 404 (:status response)))
      (is (= "application/json"
             (get-in response [:headers "Content-Type"])))
      (is (= "Not Found"
             (:message (json/parse-string (:body response) true)))))))

(deftest t-create-record
  (testing "should accept pipe-delimited records"
    (common/reset-records!)
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
             (:last-name (first @rec/current-records))))))
  (testing "should accept comma-delimited records"
    (common/reset-records!)
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
             (:last-name (first @rec/current-records))))))
  (testing "should accept space-delimited records"
    (common/reset-records!)
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
             (:last-name (first @rec/current-records))))))
  (testing "should reject non-json bodies"
    (let [response (http/post "http://localhost:3000/records"
                              {:throw-exceptions false
                               :body "this is not json"})]
      (is (= 400
             (:status response)))
      (is (= "Must send a valid record in the body!"
             (:message (json/parse-string (:body response) true))))))
  (testing "should reject empty body"
    (let [test-body (json/generate-string {:record-line ""})
          response (http/post "http://localhost:3000/records"
                              {:throw-exceptions false
                               :content-type :json
                               :body test-body})]
      (is (= 400
             (:status response)))
      (is (= "Must send a valid record in the body!"
             (:message (json/parse-string (:body response) true))))))
  (testing "should reject malformed records"
    (let [test-record "this-is-a-bad-record"
          test-body (json/generate-string {:record-line test-record})
          response (http/post "http://localhost:3000/records"
                              {:throw-exceptions false
                               :content-type :json
                               :body test-body})]
      (is (= 400
             (:status response)))
      (is (= "Must send a valid record in the body!"
             (:message (json/parse-string (:body response) true)))))))

(deftest t-get-current-records
  ;; load up our current records
  (with-open [rdr (io/reader "test/sample-pipe-delimited.txt")]
    (doseq [line (line-seq rdr)]
      (http/post "http://localhost:3000/records"
                 {:throw-exceptions false
                  :content-type :json
                  :body (json/generate-string {:record-line line})})))
  (testing "should be able to fetch by color"
    (let [response (http/get "http://localhost:3000/records/color")
          response-body (json/parse-string (:body response)
                                           true)]
      (is (= 200
             (:status response)))
      (is (= 6
             (count (:records response-body))))
      (is (= ["mordred" "wizard" "the-owl" "gawain" "of-the-lake" "wart"]
             (map :last-name (:records response-body))))))
  (testing "should be able to fetch by birthdate"
    (let [response (http/get "http://localhost:3000/records/birthdate")
          response-body (json/parse-string (:body response)
                                           true)]
      (is (= 200
             (:status response)))
      (is (= 6
             (count (:records response-body))))
      (is (= ["the-owl" "of-the-lake" "gawain" "wart" "mordred" "wizard"]
             (map :last-name (:records response-body))))))
  (testing "should be able to fetch by last name"
    (let [response (http/get "http://localhost:3000/records/name")
          response-body (json/parse-string (:body response)
                                           true)]
      (is (= 200
             (:status response)))
      (is (= 6
             (count (:records response-body))))
      (is (= ["gawain" "mordred" "of-the-lake" "the-owl" "wart" "wizard"]
             (map :last-name (:records response-body)))))))
