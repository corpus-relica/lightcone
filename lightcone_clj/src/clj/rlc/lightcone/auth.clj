(ns rlc.lightcone.auth
  (:require [buddy.auth :refer [authenticated?]]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.backends.token :refer [jwe-backend]]
            [buddy.hashers :as hashers]
            [buddy.sign.jwt :as jwt]))

(def users (atom []))

(defn db-create-user! [user]
  (tap> "CREATE USER")
  (tap> [@users user])
  (swap! users conj user))

(defn db-get-user-by-username [username]
  (first (filter #(= username (:username %)) @users)))

(defn create-user [username password]
  ;; Hash the password
  (tap> "CREATE USER")
  (tap> username)
  (tap> password)
  (let [hashed-password (hashers/derive password)]
    ;; Store the user in the database
    (db-create-user! {:username username :password hashed-password})))

(defn authenticate-user [username password]
  ;; Retrieve the user from the database
  (if-let [user (db-get-user-by-username username)]
    ;; Verify the password
    (if (hashers/check password (:password user))
      user
      false)
    false))



(def secret "your-secret-key")

(defn generate-token [user]
  (let [claims {:user (:username user)}]
    (jwt/sign claims secret)))

(defn validate-token [token]
  (try
    (jwt/unsign token secret)
    (catch Exception _
      false)))



(def session-auth-backend
  (session-backend {:unauthorized-handler (constantly {:status 401 :body "Unauthorized"})}))

(def jwt-auth-backend
  (jwe-backend {:secret secret
                :unauthorized-handler (constantly {:status 401 :body "Unauthorized"})}))

(defn wrap-session-authentication [handler]
  (wrap-authentication handler session-auth-backend))

(defn wrap-jwt-authentication [handler]
  (wrap-authentication handler jwt-auth-backend))
