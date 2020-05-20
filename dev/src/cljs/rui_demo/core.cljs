(ns rui-demo.core
  (:require
   [goog.dom :as dom]
   [re-frame.core :refer [dispatch dispatch-sync clear-subscription-cache! subscribe reg-sub]]
   [reagent.core :as reagent]
   [reagent.dom :as rdom]
   [rui-demo.buttons :refer [buttons-demo]]
   [rui-demo.flash :refer [flash-demo]]
   [rui-demo.forms :refer [forms-demo]]
   [rui-demo.icons :refer [icons-demo]]
   [rui-demo.modals :refer [modals-demo]]))

(reg-sub
  :app
  identity)


(defn- main
  []
  (let [db-ref (subscribe [:app])]
    (fn []
      [:div.container
       (let [db @db-ref]
         (for [[headline component] [["Flash" flash-demo]
                                     ["Icons" icons-demo]
                                     ["Buttons" buttons-demo]
                                     ["Forms" forms-demo]
                                     ["Modal" modals-demo]]]
           ^{:key headline}
           [:div.card.mb-3
            [:div.card-header
             [:h2 headline]]
            [component db]]))])))


(defn- mount-root!
  [app-element]
  (rdom/render [main] app-element))


(defn ^:export run
  []
  (let [app-element (dom/getElement "app")]
    (when (nil? app-element)
      (js/error "Missing 'app' element for mounting!"))
    (clear-subscription-cache!)
    (mount-root! app-element)))
