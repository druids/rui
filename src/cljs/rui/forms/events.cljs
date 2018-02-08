(ns rui.forms.events
  (:require
    [re-frame.core :refer [reg-event-db]]
    [rui.forms.core :refer [init-form assoc-values]]
    [rui.forms.middlewares :refer [blur-field validate-form]]))


(reg-event-db
  :rui::forms/forms-input-blured
  [validate-form blur-field]
  (fn [db [_ form-id field-id]]
    db))


(reg-event-db
  :rui::forms/forms-input-changed
  [validate-form]
  (fn [db [_ form-id field-id value]]
    (assoc-in db [:rui::forms form-id :fields field-id :value] value)))


(reg-event-db
  :rui::forms/init-form
  (fn [db [_ form-map initial-values]]
    (init-form db form-map initial-values)))


(reg-event-db
  :rui::forms/assoc-form-values
  (fn [db [_ form-id values]]
    (assoc-values db form-id values)))
