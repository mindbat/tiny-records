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

(deftest t-parse-file
  (testing "should be able to parse pipe-delimited files"
    (let [new-records (rec/parse-file "test/sample-pipe-delimited.txt")]
      (is (= 5 (count new-records)))
      (is (every? #(= (set rec/record-keys)
                      (set (keys %)))
                  new-records))
      (is #{"wart" "owl" "mordred" "gawain" "wizard"}
          (set (map :last-name new-records)))))
  (testing "should be able to parse comma-delimited files"
    (let [new-records (rec/parse-file "test/sample-comma-delimited.txt")]
      (is (= 5 (count new-records)))
      (is (every? #(= (set rec/record-keys)
                      (set (keys %)))
                  new-records))
      (is #{"wart" "owl" "mordred" "gawain" "wizard"}
          (set (map :last-name new-records)))))
  (testing "should be able to parse space-delimited files"
    (let [new-records (rec/parse-file "test/sample-space-delimited.txt")]
      (is (= 5 (count new-records)))
      (is (every? #(= (set rec/record-keys)
                      (set (keys %)))
                  new-records))
      (is #{"wart" "owl" "mordred" "gawain" "wizard"}
          (set (map :last-name new-records))))))

(deftest t-add-to-current-records
  (testing "should add file to current records"
    (let [file-to-parse "test/sample-pipe-delimited.txt"]
      (rec/add-to-current-records! file-to-parse)
      (is (= 5 (count @rec/current-records)))
      (is (every? #(= (set rec/record-keys)
                      (set (keys %)))
                  @rec/current-records))
      (is #{"wart" "owl" "mordred" "gawain" "wizard"}
          (set (map :last-name @rec/current-records)))))
  (testing "duplicate records should not alter the list"
    (let [file-to-parse "test/sample-comma-delimited.txt"]
      (rec/add-to-current-records! file-to-parse)
      (is (= 5 (count @rec/current-records))))))
