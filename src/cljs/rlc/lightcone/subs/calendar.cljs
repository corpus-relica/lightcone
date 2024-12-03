(ns rlc.lightcone.subs.calendar
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :events
 (fn [db _]
   (js/console.log "Events subscription called with:" (clj->js (:events db)))
   (:events db)))
