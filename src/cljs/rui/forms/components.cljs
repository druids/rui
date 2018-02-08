(ns rui.forms.components
  "It contains UI components for forms like checkbox, text input, form errors, etc."
  (:require
    [goog.string.format]
    [goog.string :refer [format]]
    [ccn.core :refer [bem css-class twbs]]
    [re-frame.core :refer [dispatch]]
    [rui.buttons :refer [button-primary]]
    [rui.forms.core :refer [field-state input-on-change! input-on-blur! gen-field-id]]
    [rui.forms.events]))


(defn- field->twbs-class
  [field]
  (case (:state field)
    :initial nil
    :invalid "is-invalid"
    :valid "is-valid"
    nil))


(defn form-errors
  [messages]
  [:div.form-errors.invalid-feedback
   (for [[message i] (zipmap messages (range))]
     ^{:key (format "%s-%s" message i)}
     [:div {:class (bem "form-errors" "error" [])} message])])


(defn- can-show-errors?
  [field errors]
  (and (not= :initial (:state field))
       (seq errors)))


(defn radio
  [form field-id choices & {:keys [modifiers]}]
  (let [field (-> form :fields field-id)
        errors (-> form :errors field-id)
        on-change (fn [event]
                    (input-on-change! form field-id identity event)
                    (input-on-blur! form field-id event)
                    (.preventDefault event))
        radio-name (gen-field-id form field-id)]
    [:div.form-group.radio
     (for [choice choices]
       (let [id (gen-field-id form (name (str (name field-id) "-" (str (:value choice)))))
             disabled? (true? (:disabled? choice))]
         ^{:key (:value choice)}
         [:div {:class (css-class "form-check"
                                  (when disabled? "disabled")
                                  (bem "radio" (concat modifiers [(field-state form field-id)])))}
          [:input {:type "radio"
                   :class (css-class "form-check-input" (field->twbs-class field))
                   :id id
                   :name radio-name
                   :value (:value choice)
                   :on-change on-change
                   :checked (or (= (:value field) (:value choice))
                                (and (nil? (:value field)) (true? (:checked? choice))))
                   :disabled disabled?}]

          [:label {:class "form-check-label", :for id} (:label choice)]]))
     (when (can-show-errors? field errors)
       [form-errors errors])]))


(defn checkbox
  [form field-id label & {:keys [children modifiers attrs]
                          :or {modifiers []
                               children []
                               attrs {}}}]
  (let [field (-> form :fields field-id)
        active? (-> field :value boolean)
        errors (-> form :errors field-id)
        on-change (fn [event]
                    (dispatch [:rui::forms/forms-input-changed (:id form) field-id (-> event .-target .-checked)])
                    (input-on-blur! form field-id event))
        id (gen-field-id form field-id)]
    (into [:div {:class (css-class "form-check"
                                   "form-group"
                                   (bem "checkbox" (concat modifiers
                                                           [(when active? "active") (field-state form field-id)])))}
           [:input (merge {:type "checkbox"
                           :class (css-class "form-check-input" (field->twbs-class field))
                           :id id
                           :name id
                           :on-change on-change
                           :checked active?}
                          attrs)]
           [:label {:for id, :class "form-check-label"} label]]
          (conj children (when (can-show-errors? field errors) [form-errors errors])))))


(defn select
  [form field-id label value-label-pairs & {:keys [children modifiers attrs label-attrs twbs-modifiers]
                                            :or {modifiers []
                                                 children []
                                                 attrs {}
                                                 label-attrs {}
                                                 twbs-modifiers []}}]
  (let [field (-> form :fields field-id)
        active? (-> field :value some?)
        errors (-> form :errors field-id)
        on-change (fn [event]
                    (dispatch [:rui::forms/forms-input-changed (:id form) field-id (-> event .-target .-value)])
                    (input-on-blur! form field-id event))
        id (gen-field-id form field-id)]
    (into [:div {:class (css-class "form-group"
                                   (bem "select" (concat modifiers
                                                         [(when active? "active") (field-state form field-id)])))}
           [:label (merge {:for id, :class "form-control-label"} label-attrs) label]
           [:select (merge {:id id
                            :name id
                            :on-change on-change
                            :class (css-class (twbs "form-control" twbs-modifiers)
                                              (field->twbs-class field))}
                           attrs)
            (for [[value label]
                  value-label-pairs]
              ^{:key value}
              [:option {:value value} label])]]
          (conj children (when (can-show-errors? field errors) [form-errors errors])))))


