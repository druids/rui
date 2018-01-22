(ns rui.flash.core
  (:require
    [rui.flash.events]))


(defrecord Flash [severity text timeout id dismissible?])


(defn add-message
  "Constructs `Flash` message in a given `db`"
  ([db severity text timeout id dismissible?]
   (update-in db [:rui::flash :messages] conj (->Flash severity text timeout id dismissible?)))
  ([db severity text timeout]
   (add-message db severity text timeout (random-uuid) true))
  ([db severity text]
   (add-message db severity text 10000 (random-uuid) true)))


(defn db->messages
  "Returns a sequence of flash messages from a given `db`"
  [db]
  (-> db :rui::flash :messages))
