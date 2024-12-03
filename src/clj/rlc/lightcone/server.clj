(ns rlc.lightcone.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [rlc.lightcone.routes.core :refer [app-routes]])) ;; your existing model namespace


;; Add middleware
(def app
  (-> app-routes
      wrap-json-body
      wrap-json-response))


(defn start-server [port]
  (jetty/run-jetty #'app {:port port
                          :join? false
                          :max-header-size 65536
                          }))

(comment
  ;; Start server from REPL
  (def server (start-server 3002))

  (.stop server)

  )
