(ns rui-demo.dev
  (:require
    [figwheel.client :as fw]
    [re-frisk.core :refer [enable-re-frisk!]]
    [rui-demo.core :as core]))

(enable-console-print!)
(enable-re-frisk!)

(fw/start {:on-jsload core/run
           :websocket-url "ws://localhost:3449/figwheel-ws"})
