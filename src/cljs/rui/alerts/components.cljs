(ns rui.alerts.components
  (:require
    [ccn.core :refer [twbs]]))


(defn alert
  "Constructs Bootstrap's alert component, takes `twbs-modifiers` as first argument and rest is taken as `children`"
  [twbs-modifiers & children]
  (into [:div {:class (twbs "alert" twbs-modifiers), :role "alert"}] children))


(def alert-primary (partial alert ["primary"]))
(def alert-secondary (partial alert ["secondary"]))
(def alert-success (partial alert ["success"]))
(def alert-danger (partial alert ["danger"]))
(def alert-warning (partial alert ["warning"]))
(def alert-info (partial alert ["info"]))
(def alert-light (partial alert ["light"]))
(def alert-dark (partial alert ["dark"]))
