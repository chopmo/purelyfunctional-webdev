(ns webdev.item.view
  (:require [hiccup.page :refer [html5]]
            [hiccup.core :refer [html h]
             ]))


(defn new-item []
  (html [:form.form-horizontal
         {:method "POST" :action "/items"}
         [:div.form-group
          [:label.control-label.col-sm-2 {:for :name-input}
           "Name"]
          [:div.col-sm-10
           [:input#name-input.form-control
            {:name :name
             :placeholder "Name"}]]]
         [:div.form-group
          [:label.control-label.col-sm-2 {:for :desc-input}
           "Description"]
          [:div.col-sm-10
           [:input#desc-input.form-control
            {:name :description
             :placeholder "Description"}]]]
         [:div.form-group
          [:div.col-sm-offset-2.col-sm-10
           [:input.btn.btn-primary
            {:type :submit
             :value "New item"}]]]]))

(defn delete-item-form [id]
  (html
   [:form {:method "POST" :action (str "/items/" id)}
    [:input {:type :hidden
             :name "_method"
             :value "DELETE"}]
    [:div.btn-group
     [:input.btn.btn-danger.btn-xs
      {:type :submit
       :value "Delete"}]]]))

(defn update-item-form [item]
  (html
   (let [checked? (:checked item)]
     [:form {:method "POST" :action (str "/items/" (:id item))}
      [:input {:type :hidden
               :name "_method"
               :value "PUT"}]
      [:input {:type :hidden
               :name "checked"
               :value (if checked? "false" "true")}]
      [:div.btn-group
       [:input
        {:type :submit
         :class (apply str (interpose " " (conj ["btn" "btn-xs" (if checked? "btn-success" "btn-warning")])))
         :value (if checked? "Checked" "Unchecked")}]]])))

(defn items-page [items]
  (html5 {:lang "en"}
         [:head
          [:title "BFG 9000 TODO list"]
          [:meta {:name :viewport
                  :content "width=device-width, initial-scale=1.0"}]
          [:link {:href "/bootstrap/css/bootstrap.min.css"
                  :rel :stylesheet}]
          [:body
           [:div.container
            [:h1 "My Items"]
            [:div.row
             (if (seq items)
               [:table.table.table.striped
                [:thead
                 [:tr
                  [:th.col-sm-2]
                  [:th "Name"]
                  [:th "Description"]]]
                [:tbody
                 (for [i items]
                   [:tr
                    [:td (delete-item-form (:id i))]
                    [:td (:name i)]
                    [:td (:description i)]
                    [:td (update-item-form i)]])]]
               [:div.col-sm-offset-1 "There are no items"])]
            [:div.col-sm-6
             [:h2 "Create a new item"]
             (new-item)]]
           [:script {:src "http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"}]
           [:script {:src "/bootstrap/js/bootstrap.min.js"}]]]))
