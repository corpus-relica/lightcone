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
  (OPTIONS "/login" _ (response/response nil))
  (POST "/login" request
    ;; First, just return a test response to verify basic routing
    (tap> "Login attempt received")
    (response/response {:message "Login endpoint reached"})))
