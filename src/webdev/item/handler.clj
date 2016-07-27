(ns webdev.item.handler
  (:require [webdev.item.model :refer [create-item
                                       read-lists
                                       read-items
                                       read-item
                                       update-item
                                       delete-item]]
            [webdev.item.view :refer [frontpage
                                      items-page
                                      lists-page]]))

(defn handle-frontpage [req]
  {:status 200
   :headers {}
   :body (frontpage)})

(defn handle-index-lists [req]
  (let [db (:webdev/db req)
        lists (read-lists db)]
    {:status 200
     :headers {}
     :body (lists-page lists)}))

(defn handle-create-list [req]
  (let [list (get-in req [:form-params "list"])]
    {:status 302
     :headers {"Location" (str "/items/" list)}
     :body ""}))

(defn handle-index-items [req]
  (let [db (:webdev/db req)
        list (get-in req [:route-params :list])
        items (read-items db list)]
    {:status 200
     :headers {}
     :body (items-page items list)}))

(defn handle-create-item [req]
  (let [name (get-in req [:params "name"])
        description (get-in req [:params "description"])
        db (:webdev/db req)
        list (get-in req [:route-params :list])
        item-id (create-item db list name description)]
    {:status 302
     :headers {"Location" (str "/items/" list)}
     :body ""}))

(defn handle-delete-item [req]
  (let [db (:webdev/db req)
        item-id (java.util.UUID/fromString (get-in req [:route-params :id]))
        item (read-item db item-id)]
    (if (and item (delete-item db item-id))
      {:status 302
       :headers {"Location" (str "/items/" (:list item))}
       :body ""}
      {:status 404
       :headers {}
       :body "Item not found"})))

(defn handle-update-item [req]
  (let [db (:webdev/db req)
        item-id (java.util.UUID/fromString (get-in req [:route-params :id]))
        item (read-item db item-id)
        checked? (Boolean. (get-in req [:form-params "checked"]))]
    (if (and item (update-item db item-id checked?))
      {:status 302
       :headers {"Location" (str "/items/" (:list item))}
       :body ""}
      {:status 404
       :headers {}
       :body "Item not found"})))
