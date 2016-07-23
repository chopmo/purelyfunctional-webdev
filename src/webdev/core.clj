(ns webdev.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [clojure.pprint :refer [pprint]]))

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

(defn request [req]
  {:status 200
   :body (with-out-str (pprint req))
   :header {}})

(defroutes app
  (GET "/" [] greet)
  (GET "/about" [] about)
  (GET "/goodbye" [] goodbye)
  (GET "/request" [] request)
  (not-found "Page not found"))

(defn -main [port]
  (jetty/run-jetty app { :port (Integer. port)}))

(defn -dev-main [port]
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
