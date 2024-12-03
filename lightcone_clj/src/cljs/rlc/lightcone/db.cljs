(ns rlc.lightcone.db)

(def default-db
  {:events []
   :people []
   :loading? false
   :form {:uid 0
          :title ""
          :time (.toISOString (js/Date.))  ; UTC timestamp
          :participants #{}
          :event-type "event"
          :note ""}})
