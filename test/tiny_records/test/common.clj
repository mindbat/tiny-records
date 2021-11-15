(ns tiny-records.test.common
  (:require [tiny-records.handler :as handler]
            [tiny-records.record :as rec]
            [ring.adapter.jetty :as ring-jetty]))

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

(defmacro with-server [app options & body]
  `(let [server# (ring-jetty/run-jetty ~app ~(assoc options :join? false))]
     (try
       ~@body
       (finally (.stop server#)))))

(defn with-server-fixture
  [f]
  (when f
    (with-server handler/app {:port 3000}
      (f))))
