(ns rui.forms
  "This namespace exposes a public API of forms."
  (:require
    [rui.forms.components :as components]
    [rui.forms.core :as core]))


;; components
(def checkbox components/checkbox)
(def radio components/radio)
(def select components/select)
(def input-text components/input-text)
(def input-password components/input-password)
(def input-number components/input-number)
(def input-email components/input-email)
(def input-file components/input-file)
(def text-area components/text-area)
(def form-wrapper components/form-wrapper)
(def button-submit components/button-submit)
(def plaintext components/plaintext)


;; state functions
(def init-form! core/init-form!)
(def db->form core/db->form)
(def form->coerced-values core/form->coerced-values)
(def form->values core/form->values)
(def init-form core/init-form)
(def assoc-field-value core/assoc-field-value)
(def assoc-values core/assoc-values)
(def add-form-errors core/add-form-errors)
(def form-errors<response core/form-errors<response)
(def input-on-change! core/input-on-change!)
(def input-on-blur! core/input-on-blur!)
