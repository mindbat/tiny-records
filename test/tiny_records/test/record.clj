(ns tiny_records.test.record
  (:require [clojure.test :refer :all]
            [tiny-records.record :as rec]))

(deftest t-parse-record
  (testing "should parse pipe-delimited records"
    (is (= {:last-name "owl"
            :first-name "archimedes"
            :email "wise@owl.com"
            :favorite-color "brown"
            :date-of-birth "pinfeathers-and-gollyfluff"}
           (rec/parse-record "owl|archimedes|wise@owl.com|brown|pinfeathers-and-gollyfluff"))))
  (testing "should parse comma-delimited records"
    (is (= {:last-name "wart"
            :first-name "arthur"
            :email "king@england.co.uk"
            :favorite-color "red"
            :date-of-birth "very-long-ago"}
           (rec/parse-record "wart,arthur,king@england.co.uk,red,very-long-ago"))))
  (testing "should parse space-delimited records"
    (is (= {:last-name "wizard"
            :first-name "merlin"
            :email "bermuda@backwards.com"
            :favorite-color "blue"
            :date-of-birth "start-of-time"}
         (rec/parse-record "wizard merlin bermuda@backwards.com blue start-of-time")))))
