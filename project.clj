(defproject druids/rui "0.14.0"
  :description "UI components based on Reagent and Re-frame"
  :url "https://github.com/druids/rui"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}

  :dependencies [[ccn "0.1.1"]]

  :source-paths ["src/cljs"]

  :profiles {:dev {:plugins [[lein-kibit "0.1.6"]
                             [jonase/eastwood "0.2.8"]
                             [lein-cljsbuild "1.1.7"]
                             [lein-figwheel "0.5.16"]
                             [lein-doo "0.1.10"]]
                   :dependencies [[org.clojure/clojure "1.9.0"]
                                  [org.clojure/clojurescript "1.10.339"]
                                  [binaryage/devtools "0.9.10"]
                                  [reagent "0.10.0"]
                                  [re-frame "0.12.0"]
                                  [re-frisk "0.5.4"]]}}

  :cljsbuild
  {:builds
   [{:id "dev"
     :source-paths ["src/cljs" "dev/src/cljs"]
     :compiler {:main rui-demo.dev
                :output-to "resources/rui-demo/public/js/compiled/demo/demo.js"
                :output-dir "resources/rui-demo/public/js/compiled/demo/out"
                :asset-path "js/compiled/demo/out"
                :source-map-timestamp true
                :preloads [devtools.preload]
                :external-config {:devtools/config {:features-to-install :all}}}}

    {:id "min"
     :source-paths ["src/cljs" "dev/src/cljs"]
     :compiler {:main rui-demo.core
                :output-to "resources/rui-demo/public/js/compiled/demo-min/demo.js"
                :output-dir "resources/rui-demo/public/js/compiled/demo-min/out"
                :asset-path "js/compiled/demo-min/out"
                :optimizations :advanced
                :closure-defines {goog.DEBUG false}
                :pretty-print false
                :parallel-build true
                :closure-warnings {:non-standard-jsdoc :off
                                   :externs-validation :off}}
     :warning-handlers
     [(fn [warning-type env extra]
        (when (warning-type cljs.analyzer/*cljs-warnings*)
          (when-let [s (cljs.analyzer/error-message warning-type extra)]
            (binding [*out* *err*]
              (println "WARNING:" (cljs.analyzer/message env s)))
            (System/exit 1))))]}


    {:id "test"
     :source-paths ["src/cljs/rui" "dev/src/rui_demo" "test/cljs"]
     :compiler {:main rui.runner
                :output-to "resources/rui-demo/public/js/compiled/test.js"
                :output-dir "resources/rui-demo/public/js/compiled/test/out"
                :optimizations :none}}]}
                ;workaround for running lein doo with latest CLJS, see https://github.com/bensu/doo/pull/141
                ;:process-shim false}}]}

  :aliases {"dev" ["do" "clean," "figwheel" "dev"]
            "dev-test" ["do" "clean," "doo" "phantom" "test" "auto"]
            "test" ["do" "clean," "doo" "phantom" "test" "once"]})
