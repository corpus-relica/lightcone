(ns rlc.lightcone.routes.auth
  (:require [compojure.core :refer [defroutes context GET POST OPTIONS PUT DELETE]]
            [ring.util.response :as response]
            [clojure.walk :refer [keywordize-keys]]
            [rlc.lightcone.app.calendar :as calendar]
            [rlc.lightcone.io.auth :refer [authenticate-user
                                        generate-token
                                        db-get-user-by-username
                                        create-user]]
            [rlc.lightcone.env :refer [CLARITY_SERVICE_URL]]))


;; In routes/auth.clj
(defroutes auth-routes
  (OPTIONS "/login" _
    (-> (response/response nil)
        (response/header "Access-Control-Allow-Origin" "http://64.23.130.139:3004")
        (response/header "Access-Control-Allow-Methods" "POST, OPTIONS")
        (response/header "Access-Control-Allow-Headers" "Content-Type")))

  (POST "/login" request
    (tap> "Login attempt received")
    (-> (response/response {:message "Login endpoint reached"})
        (response/header "Access-Control-Allow-Origin" "http://64.23.130.139:3004")
        (response/content-type "application/json"))))
