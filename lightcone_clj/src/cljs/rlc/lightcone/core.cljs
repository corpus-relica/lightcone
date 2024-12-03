(ns rlc.lightcone.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [rlc.lightcone.events.core]
            [rlc.lightcone.events.calendar]
            [rlc.lightcone.events.people]
            [rlc.lightcone.events.errors]
            [rlc.lightcone.events.form]
            [rlc.lightcone.subs.core]
            [rlc.lightcone.subs.calendar]
            [rlc.lightcone.subs.error]
            [rlc.lightcone.subs.form]
            [rlc.lightcone.subs.people]
            [rlc.lightcone.views :as views]))

(defn ^:dev/after-load mount-root []
  (let [root-el (.getElementById js/document "app")]
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (rf/dispatch-sync [:initialize-db])
  (rf/dispatch [:calendar/fetch-events])
  (rf/dispatch [:people/fetch])
  (mount-root))
