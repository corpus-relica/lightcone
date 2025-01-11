(ns rlc.lightcone.routes.person
  (:require [compojure.core :refer [defroutes context GET POST PUT DELETE]]
            [ring.util.response :as response]
            [rlc.lightcone.io.contacts :as contacts]
            [rlc.lightcone.middleware.auth :refer [with-auth]]))

(defroutes person-routes
  (context "/api/persons" []
    (GET "/" []
         (with-auth #(do
                        (let [foo (map (fn [x] {:id (:uid x)
                                                :name (:name x)})
                                       (:body (contacts/get-people %)))]
                          (response/response {:persons foo})))))
    (GET "/:id" [id]
         (with-auth #(response/response {:person {:id id}}))))

  (context "/api/person" []
    (POST "/" {body :body}
          (with-auth (fn [token]  ; or just [%] if you prefer
                       (tap> "CREATE PERSON")
                       (tap> body)
                       (let [result (contacts/create-person token body)
                             person (:body result)]
                         (tap> "CREATED PERSON")
                         (tap> person)
                         (response/response {:person person})))))

    (PUT "/:id" {params :params body :body}
          (with-auth (fn [auth-arg]
                       (tap> "UPDATE PERSON")
                       (tap> body)
                       (response/response {:person body}))))

    (DELETE "/:id" [id]
          (with-auth (fn [auth-arg]
                       (tap> "DELETE PERSON")
                       (tap> id)
                       (response/response {:person {:id id}}))))
    ))
