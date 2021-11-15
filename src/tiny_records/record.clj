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
    (.contains record-line " ") #"\s+"
    :default nil))

(defn parse-record
  "Convert a single line from a file of records
  into a usable map."
  [record-line]
  (-> record-line
      detect-delimiter
      (parse-delimited-record record-line)))

(defn valid-record?
  [possible-record]
  (and (seq possible-record)
       (not (nil? (detect-delimiter possible-record)))))

(defn add-to-current-records!
  [record-line]
  (let [new-record (parse-record record-line)]
    (swap! current-records conj new-record)
    new-record))

(defn parse-file
  "Given a file-path, read its contents, parse them into
  records, and return the list."
  [record-file]
  (with-open [rdr (io/reader record-file)]
    (doall (for [line (line-seq rdr)]
             (parse-record line)))))

(defn add-file-to-current-records!
  "Add the contents of a file at the given file-path to
  the in-memory record list."
  [record-file]
  (swap! current-records set/union (set (parse-file record-file))))

(defn format-for-output
  "Format a record for output to the user.
  Atm, this means formatting the date field."
  [record]
  (update record
          :date-of-birth
          (partial date/format "M/d/YYYY")))

(defn flip
  "Reverses a comparator. Useful for getting desc sort orders."
  [result]
  (* -1 result))

(defn color
  "Comparator fn for sorting records by color (asc)."
  [record-1 record-2]
  (compare (:favorite-color record-1)
           (:favorite-color record-2)))

(defn last-name
  "Comparator fn for sorting records by name desc."
  [record-1 record-2]
  (flip (compare (:last-name record-1)
                 (:last-name record-2))))

(defn color-then-last-name
  "Comparator fn for sorting records by
  color (asc) and last-name (asc)."
  [record-1 record-2]
  (if (= (:favorite-color record-1)
         (:favorite-color record-2))
    (flip (last-name (:last-name record-1)
                     (:last-name record-2)))
    (color record-1 record-2)))

(defn birth-date
  "Comparator fn for sorting records by dob asc."
  [record-1 record-2]
  (compare (:date-of-birth record-1)
           (:date-of-birth record-2)))

(defn sort-records
  [sort-fn]
  (sort sort-fn @current-records))

(def views->sorts
  {:view1 color-then-last-name
   :view2 birth-date
   :view3 last-name})

(defn valid-view?
  [view-type]
  (boolean (get views->sorts view-type false)))

(defn get-sorted-records
  "Fetch a sorted list of current-records according to
  which view was requested."
  [view-type]
  (map format-for-output
       (sort-records (view-type views->sorts))))
