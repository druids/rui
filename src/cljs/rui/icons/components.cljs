(ns rui.icons.components
  (:require
    [ccn.core :refer [css-class bem]]))


(defn fa
  "Returns Font Awesome icon by a given `fa-name`. Set `spin?` to `true` then an icon will spin.
   Kwargs:
   :modifiers - BEM modifiers
   :attrs - a map of attributes to override element's attributes"
  ([fa-name]
   [fa fa-name false])
  ([fa-name spin?]
   (fa fa-name spin? nil))
  ([fa-name spin? & {:keys [modifiers attrs]}]
   [:i (merge {:class (css-class (bem "fa" modifiers) (str "fa-" fa-name) (when spin? "fa-spin"))}
              attrs)]))


(defn icon-label
  "Wraps an icon and label together. You this component when you need to align an icon and label.
   Kwargs:
   :modifiers - BEM modifiers"
  [icon label & {:keys [modifiers]}]
  [:span {:class (bem "icon-label" modifiers)}
   [:span {:class (bem "icon-label" "icon" [])} icon]
   [:span {:class (bem "icon-label" "label" [])} label]])


(defn label-icon
  "Wraps a label and an icon together. You this component when you need to align a label and icon.
   Kwargs:
   :modifiers - BEM modifiers"
  [label icon & {:keys [modifiers]}]
  [:span {:class (bem "label-icon" modifiers)}
   [:span {:class (bem "label-icon" "label" [])} label]
   [:span {:class (bem "label-icon" "icon" [])} icon]])
