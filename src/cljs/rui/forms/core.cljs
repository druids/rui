(ns rui.forms.core
  "It contains all logic functions for forms."
  (:require
    [goog.string.format]
    [goog.string :refer [format]]
    [re-frame.core :refer [dispatch]]))


(def field-states #{:initial :valid :invalid})


;; represent a form field
(defrecord Field [id value valid? state blured?])

;; represent a form itself
(defrecord Form [id fields valid? errors validator])


(defn gen-field-id
  [form field-name]
  (format "%s_%s" (-> form :id name) (name field-name)))


(defn db->form
  "Return a `Form` by a given `id` in the `db`."
  [db id]
  (get-in db [:rui::forms id]))


(defn init-field
  "Initializes a `Field` by `id`, or `value` with default values."
  ([id]
   (init-field id nil))
  ([id value]
   (->Field id value false :initial false)))


(defn create-form
  "Creates a `Form` from a given `map`."
  [form-map init-values]
  (-> {:fields (reduce-kv (fn [fields field-id init-value]
                            (assoc fields field-id (init-field field-id init-value)))
                          {}
                          init-values)
       :errors {}
       :valid? false}
      (merge form-map)
      map->Form))


(defn field-state
  "Returns a field's state as a `string`."
  [form id]
  (let [state (-> form :fields id :state)]
    (if (nil? state)
      ""
      (name state))))


(defn form->values
  "Returns `form`'s values as a hashmap."
  [form]
  (reduce-kv (fn [values id field]
               (assoc values id (:value field)))
             {}
             (:fields form)))


(defn validate-form
  "Validates a given `form` by its validator. It returns new form with updated values:
   - `errors`
   - `valid?`
   - every field's `valid?` is updated too and field's `value` if coerced"
  [form]
  (let [field-values (form->values form)
        validator (:validator form)
        validator-result (validator field-values)
        [errors model] (if (vector? validator-result) validator-result [validator-result nil])
        update-value (fn [id value] (if (some? model) (get model id) value))]
    (-> form
        (assoc :errors errors)
        (assoc :valid? (empty? errors))
        (update :fields #(reduce-kv (fn [fields id field]
                                      (-> fields
                                          (assoc-in [id :valid?] (not (contains? errors id)))
                                          (update-in [id :value] (partial update-value id))))
                                    %
                                    %)))))


(defn init-form
  "Initializes a form in the `db` within `form-map` and `initial-values`"
  [db form-map initial-values]
  (-> db
      (assoc-in [:rui::forms (:id form-map)] (create-form form-map initial-values))
      (update-in [:rui::forms (:id form-map)] validate-form)))


(defn assoc-field-value
  "Assocs a given `value` into the `db` by a given `form-id` and `field-id`"
  [db form-id field-id value]
  (assoc-in db [:rui::forms form-id :fields field-id :value] value))


(defn assoc-values
  "Assocs a given `values` into the `db` by a given `form-id`"
  [db form-id values]
  (reduce-kv (fn [db field-id value] (assoc-field-value db form-id field-id value)) db values))


(defn add-form-errors
  "Adds a given `errors` into the `db` by a given `form-id`.
   The `errors` should be a map with vectors"
  [db form-id errors]
  (update-in db [:rui::forms form-id :errors] (partial merge-with into) errors))


(defn form-errors<response
  "Takes a response's body as errors and assoc them in the `db` by a `form-id`.
   The `errors` should be a map with vectors"
  [db form-id errors]
  (add-form-errors db form-id (:errors errors)))


(defn input-on-change!
  "Dispatches a input's on-change event. Optionally takes a `formatter` function that takes a new values and returns
   formatter value."
  ([form id formatter event]
   (dispatch [:rui::forms/forms-input-changed (:id form) id (-> event .-target .-value formatter)]))
  ([form id event]
   (input-on-change! form id identity event)))


(defn input-on-blur!
  "Dispatches a input's on-blur event"
  [form id event]
  (dispatch [:rui::forms/forms-input-blured (:id form) id]))


(defn init-form!
  "Initializes a form in DB by a given `form-id` with a `validator` function that takes a map of fields values
   and returns a map of vectors as errors or `nil`. Optionally takes an `initial-values` map."
  ([form-id validator]
   (init-form! form-id validator {}))
  ([form-id validator initial-values]
   (dispatch [:rui::forms/init-form {:id form-id, :validator validator} initial-values])))
