(defproject centauri "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [net.mikera/core.matrix "0.60.3"]
                 [net.mikera/vectorz-clj "0.47.0"]
                 [org.clojure/tools.macro "0.1.2"]]
  :plugins [[lein-kibit "0.1.5"]
            [refactor-nrepl "2.3.1"]]
  :main ^:skip-aot centauri.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
