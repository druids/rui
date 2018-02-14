(ns rui.modals.components
  (:require
    [goog.dom.classlist :as classes]
    [goog.style :as style]
    [goog.string :refer [unescapeEntities]]
    [ccn.core :refer [bem css-class twbs]]
    [reagent.core :as reagent]
    [rui.modals.events]))


(defn- close
  [dispatcher]
  [:button {:type "button", :class "close", :aria-label "Close", :on-click dispatcher}
   [:span {:aria-hidden "true"} (unescapeEntities "&times;")]])


(defn modal-content
  [& children]
  (into [:div.modal-content] children))


(defn modal-title
  [title]
  [:div.modal-title title])


(defn modal-header
  [& children]
  (into [:div.modal-header] children))


(defn modal-body
  [body]
  [:div.modal-body body])


(defn modal-footer
  [& children]
  (into [:div.modal-footer] children))


(defn- modal-backdrop
  []
  [:div {:class (css-class "modal-backdrop" "fade" "invisible")}])


(defn- compose-dialog-class
  "Composes a class name for dialog. Handles unusual TWBS modifiers like 'sm' and 'lg' and replace them by TWBS classes"
  [unfiltered-modifiers]
  (let [unusual-modifiers {"lg" "modal-lg", "sm" "modal-sm"}
        [twbs-modifiers classes] (reduce (fn [[modifiers classes] cls]
                                           (let [new-cls (get unusual-modifiers cls)]
                                             (if (nil? new-cls)
                                               [(conj modifiers cls) classes]
                                               [modifiers (conj classes new-cls)])))
                                         [[][]]
                                         unfiltered-modifiers)]
    (apply css-class (conj classes (twbs "modal-dialog" twbs-modifiers)))))


(defn modal-dialog
  [state title on-close body & {:keys [twbs-modifiers]}]
  [:div {:class (compose-dialog-class twbs-modifiers), :role "document"}
   [modal-content
    [modal-header
     [modal-title title]
     [close on-close]]
    [modal-body body]]])


(defn- toggle-modal!
  "Toggles modal's visibility by modifing DOM (due Bootstrap's design)."
  [this]
  (let [state (reagent/props this)
        el (reagent/dom-node this)
        modal-el (.querySelector el ".modal")
        backdrop-el (.querySelector el ".modal-backdrop")]
    (if (:opened? state)
      (do
        (style/setStyle modal-el "display" "block")
        (classes/add modal-el "show")
        (classes/swap backdrop-el "invisible" "show"))
      (do
        (style/setStyle modal-el "display" "none")
        (classes/swap backdrop-el "show" "invisible")
        (classes/remove modal-el "show")))))


(defn modal
  "Show a Bootstrap's modal depending on a given `state`. Optionally take `:twbs-modifiers` that are applied
   to modal-dialog"
  [state title on-close body & {:keys [twbs-modifiers]}]
  (reagent/create-class
    {:display-name
     "modal"

     :component-did-mount
     toggle-modal!

     :component-did-update
     toggle-modal!

     :reagent-render
     (fn [state title on-close body & {:keys [twbs-modifiers]}]
       [:div {:class "modal-wrapper"}
        [:div {:class (css-class (bem "modal" [(if (:opened? state) "opened" "closed")])
                                 "fade")

               :tab-index "-1"
               :role "dialog"
               :on-click #(when (-> % .-target (classes/contains "modal"))
                            (on-close %))}
         (when (:opened? state)
           [modal-dialog state title on-close body :twbs-modifiers twbs-modifiers])]
        [modal-backdrop]])}))
