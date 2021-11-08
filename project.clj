(defproject tiny-records "1.0.0"
  :description "A command-line utility for processing personnel records"
  :url "https://github.com/mindbat/tiny-records"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [clojure.java-time "0.3.3"]
                 [doric "0.9.0"]
                 [org.clojure/tools.cli "1.0.206"]]
  :main ^:skip-aot tiny-records.core
  :target-path "target"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
