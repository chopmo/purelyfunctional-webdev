(ns webdev.core
  (:require [webdev.item.model :as items]
            [webdev.item.handler :refer [handle-index-items
                                         handle-create-item]])
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]))

(def db "jdbc:postgresql://localhost/webdev?user=postgres&password=postgres")

(defn greet [req]
  {:status 200
   :body "Hello world2"
   :headers {}})

(defn goodbye [req]
  {:status 200
   :body "Goodbye cruel world"
   :headers {}})

(defn about [req]
  {:status 200
   :body "Created by Jacob Tjoernholm while learning about web development in Clojure using the Purely Functional lessons by Eric Normand (purelyfunctional.tv)"
   :headers {}})

(defn yo [req]
  (let [name (get-in req [:route-params :name])]
    {:status 200
     :body (str "Yo! " name "!")
     :headers {}}))

(defn calc [req]
  (let [ops {"+" +
             "-" -
             "*" *
             ":" /}
        a (Integer. (get-in req [:route-params :operand1]))
        b (Integer. (get-in req [:route-params :operand2]))
        op (ops (get-in req [:route-params :operator]))]
  (if op
    {:status 200
     :body (str (op a b))
     :headers {}}
    {:status 404
     :body "Page not found"
     :headers {}})))

(defroutes routes
  (GET "/" [] greet)
  (GET "/about" [] about)
  (GET "/calc/:operand1/:operator/:operand2" [] calc)
  (GET "/goodbye" [] goodbye)
  (GET "/yo/:name" [] yo)
  (GET "/items" [] handle-index-items)
  (POST "/items" [] handle-create-item)
  (ANY "/request" [] handle-dump)
  (not-found "Page not found"))

(defn wrap-db [hdlr]
  (fn [req]
    (hdlr (assoc req :webdev/db db))))

(defn wrap-server [hdlr]
  (fn [req]
    (let [response (hdlr req)]
      (assoc-in response [:headers "Server"] "BFG 9000"))))

(def app
  (wrap-server
   (wrap-db
    (wrap-params
     routes))))


(defn -main [port]
  (items/create-table db)
  (jetty/run-jetty app { :port (Integer. port)}))

(defn -dev-main [port]
  (items/create-table db)
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
