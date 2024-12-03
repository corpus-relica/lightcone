(ns rlc.lightcone.views.people-list
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [re-com.core :as rc]))


(defn people-list []
  (let [people @(rf/subscribe [:people])]
    (js/console.log "!!!!!!!!!!!!!!!!!!!!!!!!!!! Rendering people list with:" (clj->js people))
    [:div.people-list
     [:h3 "People"]
     (for [person people]
       ^{:key (:id person)}
       [:div.person {:style {:margin "10px"
                           :padding "10px"
                           :border "1px solid #ccc"}}
        [:h4 (:name person)]
        ])]))
