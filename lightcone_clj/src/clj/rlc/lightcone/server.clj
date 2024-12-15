(ns rlc.lightcone.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [rlc.lightcone.routes.core :refer [app-routes]]
            [ring.middleware.cors :refer [wrap-cors]])) ;; your existing model namespace

(System/getenv "someshit")

;; Add middleware
(def app
  (-> app-routes
      (wrap-cors :access-control-allow-origin [#"http://localhost:3004"]
                 :access-control-allow-methods [:get :put :post :delete :options])
      wrap-json-body
      wrap-json-response))


(defn start-server [port]
  (jetty/run-jetty #'app {:port port
                          :join? false
                          :max-header-size 65536
                          }))


(comment
  ;; Start server from REPL
  (def server (start-server 3003))

  (.stop server)

  )
