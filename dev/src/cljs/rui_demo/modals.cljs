(ns rui-demo.modals
  (:require
    [clojure.string :refer [blank?]]
    [reagent.core :as reagent]
    [rui.buttons :as buttons]
    [rui.forms :as forms]
    [rui.modals :as modals]))


(def form-id :form-modal)

(defn validator
  [values]
  (when (-> values :name blank?)
    {:name ["Name cannot be blank"]}))


(defn- modal-internal
  [db modal-id label title body twbs-modifiers]
  (let [modal-state (modals/db->modal-state db modal-id)]
    [:div.modals-demo.card-body
     [buttons/button-primary label (partial modals/open-modal! modal-id)]
     [modals/modal modal-state title (partial modals/close-modal! modal-id)
      body
      :twbs-modifiers twbs-modifiers]]))


(defn modals-demo
  [db]
  (reagent/create-class
    {:display-name
     "modals-demo"

     :component-will-mount
     #(forms/init-form! form-id validator {:name "initial name"})

     :reagent-render
     (fn [db]
       (let [form (forms/db->form db form-id)
             on-submit #(when (:valid? form) (js/console.log form))]
         [:div.modals-demo.card-body
          [modal-internal db :modal "Open modal" "Modal title" "Woohoo, you're reading this text in a modal!" []]
          [modal-internal db :centered-modal "Open centered modal" "Modal title" "Woohoo, I'm centered"
           ["centered"]]
          [modal-internal db :lg-modal "Open large modal" "Modal title" "Woohoo, I'm large" ["lg"]]
          [modal-internal db :sm-modal "Open small modal" "Modal title" "Woohoo, I'm small" ["sm"]]
          [modal-internal db :form-modal "Open form modal" "Modal title"
           (when form
             [:div
              [forms/form-wrapper {:on-submit on-submit}]
              [forms/input-text form :name "Name" :attrs {:placeholder "Please fill you name"}]])
           []]]))}))

