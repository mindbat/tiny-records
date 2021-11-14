(ns tiny-records.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.cli :as cli]
            [doric.core :as doric]
            [java-time :as date]
            [ring.adapter.jetty :as ring-jetty]
            [tiny-records.handler :as handler]
            [tiny-records.record :as rec])
  (:gen-class))

(defn get-file-list
  "Given a directory path, return a list of all .txt files in it."
  [dir-path]
  (.listFiles (io/file dir-path)
              (proxy [java.io.FilenameFilter] []
                (accept [f, s] (.endsWith s ".txt")))))

(defn valid-file?
  [file-path]
  (let [possible-file (io/file file-path)]
    (and (.exists possible-file)
         (.endsWith file-path ".txt"))))

(defn valid-directory?
  [dir-path]
  (.exists (io/file dir-path)))

(defn valid-file-or-directory?
  [path]
  (or (valid-file? path)
      (valid-directory? path)))

(defn process-directory!
  "Given a path to a directory, parse every txt file found
  in it into records."
  [dir-path]
  ;; if the directory is fake, we have nothing to do
  (assert (.exists (io/file dir-path))
          "Directory does not exist!")
  (let [file-list (get-file-list dir-path)]
    (doseq [f file-list]
      (rec/add-file-to-current-records! f))))

(defn process-file!
  "Given a path to a file, parse it into records."
  [file-path]
  (assert (.exists (io/file file-path))
          "File does not exist!")
  (assert (.endsWith file-path ".txt")
            "File is wrong format!")
  (rec/add-file-to-current-records! file-path))

(defn print-current-records
  [requested-view]
  (assert (rec/valid-view? requested-view)
   "Requested view does not exist!")
  (println (doric/table rec/record-keys
                        (map rec/format-date-for-output
                             (rec/get-sorted-records requested-view)))))

(def cli-options
  [["-h" "--help"]
   ["-d" "--data PATH" "Path to directory or single file where the input data lives. (cli mode only)"
    :validate [valid-file-or-directory?
               "Must point to existing directory or .txt file!"]]
   ["-o" "--output VIEW" "Which view to use for the data output: view1, view2, or view3? (cli mode only)"
    :parse-fn keyword
    :validate [rec/valid-view? "Must be one of: view1, view2, or view3"]]
   ["-p" "--port PORT" "Port on which to start the web server (web mode only)"
    :parse-fn #(Integer/parseInt %)
    :default 3000]])

(defn print-help
  [summary]
  (let [extra-help ["Welcome to Tiny Records!"
                    "This application can be started in either web-server or cli mode"
                    "To access web-server mode, run it with 'web' as the first argument:\n    ./tiny-records web --port [your-port-of-choice]"
                    "Full list of options below:"]]
    (println (str/join "\n\n" extra-help)))
  (println summary)
  (System/exit 0))

(defn print-errors
  [errors]
  (println "Some of the provided arguments had errors:")
  (println (str/join "\n" errors))
  (System/exit 1))

(defn process-and-output
  [data-path requested-view]
  (if (.isFile (io/file data-path))
    (process-file! data-path)
    (process-directory! data-path))
  (print-current-records requested-view)
  (System/exit 0))

(defn -main
  "Parse and validate the args, then kick off either the web server
  or the cli data processing."
  [& args]
  (let [{:keys [options arguments errors summary]}
        (cli/parse-opts args cli-options)]
    (cond
      (:help options) (print-help summary)
      (< 0 (count errors)) (print-errors errors)
      (= "web" (first arguments)) (ring-jetty/run-jetty
                                   handler/app
                                   {:port (:port options)})
      :default (process-and-output (:data options) (:output options)))))