(defn input-field
  [input-type form field-id label & {:keys [modifiers attrs label-attrs twbs-modifiers]
                                     :or {modifiers []
                                          attrs {}
                                          label-attrs {}
                                          twbs-modifiers []}}]
  (let [id (gen-field-id form field-id)
        field (get-in form [:fields field-id])]
    [:div {:class (css-class "form-group"
                             (bem "input-field" (conj modifiers (field-state form field-id))))}
     (when (some? label)
       [:label (merge {:for id, :class "form-control-label"}
                      label-attrs)
        label])
     [:input (merge {:type input-type
                     :class (css-class (twbs "form-control" twbs-modifiers)
                                       (field->twbs-class field))
                     :id id
                     :name id
                     :value (-> form :fields field-id :value)
                     :placeholder label
                     :on-change (partial input-on-change! form field-id)
                     :on-blur (partial input-on-blur! form field-id)}
                    attrs)]
     (when (can-show-errors? (-> form :fields field-id) (-> form :errors field-id))
       [form-errors (-> form :errors field-id)])]))


(def input-text (partial input-field "text"))
(def input-password (partial input-field "password"))
(def input-number (partial input-field "number"))
(def input-email (partial input-field "email"))


(defn input-file
  [form field-id label & {:keys [modifiers attrs label-attrs twbs-modifiers]
                          :as kwargs}]
  (let [id (gen-field-id form field-id)
        field (get-in form [:fields field-id])]
    [:div {:class (css-class "form-group"
                             (bem "input-field" (conj modifiers (field-state form field-id))))}
     [:div.custom-file
      (when (some? label)
        [:label (merge {:for id, :class "form-control-label"}
                       label-attrs)
         label])
      [:input (merge {:type "file"
                      :class (css-class (twbs "form-control-file" twbs-modifiers)
                                        (field->twbs-class field))
                      :id id
                      :name id
                      :value (-> form :fields field-id :value)
                      :placeholder label
                      :on-change (partial input-on-change! form field-id)
                      :on-blur (partial input-on-blur! form field-id)}
                     attrs)]
      (when (can-show-errors? (-> form :fields field-id) (-> form :errors field-id))
        [form-errors (-> form :errors field-id)])]]))


(defn text-area
  [form field-id label & {:keys [modifiers attrs label-attrs twbs-modifiers]
                          :or {modifiers []
                               attrs {}
                               label-attrs {}
                               twbs-modifiers []}}]
  (let [id (gen-field-id form field-id)
        field (get-in form [:fields field-id])]
    [:div {:class (css-class "form-group"
                             (bem "text-area" (conj modifiers (field-state form field-id))))}
     (when (some? label)
       [:label (merge {:for id, :class "form-control-label"}
                      label-attrs)
        label])
     [:textarea (merge {:class (css-class (twbs "form-control" twbs-modifiers)
                                          (field->twbs-class field))
                        :id id
                        :name id
                        :value (-> form :fields field-id :value)
                        :placeholder label
                        :on-change (partial input-on-change! form field-id)
                        :on-blur (partial input-on-blur! form field-id)}
                       attrs)]
     (when (can-show-errors? (-> form :fields field-id) (-> form :errors field-id))
       [form-errors (-> form :errors field-id)])]))


(defn form-wrapper
  [{:keys [on-submit] :as kwargs} & children]
  (into [:form {:class "form-wrapper", :on-submit #(do (.preventDefault %) (on-submit %))}]
        children))


(defn button-submit
  [title on-click & {:as kwargs}]
  (into [button-primary title on-click] (apply concat (merge kwargs {:modifiers ["submit"]}))))


(defn plaintext
  [form field-id label & {:keys [modifiers attrs label-attrs twbs-modifiers]
                          :or {modifiers []
                               attrs {}
                               label-attrs {}
                               twbs-modifiers []}}]
  (let [id (gen-field-id form field-id)]
    [:div {:class (css-class "form-group"
                             (bem "plaintext" (conj modifiers (field-state form field-id))))}
     (when (some? label)
       [:label (merge {:for id, :class "form-control-label"}
                      label-attrs)
        label])
     [:input (merge {:class (twbs "form-control-plaintext" twbs-modifiers)
                     :id id
                     :read-only true
                     :name id
                     :type "text"
                     :value (-> form :fields field-id :value)
                     :placeholder label
                     :on-change (partial input-on-change! form field-id)
                     :on-blur (partial input-on-blur! form field-id)}
                    attrs)]]))
