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
         (with-auth #(response/response {:person {:id id}})))))
