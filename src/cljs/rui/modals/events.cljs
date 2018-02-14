(ns rui.modals.events
  (:require
    [re-frame.core :refer [reg-event-db]]
    [rui.modals.core :refer [open-modal close-modal]]
    [rui.modals.middlewares :refer [add-modal-open remove-modal-open]]))


(reg-event-db
  :rui::modals/init-state
  (fn [db [_ id state]]
    (assoc-in db [:rui::modal id] state)))


(reg-event-db
  :rui::modals/open
  [add-modal-open]
  (fn [db [_ id on-close]]
    (open-modal db id on-close)))


(reg-event-db
  :rui::modals/close
  [remove-modal-open]
  (fn [db [_ id]]
    (close-modal db id)))
