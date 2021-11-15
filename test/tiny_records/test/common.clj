(ns tiny-records.test.common
  (:require [tiny-records.record :as rec]))

(defn reset-records!
  "Reset the current records list."
  []
  (reset! rec/current-records #{}))

(defn reset-records-fixture
  "Test fixture for resetting the current records."
  [f]
  (reset-records!)
  (when f
    (f)))
