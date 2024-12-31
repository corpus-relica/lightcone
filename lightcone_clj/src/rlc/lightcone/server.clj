(ns rlc.lightcone.server
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [rlc.lightcone.routes.core :refer [app-routes]]
            [ring.middleware.cors :refer [wrap-cors]]
            [portal.api :as p]))

(def app
  (-> #'app-routes
      wrap-json-response
      (wrap-json-body {:keywords? true})
      (wrap-cors :access-control-allow-origin [#"http://64.23.130.139:3004"]
                :access-control-allow-methods [:post :options]
                :access-control-allow-headers ["Content-Type"]
                :access-control-expose-headers ["Content-Type"])))

(defn start-server [port]
  (jetty/run-jetty app {:port port  ; Remove the var quote here
                        :join? true
                        :max-header-size 65536}))

(defn -main [& args]
  ;; Start portal with web UI
  (p/open {:port 5555})
  (add-tap #'p/submit)

  (tap> "Starting Lightcone server...")

  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (println "Starting server on port" port)
    (start-server port)))
