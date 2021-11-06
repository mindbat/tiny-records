(ns tiny-records.record
  (:require [clojure.string :as str]))

(def record-keys [:last-name :first-name :email :favorite-color :date-of-birth])

(defn parse-delimited-record
  "Given a field delimiter and a record string,
  return a record map."
  [delimiter record-line]
  (zipmap record-keys (str/split record-line delimiter)))

(defn detect-delimiter
  "Discover which field delimiter this record is using."
  [record-line]
  (cond
    (.contains record-line "|") #"\|"
    (.contains record-line ",") #","
    (.contains record-line " ") #"\s+"))

(defn parse-record
  "Convert a single line from a file of records
  into a usable map."
  [record-line]
  (-> record-line
      detect-delimiter
      (parse-delimited-record record-line)))
