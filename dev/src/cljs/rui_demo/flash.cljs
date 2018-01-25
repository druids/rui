(ns rui-demo.flash
  (:require
    [re-frame.core :refer [reg-event-db dispatch]]
    [rui.flash.core :refer [add-message db->messages]]
    [rui.flash.components :refer [flash]]))


(reg-event-db
  :flash/update-message-text
  (fn [db [_ text]]
    (assoc-in db [:flash :message-text] text)))


(reg-event-db
  :flash/add
  (fn [db _]
    (-> db
        (add-message :info (get-in db [:flash :message-text]))
        (assoc-in [:flash :message-text] nil))))


(defn- update-message-text!
  [e]
  (dispatch [:flash/update-message-text (-> e .-target .-value)]))


(defn- add-message!
  [e]
  (dispatch [:flash/add]))


(defn flash-demo
  [db]
  [:div.flash-demo.card-body
   [:input {:on-change update-message-text!, :value (-> db :flash :message-text)}]
   [:button {:on-click add-message!} "Add message"]
   [flash (db->messages db)]])
