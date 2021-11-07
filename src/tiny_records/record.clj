(ns tiny-records.record
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]
            [java-time :as date]))

(def record-keys [:last-name :first-name :email
                  :favorite-color :date-of-birth])

(def current-records (atom #{}))

(defn convert-incoming-to-date
  "Function for converting the date-of-birth for a new record
  into a local-date we can use later."
  [incoming]
  (apply date/local-date
         (map #(Integer/parseInt %1)
              (str/split incoming #"-"))))

(defn normalize-record
  "Normalize the format of the input record so we know
  what's in the current-records atom."
  [new-record]
  (reduce-kv (fn [acc k v]
               (if (= k :date-of-birth)
                 (assoc acc k (convert-incoming-to-date v))
                 (assoc acc k (str/lower-case v))))
             {}
             new-record))

(defn parse-delimited-record
  "Given a field delimiter and a record string,
  return a record map."
  [delimiter record-line]
  (->> delimiter
       (str/split record-line)
       (zipmap record-keys)
       normalize-record))

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

(defn parse-file
  "Given a file-path, read its contents, parse them into
  records, and return the list."
  [record-file]
  (with-open [rdr (io/reader record-file)]
    (doall (for [line (line-seq rdr)]
             (parse-record line)))))

(defn add-to-current-records!
  "Add the contents of a file at the given file-path to
  the in-memory record list."
  [record-file]
  (swap! current-records set/union (set (parse-file record-file))))
