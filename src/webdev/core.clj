(ns webdev.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]))

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
  (let [o1 (Integer. (get-in req [:route-params :operand1]))
        o2 (Integer. (get-in req [:route-params :operand2]))
        op (eval (get-in req [:route-params :operator]))
        result (case op
                 "+" (+ o1 o2)
                 "-" (- o1 o2)
                 "*" (* o1 o2)
                 ":" (/ o1 o2))]
    {:status 200
     :body (str result)
     :headers {}}))

(defroutes app
  (GET "/" [] greet)
  (GET "/about" [] about)
  (GET "/calc/:operand1/:operator/:operand2" [] calc)
  (GET "/goodbye" [] goodbye)
  (GET "/request" [] handle-dump)
  (GET "/yo/:name" [] yo)
  (not-found "Page not found"))

(defn -main [port]
  (jetty/run-jetty app { :port (Integer. port)}))

(defn -dev-main [port]
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
