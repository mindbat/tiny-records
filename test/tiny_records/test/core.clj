(ns tiny-records.test.core
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [tiny-records.core :as core]
            [tiny-records.record :as rec]
            [tiny-records.test.common :as common]))

(use-fixtures :each
  common/reset-records-fixture)

(deftest t-process-directory
  (testing "should process all txt files"
    (let [original-add rec/add-file-to-current-records!
          times-called (atom 0)]
      (with-redefs [rec/add-file-to-current-records! (fn [arg]
                                                       (swap! times-called inc)
                                                       (original-add arg))]
        ;; spit a non-text file in there for checking
        (spit "test/nope.md" "nope|nope|nope|nope|nope")
        (is (.exists (io/file "test/nope.md")))
        (core/process-directory! "test/")
        (is (= 6 (count @rec/current-records)))
        (is (every? #(= (set rec/record-keys)
                        (set (keys %)))
                    @rec/current-records))
        (is (= #{"wart" "the-owl" "of-the-lake" "mordred" "gawain" "wizard"}
               (set (map :last-name @rec/current-records))))
        (is (= 3 @times-called))
        (io/delete-file "test/nope.md"))))
  (testing "nonexistent directory should throw informative exception"
    (is (thrown-with-msg? AssertionError #"Directory does not exist!"
                          (core/process-directory! "missing/")))))

(deftest t-process-file!
  (testing "happy path - should process found file"
    (core/process-file! "test/sample-comma-delimited.txt")
    (is (= 6 (count @rec/current-records)))
    (is (every? #(= (set rec/record-keys)
                    (set (keys %)))
                @rec/current-records))
    (is (= #{"wart" "the-owl" "of-the-lake" "mordred" "gawain" "wizard"}
           (set (map :last-name @rec/current-records)))))
  (testing "should throw useful exception for missing file"
    (is (thrown-with-msg? AssertionError #"File does not exist!"
                          (core/process-file! "test/missing.txt"))))
  (testing "should throw useful exception for non-txt file"
    (spit "test/nope.md" "nope|nope|nope|nope|nope")
    (is (thrown-with-msg? AssertionError #"File is wrong format!"
                          (core/process-file! "test/nope.md")))
    (io/delete-file "test/nope.md")))

(deftest t-print-current-records
  (testing "should validate requested view"
    (is (thrown-with-msg? AssertionError #"Requested view does not exist!"
                          (core/print-current-records :nope-view))))
  (testing "check view1 printing"
    (core/process-file! "test/sample-pipe-delimited.txt")
    (let [printing (with-out-str (core/print-current-records :view1))
          first-entry (nth (str/split printing #"\n") 3)]
      (is (.contains first-entry "mordred")
          "view1 prints in wrong order")
      (is (.contains first-entry "12/31/0628")
          "view1 prints dates in the wrong format")))
  (testing "check view2 printing"
    (let [printing (with-out-str (core/print-current-records :view2))
          first-entry (nth (str/split printing #"\n") 3)]
      (is (.contains first-entry "the-owl")
          "view2 prints in wrong order")
      (is (.contains first-entry "4/6/0287")
          "view2 prints dates in the wrong format")))
  (testing "check view3 printing"
    (let [printing (with-out-str (core/print-current-records :view3))
          first-entry (nth (str/split printing #"\n") 3)]
      (is (.contains first-entry "wizard")
          "view3 prints in wrong order")
      (is (.contains first-entry "1/1/2467")
          "view3 prints dates in the wrong format"))))
