(ns rui-demo.icons
  (:require
    [rui.icons :as icons]))


(defn icons-demo
  [db]
  [:div.icons-demo
   [:div.card-body
    [:h6.card-subtitle.text-muted "fa gear"]
    [icons/fa "gear"]
    [icons/fa "gear" true]]
   [:div.card-body
    [:h6.card-subtitle.text-muted "icon-label"]
    [icons/icon-label [icons/fa "download"] "Download"]]
   [:div.card-body
    [:h6.card-subtitle.text-muted "label-icon"]
    [icons/label-icon "Download" [icons/fa "download"]]]])
