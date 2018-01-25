(ns rui-demo.buttons
  (:require
    [rui.buttons :as buttons]))


(def button-types ["primary" "secondary" "danger" "success" "warning" "info" "light" "dark"])


(defn buttons-demo
  [db]
  [:div.buttons-demo
   [:div.card-body
    [:h6.card-subtitle.text-muted "default buttons"]
    (for [button-type button-types]
      ^{:key button-type}
      [(buttons/button-factory button-type) button-type #(console.log button-type)
       :classes ["mr-1"]])]

   [:div.card-body
    [:h6.card-subtitle.text-muted "large buttons"]
    (for [button-type button-types]
      ^{:key button-type}
      [(buttons/button-factory button-type) button-type #(console.log button-type)
       :classes ["mr-1"]
       :twbs-modifiers ["lg"]])]

   [:div.card-body
    [:h6.card-subtitle.text-muted "large buttons"]
    (for [button-type (map (partial str "outline-") button-types)]
      ^{:key button-type}
      [(buttons/button-factory button-type) button-type #(console.log button-type)
       :classes ["mr-1" "mt-1"]])]

   [:div.card-body
    [:h6.card-subtitle.text-muted "disabled button"]
    [buttons/button-primary "disabled button" #(console.log "disabled button")
     :enabled? false]]

   [:div.card-body
    [:h6.card-subtitle.text-muted "waiting button"]
    [buttons/button-primary "waiting button" #(console.log "waiting button")
     :waiting? true]]])
