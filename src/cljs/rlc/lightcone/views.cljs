(ns rlc.lightcone.views
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [re-com.core :as rc]
            [goog.date.UtcDateTime]
            [rlc.lightcone.views.event-form :refer [event-form]]
            [rlc.lightcone.views.timeline :refer [timeline-component]]
            [rlc.lightcone.views.event-list :refer [event-list]]
            [rlc.lightcone.views.people-list :refer [people-list]]
            [re-com.tabs                    :refer [horizontal-tabs-args-desc bar-tabs-args-desc pill-tabs-args-desc
                                                    horizontal-tabs-parts-desc bar-tabs-parts-desc pill-tabs-parts-desc]]
            ))


(def tabs-definition
  [{:id ::tab1  :label "Events"  :say-this "I don't like my tab siblings, they smell."}
   {:id ::tab2  :label "People"  :say-this "Don't listen to Tab1, he's just jealous of my train set."}])

(defn error-display []
  (when-let [error @(rf/subscribe [:error])]
    [rc/alert-box
     :alert-type :danger
     :heading "Error"
     :body (str error)]))

(defn main-panel []
  (let [selected-tab-id (r/atom (:id (first tabs-definition)))]
    (fn []  ; Return render function for lifecycle management
      (js/console.log "CHANGE TAB" (clj->js @selected-tab-id))
      [:div
       [error-display]
       [:h1 "Calendar"]
       [rc/h-box
        :src (rc/at)
        :align :center
        :children
        [[rc/horizontal-tabs
          :src (rc/at)
          :model @selected-tab-id  ; Dereference here
          :tabs tabs-definition
          :on-change #(reset! selected-tab-id %)]]]

       (case @selected-tab-id
         ::tab1
         [:div
          [event-form]
          [timeline-component]
          [event-list]]
         ::tab2
          [people-list]
         )])))
