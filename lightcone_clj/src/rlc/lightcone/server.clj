(ns rlc.lightcone.server
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [rlc.lightcone.routes.core :refer [app-routes]]
            [ring.middleware.cors :refer [wrap-cors]]
            [portal.api :as p]

[rlc.lightcone.io.auth :refer [authenticate-user
                                        generate-token
                                        db-get-user-by-username
                                        create-user]]
            ))

;; Add middleware in the correct order
(def app
  (-> #'app-routes  ; Note the var quote here
      (wrap-cors
       :access-control-allow-origin [#"http://localhost:3004"
                                     #"http://64.23.130.139:3004"
                                  ;; #"http://localhost:3003"
                                  ;; #"http://64.23.130.139:3003"
                                     ]
       :access-control-allow-methods [:get :put :post :delete :options]
       :access-control-allow-headers ["Content-Type" "Authorization"])  ; Added Authorization
      (wrap-json-body {:keywords? true})  ; Add keywords? true
      wrap-json-response))

(defn start-server [port]
  (jetty/run-jetty app {:port port  ; Remove the var quote here
                        :join? false
                        :max-header-size 65536}))

(defn -main [& args]
  ;; Start portal with web UI
  (p/open {:port 5555})
  (add-tap #'p/submit)

  (tap> "Starting Lightcone server...foolishness")

  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (println "Starting server on port" port)

    (start-server port)
    (println "Server started on port" port)

    (tap> "Started")
      (if-let [user (authenticate-user "john" "changeme")]
        (do
          (tap> "FONOFDSFDSFDSFDSF")
          (tap> user))
        (tap> "Invalid credentials"))))
