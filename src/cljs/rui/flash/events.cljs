(ns rui.flash.events
  (:require
    [re-frame.core :refer [reg-event-db]]))


(reg-event-db
  :rui::flash/dismiss-message
  (fn [db [_ id]]
    (update-in db [:rui::flash :messages] (partial remove #(= (:id %) id)))))
