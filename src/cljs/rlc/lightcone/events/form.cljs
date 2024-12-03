(ns rlc.lightcone.events.form
  (:require [re-frame.core :as rf]))


(rf/reg-event-fx
 :form/set-field
 (fn [{:keys [db]} [_ field value]]
   {:db (assoc-in db [:form field] value)
    :dispatch [:calendar/update-event-title {:uid (:uid (:form db)) :title value}]
    }))

(rf/reg-event-fx
 :form/set-note-field
 (fn [{:keys [db]} [_ field value]]
   (js/console.log "set-note-field" (clj->js {:uid (:uid (:form db)) :note value}))
   {:db (assoc-in db [:form field] value)
    :dispatch [:calendar/update-event-note {:uid (:uid (:form db)) :note value}]
    }))

(rf/reg-event-fx
 :form/set-time-field
 (fn [{:keys [db]} [_ field value]]
   (js/console.log "set-time-field" (clj->js field) value)
   {:db (assoc-in db [:form field] value)
    :dispatch [:calendar/update-event (:form (assoc-in db [:form field] value))]
    }))

(rf/reg-event-db
 :form/clear
 (fn [db _]
   (let [now (.toISOString (js/Date.))]
     (assoc db :form {:uid 0
                      :title ""
                      :time (.toISOString (js/Date.))  ; UTC timestamp
                      :participants #{}
                      :event-type "event"
                      :note ""}))))

(rf/reg-event-fx
 :form/set-participants-field
 (fn [{:keys [db]} [_ field value]]
   {:db (assoc-in db [:form field] value)
    :dispatch [:calendar/update-event-participants {:uid (:uid (:form db))
                                                    :participants value}]
    }))
