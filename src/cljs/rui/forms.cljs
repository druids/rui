(ns rui.forms
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
(def form->values core/form->values)
