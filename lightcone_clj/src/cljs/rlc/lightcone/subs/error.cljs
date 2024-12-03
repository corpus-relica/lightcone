(ns rlc.lightcone.subs.error
  (:require [re-frame.core :as rf]))


(rf/reg-sub
 :error
 (fn [db _]
   (:error db)))
