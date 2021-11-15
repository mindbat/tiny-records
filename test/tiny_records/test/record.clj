(ns tiny_records.test.record
  (:require [clojure.test :refer :all]
            [java-time :as date]
            [tiny-records.record :as rec]))

(use-fixtures :each
  (fn [f]
    (reset! rec/current-records #{})
    (when f
      (f))))

(deftest t-parse-record
  (testing "should parse pipe-delimited records"
    (is (= {:last-name "owl"
            :first-name "archimedes"
            :email "wise@owl.com"
            :favorite-color "brown"
            :date-of-birth (date/local-date 1972 03 26)}
           (rec/parse-record "owl|archimedes|wise@owl.com|brown|1972-03-26"))))
  (testing "should parse comma-delimited records"
    (is (= {:last-name "wart"
            :first-name "arthur"
            :email "king@england.co.uk"
            :favorite-color "red"
            :date-of-birth (date/local-date 1975 06 23)}
           (rec/parse-record "wart,arthur,king@england.co.uk,red,1975-06-23"))))
  (testing "should parse space-delimited records"
    (is (= {:last-name "wizard"
            :first-name "merlin"
            :email "bermuda@backwards.com"
            :favorite-color "blue"
            :date-of-birth (date/local-date 2246 02 25)}
         (rec/parse-record "wizard merlin bermuda@backwards.com blue 2246-02-25")))))

(deftest t-parse-file
  (testing "should be able to parse pipe-delimited files"
    (let [new-records (rec/parse-file "test/sample-pipe-delimited.txt")]
      (is (= 6 (count new-records)))
      (is (every? #(= (set rec/record-keys)
                      (set (keys %)))
                  new-records))
      (is (= #{"wart" "the-owl" "of-the-lake" "mordred" "gawain" "wizard"}
             (set (map :last-name new-records))))))
  (testing "should be able to parse comma-delimited files"
    (let [new-records (rec/parse-file "test/sample-comma-delimited.txt")]
      (is (= 6 (count new-records)))
      (is (every? #(= (set rec/record-keys)
                      (set (keys %)))
                  new-records))
      (is (= #{"wart" "the-owl" "of-the-lake" "mordred" "gawain" "wizard"}
             (set (map :last-name new-records))))))
  (testing "should be able to parse space-delimited files"
    (let [new-records (rec/parse-file "test/sample-space-delimited.txt")]
      (is (= 6 (count new-records)))
      (is (every? #(= (set rec/record-keys)
                      (set (keys %)))
                  new-records))
      (is (= #{"wart" "the-owl" "of-the-lake" "mordred" "gawain" "wizard"}
             (set (map :last-name new-records)))))))

(deftest t-add-file-to-current-records
  (testing "should add file to current records"
    (let [file-to-parse "test/sample-pipe-delimited.txt"]
      (rec/add-file-to-current-records! file-to-parse)
      (is (= 6 (count @rec/current-records)))
      (is (every? #(= (set rec/record-keys)
                      (set (keys %)))
                  @rec/current-records))
      (is (= #{"wart" "the-owl" "of-the-lake" "mordred" "gawain" "wizard"}
             (set (map :last-name @rec/current-records))))))
  (testing "duplicate records should not alter the list"
    (let [file-to-parse "test/sample-comma-delimited.txt"]
      (rec/add-file-to-current-records! file-to-parse)
      (is (= 6 (count @rec/current-records))))))

(deftest t-get-sorted-records
  (testing "view1 means sort by color and last name"
    (let [file-to-parse "test/sample-pipe-delimited.txt"]
      (rec/add-file-to-current-records! file-to-parse)
      (is (= 6 (count @rec/current-records)))
      (let [sorted-records (rec/get-sorted-records-by-view :view1)]
        (is (= 6 (count sorted-records)))
        (is (= ["mordred" "wizard" "the-owl" "gawain" "of-the-lake" "wart"]
               (map :last-name sorted-records))))))
  (testing "view2 means sort by birth date only"
    (let [file-to-parse "test/sample-pipe-delimited.txt"]
      (rec/add-file-to-current-records! file-to-parse)
      (is (= 6 (count @rec/current-records)))
      (let [sorted-records (rec/get-sorted-records-by-view :view2)]
        (is (= 6 (count sorted-records)))
        (is (= ["the-owl" "of-the-lake" "gawain" "wart" "mordred" "wizard"]
               (map :last-name sorted-records))))))
  (testing "view3 means sort by last-name"
    (let [file-to-parse "test/sample-pipe-delimited.txt"]
      (rec/add-file-to-current-records! file-to-parse)
      (is (= 6 (count @rec/current-records)))
      (let [sorted-records (rec/get-sorted-records-by-view :view3)]
        (is (= 6 (count sorted-records)))
        (is (= ["wizard" "wart" "the-owl" "of-the-lake" "mordred" "gawain"]
               (map :last-name sorted-records)))))))
