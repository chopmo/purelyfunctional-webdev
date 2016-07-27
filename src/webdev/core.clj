(ns webdev.core
  (:require [webdev.item.model :as items]
            [webdev.item.handler :refer [handle-frontpage
                                         handle-index-items
                                         handle-create-item
                                         handle-delete-item
                                         handle-update-item]])
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.core :refer [defroutes ANY GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]))

(def db (or
         (System/getenv "DATABASE_URL")
         "jdbc:postgresql://localhost/webdev?user=postgres&password=postgres"))

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
  (GET "/" [] handle-frontpage)
  (GET "/about" [] about)
  (GET "/calc/:operand1/:operator/:operand2" [] calc)
  (GET "/goodbye" [] goodbye)
  (GET "/yo/:name" [] yo)
  (GET "/items" [] handle-index-items)
  (POST "/items" [] handle-create-item)
  (DELETE "/items/:id" [] handle-delete-item)
  (PUT "/items/:id" [] handle-update-item)
  (ANY "/request" [] handle-dump)
  (not-found "Page not found"))

(defn wrap-db [hdlr]
  (fn [req]
    (hdlr (assoc req :webdev/db db))))

(defn wrap-server [hdlr]
  (fn [req]
    (let [response (hdlr req)]
      (assoc-in response [:headers "Server"] "BFG 9000"))))

(def sim-methods
  {"POST" :post
   "DELETE" :delete})

(defn wrap-simulated-methods [hdlr]
  (fn [req]
    (if-let [method (and (= :post (:request-method req))
                         (sim-methods (get-in req [:form-params "_method"])))]
      (hdlr (assoc req :request-method method))
      (hdlr req))))

(def app
  (wrap-server
   (wrap-file-info
    (wrap-resource
     (wrap-db
      (wrap-params
       (wrap-simulated-methods
        routes)))
     "static"))))


(defn -main [port]
  (items/create-table db)
  (jetty/run-jetty app { :port (Integer. port)}))

(defn -dev-main [port]
  (items/create-table db)
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
