(ns rui.modals
  (:require
    [rui.modals.components :as components]
    [rui.modals.core :as core]))


(def modal components/modal)

(def open-modal! core/open-modal!)
(def close-modal! core/close-modal!)
(def db->modal-state core/db->modal-state)
