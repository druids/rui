(ns rui.buttons.components
  (:require
    [reagent.core :as reagent]
    [ccn.core :refer [bem css-class twbs]]
    [rui.icons :refer [fa]]))


(defn- map->vec
  "Returns a vector from a given `hashmap`
   {:a 0 :b [1 2]} -> [:a 0 :b [1 2]]"
  [hashmap]
  (apply concat (vec hashmap)))


(defn- compose-button-class
  [modifiers twbs-modifiers classes enabled-internal? waiting?]
  (->> (bem "btn" (concat modifiers [(if enabled-internal? "enabled" "disabled") (when waiting? "waiting")]))
       (conj [(twbs "btn" twbs-modifiers)])
       (concat classes)
       (apply css-class)))


(defn button
  "Returns Bootstrap's button with a given `title` as content and `on-click` handler.
   Kwargs:
   :enabled? - a boolean that indicates enabled/disabled state of it
   :attrs - a map of attributes to override element's attributes
   :modifiers - BEM modifiers
   :twbs-modifiers - Bootstrap modifiers
   :children - a sequence of its children components
   :waiting? - a boolean that shows a spinner when `true`
   :classes - a sequence of CSS classes"
  [title on-click & {:keys [enabled? attrs modifiers waiting? children twbs-modifiers classes]
                     :or {enabled? true
                          waiting? false
                          modifiers []
                          attrs {}
                          twbs-modifiers []
                          classes []}}]
  (let [enabled-internal? (and enabled? (not waiting?))]
    (into [:button (merge {:class (compose-button-class modifiers twbs-modifiers classes enabled-internal? waiting?)
                           :on-click #(when enabled-internal?
                                        (on-click %))}
                          (when-not enabled-internal? {:disabled true})
                          attrs)
           (when waiting?
             [fa "gear" true])
           [:span {:class (bem "btn" "title" [])} title]]
          children)))


(defn button-factory
  "Helper function for creating buttons"
  [button-type]
  (fn [title on-click & {:keys [enabled? attrs modifiers waiting? children twbs-modifiers]
                         :as kwargs}]
    (into [button title on-click] (map->vec (update kwargs :twbs-modifiers conj button-type)))))
