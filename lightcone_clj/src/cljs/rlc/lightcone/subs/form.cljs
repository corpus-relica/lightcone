(ns rlc.lightcone.subs.form
  (:require [re-frame.core :as rf]))


(rf/reg-sub
 :form/fields
 (fn [db _]
   (:form db)))
