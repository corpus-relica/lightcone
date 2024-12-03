(ns rlc.lightcone.api.contacts
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [cljs-http.client :as http]
            [cljs.core.async :refer [go <!]]
            ["vis-timeline/standalone" :refer [Timeline DataSet]]
            ))

(defn fetch-people []
  (go (let [response (<! (http/get "/api/person"))]
        (when (= 200 (:status response))
          (:person (:body response))))))
