(ns tiny-records.core
  (:require [clojure.java.io :as io]
            [doric.core :as doric]
            [java-time :as date]
            [tiny-records.record :as rec])
  (:gen-class))

(defn get-file-list
  "Given a directory path, return a list of all .txt files in it."
  [dir-path]
  (.listFiles (io/file dir-path)
              (proxy [java.io.FilenameFilter] []
                (accept [f, s] (.endsWith s ".txt")))))

(defn process-directory!
  "Given a path to a directory, parse every txt file found
  in it into records."
  [dir-path]
  ;; if the directory is fake, we have nothing to do
  (assert (.exists (io/file dir-path))
          "Directory does not exist!")
  (let [file-list (get-file-list dir-path)]
    (doseq [f file-list]
      (rec/add-to-current-records! f))))

(defn process-file!
  "Given a path to a file, parse it into records."
  [file-path]
  (assert (.exists (io/file file-path))
          "File does not exist!")
  (assert (.endsWith file-path ".txt")
            "File is wrong format!")
  (rec/add-to-current-records! file-path))

(defn format-date-for-output
  "Format the date of a record for output to a user."
  [record]
  (update record
          :date-of-birth
          (partial date/format "M/d/YYYY")))

(defn print-current-records
  [requested-view]
  (assert (get rec/views->sorts requested-view false)
   "Requested view does not exist!")
  (println (doric/table rec/record-keys
                        (map format-date-for-output
                             (rec/get-sorted-records requested-view)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
