(ns tiny-records.test.core
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [tiny-records.core :as core]
            [tiny-records.record :as rec]))

(use-fixtures :each
  (fn [f]
    (reset! rec/current-records #{})
    (when f
      (f))))

(deftest t-process-directory
  (testing "should process all txt files"
    (let [original-add rec/add-to-current-records!
          times-called (atom 0)]
      (with-redefs [rec/add-to-current-records! (fn [arg]
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
