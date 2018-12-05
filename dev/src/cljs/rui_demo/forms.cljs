(ns rui-demo.forms
  (:require
    [clojure.string :refer [blank?]]
    [re-frame.core :refer [dispatch]]
    [reagent.core :as reagent]
    [rui.buttons :as buttons]
    [rui.forms :as forms]))


(def form-id :demo)


(defn validator
  [values]
  (when (-> values :name blank?)
    {:name ["Name cannot be blank"]}))


(defn forms-demo
  [db]
  (reagent/create-class
    {:display-name
     "forms-demo"

     :component-will-mount
     #(forms/init-form! form-id validator {:name "initial name", :language :sv})

     :reagent-render
     (fn [db]
       (let [form (forms/db->form db form-id)
             on-submit #(when (:valid? form) (js/console.log form))]
         (when form
           [:div.forms-demo.card-body
            [forms/form-wrapper {:on-submit on-submit}
             [forms/input-text form :name "Name" :attrs {:placeholder "Please fill you name"}]
             [forms/input-email form :email "E-mail" :attrs {:placeholder "Please fill you e-mail address"}]
             [forms/select form :children "Children" (map #(vector % %) (range 9))]
             [forms/select form :language "Language" [[:cs "CS"] [:de "DE"] [:sv "SV"]]]
             [forms/text-area form :notes "Notes" :attrs {:placeholder "Please let us know if you missed something"}]
             [forms/input-text form :sm "Small input"
              :attrs {:placeholder "See I am small"}
              :twbs-modifiers ["sm"]]
             [forms/input-text form :lg "Large input"
              :attrs {:placeholder "See I am large"}
              :twbs-modifiers ["lg"]]
             [forms/input-text form :readonly "Readonly" :attrs {:placeholder "See I am readonly", :read-only true}]
             [forms/plaintext form :plaintext "Readonly plain text"
              :attrs {:placeholder "This value is read only plain text"}]
             [forms/input-file form :cv "Your CV"]
             [forms/radio form :radio [{:value "default", :label "Default radio", :checked? true}
                                       {:value "second", :label "Second radio"}
                                       {:value "disabled", :label "Disabled radio", :disabled? true}]]
             [forms/checkbox form :default-checkbox "Default checkbox"]
             [forms/checkbox form :disabled-checkbox "Disabled checkbox" :attrs {:disabled true}]
             [forms/button-submit "Submit" on-submit
              :enabled? (:valid? form)]]
            (let [values (forms/form->values form)]
              [:div.card-body
               [:h6.card-subtitle.text-muted "Form values"]
               (for [[k v] values]
                 ^{:key k}
                 [:dl.row
                  [:dt.col-sm-3 k]
                  [:dd.col-sm-9 (pr-str v)]])])])))}))
