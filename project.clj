(defproject tiny-records "1.0.0"
  :description "A command-line utility for processing personnel records"
  :url "https://github.com/mindbat/tiny-records"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [clojure.java-time "0.3.3"]
                 [compojure "1.6.1"]
                 [doric "0.9.0"]
                 [org.clojure/tools.cli "1.0.206"]
                 [ring/ring-core "1.9.4"]
                 [ring/ring-devel "1.9.4"]
                 [ring/ring-jetty-adapter "1.9.4"]]
  :main ^:skip-aot tiny-records.core
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler tiny-records.handler/app}
  :target-path "target"
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring/ring-mock "0.3.2"]]}
             :uberjar {:aot :all
                       :main tiny-records.core
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
