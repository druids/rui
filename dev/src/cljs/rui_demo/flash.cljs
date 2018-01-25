(ns rui-demo.flash
  (:require
    [re-frame.core :refer [reg-event-db dispatch]]
    [rui.flash :as flash]))


(reg-event-db
  :flash/update-message-text
  (fn [db [_ text]]
    (assoc-in db [:flash :message-text] text)))


(reg-event-db
  :flash/update-message-severity
  (fn [db [_ severity]]
    (assoc-in db [:flash :message-severity] severity)))


(reg-event-db
  :flash/add
  (fn [db _]
    (-> db
        (flash/add-message (name (get-in db [:flash :message-severity] :primary)) (get-in db [:flash :message-text]))
        (assoc-in [:flash :message-text] nil))))


(defn- update-message-text!
  [e]
  (dispatch [:flash/update-message-text (-> e .-target .-value)]))


(defn- add-message!
  [e]
  (dispatch [:flash/add]))


(defn- update-message-severity!
  [e]
  (dispatch [:flash/update-message-severity (-> e .-target .-value)]))


(defn flash-demo
  [db]
  [:div.flash-demo.card-body
   [:input {:on-change update-message-text!, :value (-> db :flash :message-text)}]
   [:button {:on-click add-message!} "Add message"]
   [:select {:on-change update-message-severity!}
    (for [value ["primary" "secondary" "info" "danger" "warning" "success" "light" "dark"]]
      ^{:key value}
      [:option {:value value} value])]
   [flash/flash (flash/db->messages db)]])
