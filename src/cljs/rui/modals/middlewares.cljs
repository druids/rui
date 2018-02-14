(ns rui.modals.middlewares
  (:require
    [goog.dom.classes :as classes]
    [re-frame.core :refer [after]]))


(def add-modal-open
  (after
    (fn [db _]
      (classes/add (-> js/window .-document .-body) "modal-open"))))


(def remove-modal-open
  (after
    (fn [db _]
      (classes/remove (-> js/window .-document .-body) "modal-open"))))
