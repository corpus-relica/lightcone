(ns rlc.lightcone.routes.person
  (:require [compojure.core :refer [defroutes context GET POST PUT DELETE]]
            [ring.util.response :as response]
            [rlc.lightcone.contacts :as contacts]))

(defroutes person-routes
  (context "/api/person" []
    (GET "/" []
         (let [foo (map (fn [x] {:id (:uid x)
                                           :name (:name x)})
                        (:body (contacts/get-people)))]
           (tap> "RESPONSE FROM PERSONS API:")
            (tap> foo)
           (response/response {:person foo}))
         )

    (GET "/:id" [id]
      (response/response {:person {:id id}}))))
