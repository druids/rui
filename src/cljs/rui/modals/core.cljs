(ns rui.modals.core
  (:require
    [goog.events :refer [listen unlistenByKey]]
    [goog.events.EventType :refer [KEYUP]]
    [goog.events.KeyCodes :refer [ESC]]
    [re-frame.core :refer [dispatch]]))


(defn open-modal
  "Sets a modal by a given `id` to opened state. Sets a listener for Escape key, when called multiple times
   the listener is not reset."
  [db id on-close]
  (-> db
      (assoc-in [:rui::modals id :opened?] true)
      (update-in [:rui::modals id :listener-key] (fn [listener-key]
                                                   (if (some? listener-key)
                                                     listener-key
                                                     (listen js/window KEYUP #(when (= ESC (.-keyCode %))
                                                                                (on-close %))))))))


(defn close-modal
  "Sets a modal by a given `id` to closed state. Unlistens a listener for Escape key, when called multiple times
   the listener is remove only once."
  [db id]
  (-> db
      (assoc-in [:rui::modals id :opened?] false)
      (update-in [:rui::modals id :listener-key] (fn [listener-key]
                                                   (when (some? listener-key)
                                                     (unlistenByKey listener-key))
                                                   nil))))


(defn initial-state
  [id]
  {:id id, :opened? false, :listener-key nil})


(defn init-modal-state!
  ([id]
   (init-modal-state! id nil))
  ([id state]
   (dispatch [:rui::modals/init-state id (merge (initial-state id) state)])))


(defn db->modal-state
  [db id]
  (get-in db [:rui::modals id]))


(defn close-modal!
  [id event]
  (dispatch [:rui::modals/close id]))


(defn open-modal!
  [id event]
  (dispatch [:rui::modals/open id (partial close-modal! id)]))
