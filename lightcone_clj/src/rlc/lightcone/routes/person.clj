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
    ;; Create a person
    (POST "/" {body :body}
          (with-auth (fn [token]
                       (let [result (contacts/create-person token body)
                             person (:body result)]
                         (response/response {:person person})))))

    ;; Update a person
    (PUT "/:id" {params :params body :body}
          (with-auth (fn [token]
                        (let [result (contacts/update-person token body)]
                          (response/response result)))))

    ;; Delete a person
    (DELETE "/:id" [id]
          (with-auth (fn [token]
                       (tap> "DELETING PERSON")
                       (tap> id)
                       (let [result (contacts/delete-person token id)]
                         (response/response result))
                       )))
    ))
