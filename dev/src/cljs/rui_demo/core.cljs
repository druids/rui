(ns rui-demo.core
  (:require
    [goog.dom :as dom]
    [reagent.core :as reagent]
    [re-frame.core :refer [dispatch dispatch-sync clear-subscription-cache! subscribe reg-sub]]
    [rui-demo.flash :refer [flash-demo]]))


(reg-sub
  :app
  identity)


(defn- main
  []
  (let [db (subscribe [:app])]
    (fn []
      [:div
       [flash-demo @db]])))


(defn- mount-root!
  [app-element]
  (reagent/render [main] app-element))


(defn ^:export run
  []
  (let [app-element (dom/getElement "app")]
    (when (nil? app-element)
      (js/error "Missing 'app' element for mounting!"))
    (clear-subscription-cache!)
    (mount-root! app-element)))
