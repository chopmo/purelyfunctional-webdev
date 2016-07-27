(ns webdev.item.model
  (:require [clojure.java.jdbc :as db]))

(defn create-table [db]
  (db/execute!
   db
   ["CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\""])
  (db/execute!
   db
   ["CREATE TABLE IF NOT EXISTS items
       (id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        list TEXT NOT NULL,
        name TEXT NOT NULL,
        description TEXT NOT NULL,
        checked BOOLEAN NOT NULL DEFAULT FALSE,
        date_created TIMESTAMPTZ NOT NULL DEFAULT now())"]))

(defn create-item [db list name description]
  (:id (first (db/query
               db
               ["INSERT INTO items (list, name, description)
                 VALUES (?, ?, ?)
                 RETURNING id"
                list
                name
                description]))))


(defn update-item [db id checked]
  (= [1] (db/execute!
          db
          ["UPDATE items
            SET checked = ?
            WHERE id = ?"
           checked
           id])))

(defn delete-item [db id]
  (= [1] (db/execute!
          db
          ["DELETE FROM items
            WHERE id = ?"
           id])))

(defn read-items [db list]
  (db/query
   db
   ["SELECT id, list, name, description, checked, date_created
     FROM items
     WHERE list = ?
     ORDER BY date_created"
    list]))

(defn read-item [db id]
  (first
   (db/query
    db
    ["SELECT id, list, name, description, checked, date_created
      FROM items
      WHERE id = ?"
     id])))

(defn read-lists [db]
  (db/query
   db
   ["SELECT list FROM items"]))
