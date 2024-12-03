(ns rlc.lightcone.subs.core
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :loading?
 (fn [db _]
   (:loading? db)))
