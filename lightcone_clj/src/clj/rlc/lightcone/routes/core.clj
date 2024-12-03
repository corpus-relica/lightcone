(ns rlc.lightcone.routes.core
  (:require [compojure.core :refer [defroutes context GET]]
            [compojure.route :as route]
            [ring.util.response :as response]
            [rlc.lightcone.routes.events :refer [event-routes]]
            [rlc.lightcone.routes.person :refer [person-routes]]
            [rlc.lightcone.routes.auth :refer [auth-routes]]))

(defroutes app-routes
  ;; Static routes
  (GET "/" []
    (if-let [index (response/resource-response "public/index.html")]
      (response/content-type index "text/html")
      (response/not-found "Index page not found")))

  ;; API routes from other namespaces
  event-routes
  person-routes
  auth-routes

  ;; Static resources and fallback
  (route/resources "/")
  (route/not-found "Not Found"))
