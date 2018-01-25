(ns rui.buttons
  (:require
    [rui.buttons.components :as components]))


(def button components/button)
(def button-primary (components/button-factory "primary"))
(def button-secondary (components/button-factory "secondary"))
(def button-success (components/button-factory "success"))
(def button-danger (components/button-factory "danger"))
(def button-warning (components/button-factory "warning"))
(def button-info (components/button-factory "info"))
(def button-light (components/button-factory "light"))
(def button-dark (components/button-factory "dark"))
(def button-factory components/button-factory)
