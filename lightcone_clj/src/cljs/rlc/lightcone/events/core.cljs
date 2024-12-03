(ns rlc.lightcone.events.core
  (:require [re-frame.core :as rf]
            [rlc.lightcone.db :as db]))

;; Initialize
(rf/reg-event-db
 :initialize-db
 (fn [_ _]
   db/default-db))
