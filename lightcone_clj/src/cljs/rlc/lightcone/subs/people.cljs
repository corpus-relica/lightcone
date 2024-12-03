(ns rlc.lightcone.subs.people
  (:require [re-frame.core :as rf]))


(rf/reg-sub
 :people
 (fn [db _]
   (js/console.log "People subscription called with:" (clj->js (:people db)))
   (:people db)))
