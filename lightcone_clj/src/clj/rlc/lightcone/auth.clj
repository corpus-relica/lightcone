(ns rlc.lightcone.auth
  (:require [buddy.auth :refer [authenticated?]]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.backends.token :refer [jwe-backend]]
            [buddy.hashers :as hashers]
            [buddy.sign.jwt :as jwt]
            [clj-http.client :as http]))

(def base-url "http://localhost:3000")
(def login-url (str base-url "/auth/login"))

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
  (try
    (let [response (http/post login-url
                              {:form-params {:username username
                                             :password password}
                               :content-type :json
                               :accept :json
                               :throw-exceptions false
                               :as :json})]
      (tap> response)
      (if (= (:status response) 200)
        (:access_token (:body response))
        false))
    (catch Exception e
      (println "Authentication error:" (.getMessage e))
      false)))


(def secret "your-secret-key")
;; (def token-expiration-time (* 60 60)) ; 1 hour in seconds
(def token-expiration-time 60) ; 1 minute in seconds

(defn generate-token [user]
  (let [claims {:user (:username user)
                :exp (+ (System/currentTimeMillis) (* token-expiration-time 1000))}]
    (jwt/sign claims secret)))

(defn validate-token [token]
  (try
    (jwt/unsign token secret)
    (catch Exception _
      false)))

(defn is-authenticated? [headers]
  (let [header-value (get headers "authorization")
        token (when header-value (second (re-find #"Bearer\s+(.+)" header-value)))]
    (tap> "IS AUTHENTICATED")
    (tap> token)
    (when token
      (try
        (let [claims (jwt/unsign token secret)]
          (tap> "CLAIMS")
          (tap> claims)
          (tap> [(System/currentTimeMillis) "<" (:exp claims)])
          (tap> (< (System/currentTimeMillis) (:exp claims)))
          (if (< (System/currentTimeMillis) (:exp claims))
            :yes
            :token-expired))
        (catch Exception _
          :no)))))

(def session-auth-backend
  (session-backend {:unauthorized-handler (constantly {:status 401 :body "Unauthorized"})}))

(def jwt-auth-backend
  (jwe-backend {:secret secret
                :unauthorized-handler (constantly {:status 401 :body "Unauthorized"})}))

(defn wrap-session-authentication [handler]
  (wrap-authentication handler session-auth-backend))

(defn wrap-jwt-authentication [handler]
  (wrap-authentication handler jwt-auth-backend))
