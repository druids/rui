(ns rui.forms.middlewares
  (:require
    [re-frame.core :refer [enrich]]
    [rui.forms.core :as forms]))


(def blur-field
  (enrich
    (fn [db [_ form-id id]]
      (assoc-in db [:rui::forms form-id :fields id :blured?] true))))


(def validate-form
  (enrich
    (fn [db [_ form-id field-id]]
      (-> db
          (update-in [:rui::forms form-id] forms/validate-form)
          (update-in [:rui::forms form-id :fields field-id] #(if (:blured? %)
                                                               (assoc % :state (if (:valid? %) :valid :invalid))
                                                               %))))))
