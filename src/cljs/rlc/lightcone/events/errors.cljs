(ns rlc.lightcone.events.errors
  (:require [re-frame.core :as rf]))

(rf/reg-event-fx
 :error/api
 (fn [{:keys [db]} [_ error-response]]
   {:db (assoc db :error error-response)
    :log/error ["API Error:" error-response]}))
