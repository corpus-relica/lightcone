(ns rlc.lightcone.server
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [rlc.lightcone.routes.core :refer [app-routes]]
            [ring.middleware.cors :refer [wrap-cors]]
            [portal.api :as p]))

;; Add middleware
(def app
  (-> app-routes
      (wrap-cors :access-control-allow-origin [#"http://localhost:3004"
                                               #"http://64.23.130.139:3004"]
                :access-control-allow-methods [:get :put :post :delete :options])
      wrap-json-body
      wrap-json-response))

(defn start-server [port]
  (jetty/run-jetty #'app {:port port
                          :join? true
                          :max-header-size 65536}))

(defn -main [& args]
  ;; Start portal with web UI
  (p/open {:port 5555})
  (add-tap #'p/submit)

  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (println "Starting server on port" port)
    (start-server port)))


(print "EAT SHIT")
