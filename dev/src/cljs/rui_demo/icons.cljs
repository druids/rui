(ns rui-demo.icons
  (:require
    [rui.icons.components :refer [fa icon-label label-icon]]))


(defn icons-demo
  [db]
  [:div.icons-demo
   [:div.card-body
    [:h6.card-subtitle.text-muted "fa gear"]
    [fa "gear"]
    [fa "gear" true]]
   [:div.card-body
    [:h6.card-subtitle.text-muted "icon-label"]
    [icon-label [fa "download"] "Download"]]
   [:div.card-body
    [:h6.card-subtitle.text-muted "label-icon"]
    [label-icon "Download" [fa "download"]]]])
