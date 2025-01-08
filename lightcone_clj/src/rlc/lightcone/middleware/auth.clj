(ns rlc.lightcone.middleware.auth
  (:require [ring.util.response :as response]
            [rlc.lightcone.util :refer [extract-token]]
            [rlc.lightcone.io.auth :as auth]))

(defn with-auth
  "Middleware that handles authentication and executes handler with authenticated token.
   handler-fn should be a function that takes the token as its argument."
  [handler-fn]
  (fn [request]
    (let [token (extract-token request)
          auth-result (auth/is-authenticated? token)]
      (if (map? auth-result)
        (cond
          (:authenticated auth-result)
          (handler-fn token)

          (= (:reason auth-result) :token-expired)
          (response/status 419)

          :else
          (response/status 401))
        (response/status 500)))))
