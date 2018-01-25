(ns rui.flash.components
  (:require
    [goog.string :refer [unescapeEntities]]
    [ccn.core :refer [css-class]]
    [re-frame.core :refer [dispatch]]
    [reagent.core :as reagent]
    [rui.alerts.components :refer [alert]]
    [rui.flash.core :refer [->Flash]]))


(defn- close
  [dispatcher]
  [:button {:type "button", :class "close", :aria-label "Close", :on-click dispatcher}
   [:span {:aria-hidden "true"} (unescapeEntities "&times;")]])


(defn- flash-message
  [message]
  (let [dispatcher #(dispatch [:rui::flash/dismiss-message (:id message)])]
    (reagent/create-class
      {:display-name
       "flash-message"

       :component-did-mount
       (fn [_]
         (when (and (:dismissible? message)
                    (pos? (:timeout message)))
           (js/setTimeout dispatcher (:timeout message))))

       :reagent-render
       (fn [message]
         [alert [(-> message :severity name) (when (:dismissible? message) "dismissible")]
          (when (:dismissible? message)
            [close dispatcher])
          [:span.flash-message__text (:text message)]])})))


(defn flash
  "Renders a list of given `messages` as Bootstrap's alerts"
  [messages]
  [:div.flash
   (for [message messages]
     ^{:key (:id message)}
     [flash-message message])])


(defn offline-panel
  "Returns offline-panel component with background overlay and flash message that a user is offline.
   Kwargs:
   :modifiers - BEM modifiers
   :messages - a map for translations"
  [& {:keys [modifiers messages]
      :or {modifiers []
           messages {:you-are-offline "You are offline"}}}]
  [:div.offline-panel
   [alert (->Flash :danger (:you-are-offline messages) 0 :offline false)]
   [:div.offline-panel__overlay]])
