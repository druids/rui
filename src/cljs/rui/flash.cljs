(ns rui.flash
  (:require
    [rui.flash.components :as components]
    [rui.flash.core :as core]))


(def flash components/flash)
(def offline-panel components/offline-panel)
(def Flash core/Flash)
(def ->Flash core/->Flash)
(def add-message core/add-message)
(def db->messages core/db->messages)
