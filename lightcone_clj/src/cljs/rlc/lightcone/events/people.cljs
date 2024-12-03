(ns rlc.lightcone.events.people
  (:require [re-frame.core :as rf]
            [rlc.lightcone.api.contacts :as contacts]
            [cljs.core.async :refer [go <!]]))

(rf/reg-event-fx
 :people/fetch
 (fn [{:keys [db]} _]
   {:db (assoc db :loading? true)
    :people/fetch nil}))

(rf/reg-event-db
 :people/loaded
 (fn [db [_ people]]
   (js/console.log "People loaded:" (clj->js people))
   (-> db
       (assoc :people people)
       (assoc :loading? false))))

(rf/reg-fx                    ;; Only one fetch-people effect)
 :people/fetch
  (fn [_]
    (go (when-let [people (<! (contacts/fetch-people))]
          (rf/dispatch [:people/loaded people])))))
