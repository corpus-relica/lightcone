(ns rlc.lightcone.routes.auth
  (:require [compojure.core :refer [defroutes context GET POST OPTIONS PUT DELETE]]
            [ring.util.response :as response]
            [clojure.walk :refer [keywordize-keys]]
            [rlc.lightcone.io.calendar :as calendar]
            [rlc.lightcone.io.auth :refer [authenticate-user
                                        generate-token
                                        db-get-user-by-username
                                        create-user]]))

(defroutes auth-routes
  (OPTIONS "/register" _ (response/response nil))
  (POST "/register" request
    (let [username (get-in request [:body "username"])
          password (get-in request [:body "password"])]
      (tap> "REGISTER")
      (tap> username)
      (tap> password)
      (if (db-get-user-by-username username)
        {:status 400 :body "Username already exists"}
        (do
          (create-user username password)
          {:status 201 :body "User created successfully"}))))

  (OPTIONS "/login" _ (response/response nil))
  (POST "/login" request
    (let [username (get-in request [:body "username"])
          password (get-in request [:body "password"])]
      (tap> "LOGIN")
      (tap> username)
      (tap> password)
      (if-let [user (authenticate-user username password)]
        (do
          (tap> "FONOFDSFDSFDSFDSF")
          (tap> user)
          {:status 200 :body {:token user}})
        {:status 401 :body "Invalid credentials"})))

  (POST "/logout" []
    (-> (response/redirect "/")
        (assoc :session nil))))
