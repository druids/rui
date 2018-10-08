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


(defn field->twbs-class
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


(defn can-show-errors?
  [field errors]
  (and (not= :initial (:state field))
       (seq errors)))


(defn radio-input-element
  "Bare radio input that renders just a div with an input type radio and label. Don't use it directly."
  [{:keys [id field radio-name choice on-change checked? disabled? label modifiers form field-id]}]
  (let [chosen-value? (= (:value choice) (:value field))]
    [:div {:class (css-class "form-check"
                             (when disabled? "disabled")
                             (bem "radio" (concat modifiers [(when chosen-value? (field-state form field-id))])))}
     [:input {:type "radio"
              :class (css-class "form-check-input" (when chosen-value? (field->twbs-class field)))
              :id id
              :name radio-name
              :value (:value choice)
              :on-change on-change
              :checked checked?
              :disabled disabled?}]
     label]))


(defn radio
  "Radio input field with all functionalities like: updating value, showing an error, ...
   Required parameters:
   - `form` a form from the state
   - `field-id` field is as a keyword
   - `choices` a all possible values of the radio, a sequence of maps with `:label` and `:value` keys
   Optional parameters:
   - `modifiers` a CSS modifiers, a sequence of strings
   - `component-class-name` a string that is added to 'form-group' Bootstrap element
   - `renderer` a custom radio renderer, Reagent component that accepts a hashmap with following keys:
      id, field, radio-name, choice, on-change, checked?, disabled?, label, modifiers, form, field-id, input-el
   - `on-change` a custom handler that is called after the original on-change handler"
  [form field-id choices & {:keys [modifiers component-class-name renderer on-change]}]
  (let [field (-> form :fields field-id)
        errors (-> form :errors field-id)
        on-change-internal (fn [was-keyword? event]
                             (input-on-change! form field-id (if was-keyword? keyword identity) event)
                             (input-on-blur! form field-id event)
                             (.preventDefault event)
                             (when on-change
                               (on-change event)))
        radio-name (gen-field-id form field-id)]
    [:div {:class (css-class "form-group" "radio" component-class-name)}
     (for [choice choices]
       (let [id (gen-field-id form (name (str (name field-id) "-" (str (:value choice)))))
             disabled? (true? (:disabled? choice))
             checked? (or (= (:value field) (:value choice))
                          (and (nil? (:value field)) (true? (:checked? choice))))
             label [:label {:class "form-check-label", :for id} (:label choice)]
             input-opts {:id id
                         :field field
                         :radio-name radio-name
                         :choice choice
                         :on-change (partial on-change-internal (-> choice :value keyword?))
                         :checked? checked?
                         :disabled? disabled?
                         :modifiers modifiers
                         :form form
                         :field-id field-id}
             input-el [radio-input-element (merge input-opts
                                                  {:label (when-not renderer label)})]]
         (if (some? renderer)
           (with-meta [renderer (merge input-opts
                                       {:input-el input-el})]
                      {:key (:value choice)})
           (with-meta input-el {:key (:value choice)}))))
     (when (can-show-errors? field errors)
       [form-errors errors])]))


(defn checkbox
  [form field-id label & {:keys [children modifiers attrs on-change]
                          :or {modifiers []
                               children []
                               attrs {}}}]
  (let [field (-> form :fields field-id)
        active? (-> field :value boolean)
        errors (-> form :errors field-id)
        on-change-internal (fn [event]
                             (dispatch [:rui::forms/forms-input-changed
                                        (:id form)
                                        field-id
                                        (-> event .-target .-checked)])
                             (input-on-blur! form field-id event)
                             (when on-change
                               (on-change event)))
        id (gen-field-id form field-id)]
    (into [:div {:class (css-class "form-check"
                                   "form-group"
                                   (bem "checkbox" (concat modifiers
                                                           [(when active? "active") (field-state form field-id)])))}
           [:input (merge {:type "checkbox"
                           :class (css-class "form-check-input" (field->twbs-class field))
                           :id id
                           :name id
                           :on-change on-change-internal
                           :checked active?}
                          attrs)]
           [:label {:for id, :class "form-check-label"} label]]
          (conj children (when (can-show-errors? field errors) [form-errors errors])))))


(defn select
  [form field-id label value-label-pairs & {:keys [children modifiers attrs label-attrs twbs-modifiers on-change]
                                            :or {modifiers []
                                                 children []
                                                 attrs {}
                                                 label-attrs {}
                                                 twbs-modifiers []}}]
  (let [field (-> form :fields field-id)
        active? (-> field :value some?)
        errors (-> form :errors field-id)
        on-change-internal (fn [was-keyword? event]
                             (input-on-change! form field-id (if was-keyword? keyword identity) event)
                             (input-on-blur! form field-id event)
                             (when on-change
                               (on-change event)))
        id (gen-field-id form field-id)]
    (into [:div {:class (css-class "form-group"
                                   (bem "select" (concat modifiers
                                                         [(when active? "active") (field-state form field-id)])))}
           [:label (merge {:for id, :class "form-control-label"} label-attrs) label]
           [:select (merge {:id id
                            :name id
                            :on-change (partial on-change-internal
                                                (->> value-label-pairs (map first) (every? keyword?)))
                            :class (css-class (twbs "form-control" twbs-modifiers)
                                              (field->twbs-class field))}
                           attrs)
            (for [[value label] value-label-pairs]
              ^{:key value}
              [:option {:value value} label])]]
          (conj children (when (can-show-errors? field errors) [form-errors errors])))))


(defn input-field
  "Input field with all functionalities like: updating value, showing an error, ...
   Required parameters:
   - `input-type` a string like: 'text', 'number', ...
   - `form` a form from the state
   - `field-id` field is as a keyword
   - `label` a Reagent valid component or string
   Optional parameters:
   - `modifiers` a CSS modifiers, a sequence of strings
   - `attrs` a hashmap of HTML attributes of the input
   - `label-attrs` a hashmap of HTML attributes for the label
   - `twbs-modifiers` Twitter Bootstrap modifiers, a sequence of strings
   - `input-group-append` a Twitter Bootstrap's 'input-group-append', a Reagent component of string
   - `input-group-prepend` a Twitter Bootstrap's 'input-group-prepend', a Reagent component of string
   - `on-change` a custom handler that is called after the original on-change handler
   - `on-blur` a custom handler that is called after the original on-blur handler"
  [input-type form field-id label & {:keys [modifiers attrs label-attrs twbs-modifiers input-group-append
                                            input-group-prepend on-change on-blur]
                                     :or {modifiers []
                                          attrs {}
                                          label-attrs {}
                                          twbs-modifiers []
                                          input-group-prepend nil
                                          input-group-append nil}}]
  (let [id (gen-field-id form field-id)
        field (get-in form [:fields field-id])
        on-change-internal (fn [event]
                             (input-on-change! form field-id event)
                             (when on-change
                               (on-change event)))
        on-blur-internal (fn [event]
                           (input-on-blur! form field-id event)
                           (when on-blur
                             (on-blur event)))]
    [:div {:class (css-class "form-group"
                             (bem "input-field" (name field-id) (conj modifiers (field-state form field-id))))}
     (when (some? label)
       [:label (merge {:for id, :class "form-control-label"}
                      label-attrs)
        label])
     [:div {:class (when (or input-group-prepend input-group-append) "input-group")}
      (when (some? input-group-prepend)
        [:div.input-group-prepend
         (if (string? input-group-prepend)
           [:div.input-group-text input-group-prepend]
           input-group-prepend)])
      [:input (merge {:type input-type
                      :class (css-class (twbs "form-control" twbs-modifiers)
                                        (field->twbs-class field))
                      :id id
                      :name id
                      :value (-> form :fields field-id :value)
                      :placeholder label
                      :on-change on-change-internal
                      :on-blur on-blur-internal}
                     attrs)]
      (when (some? input-group-append)
        [:div.input-group-append
         (if (string? input-group-append)
           [:div.input-group-text input-group-append]
           input-group-append)]

       (when (can-show-errors? (-> form :fields field-id) (-> form :errors field-id))
         [form-errors (-> form :errors field-id)]))]]))


(def input-text (partial input-field "text"))
(def input-password (partial input-field "password"))
(def input-number (partial input-field "number"))
(def input-email (partial input-field "email"))


(defn input-file
  [form field-id label & {:keys [modifiers attrs label-attrs twbs-modifiers on-blur on-change]
                          :as kwargs}]
  (let [id (gen-field-id form field-id)
        field (get-in form [:fields field-id])
        on-change-internal (fn [event]
                             (input-on-change! form field-id event)
                             (when on-change
                               (on-change event)))
        on-blur-internal (fn [event]
                           (input-on-blur! form field-id event)
                           (when on-blur
                             (on-blur event)))]
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
                      :on-change on-change-internal
                      :on-blur on-blur-internal}
                     attrs)]
      (when (can-show-errors? (-> form :fields field-id) (-> form :errors field-id))
        [form-errors (-> form :errors field-id)])]]))


(defn text-area
  [form field-id label & {:keys [modifiers attrs label-attrs twbs-modifiers on-blur on-change]
                          :or {modifiers []
                               attrs {}
                               label-attrs {}
                               twbs-modifiers []}}]
  (let [id (gen-field-id form field-id)
        field (get-in form [:fields field-id])
        on-change-internal (fn [event]
                             (input-on-change! form field-id event)
                             (when on-change
                               (on-change event)))
        on-blur-internal (fn [event]
                           (input-on-blur! form field-id event)
                           (when on-blur
                             (on-blur event)))]
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
                        :on-change on-change-internal
                        :on-blur on-blur-internal}
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
