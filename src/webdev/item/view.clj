(ns webdev.item.view
  (:require [hiccup.page :refer [html5]]
            [hiccup.core :refer [html h]
             ]))

(defn frontpage []
  (html
   [:h1 "Cljweb practice app"]
   [:ul
    [:li [:a {:href "/about"} "About"]]
    [:li [:a {:href "/calc/2/+/2"} "Calculator"]]
    [:li [:a {:href "/goodbye"} "Goodbye"]]
    [:li [:a {:href "/yo/Jacob"} "Greeter"]]
    [:li [:a {:href "/items"} "TODO app"]]
    ]))

(defn new-item [list]
  (html [:form.form-horizontal
         {:method "POST" :action (str "/items/" list)}
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

(defn css-class-list [& args]
  (apply str (interpose " " args)))

(defn update-item-form [id checked]
  (html
   [:form {:method "POST" :action (str "/items/" id)}
    [:input {:type :hidden
             :name "_method"
             :value "PUT"}]
    [:input {:type :hidden
             :name "checked"
             :value (str (not checked))}]
    [:div.btn-group
     [:input
      {:type :submit
       :class (css-class-list "btn"
                              "btn-xs"
                              (if checked "btn-success" "btn-warning"))
       :value (if checked "Checked" "Unchecked")}]]]))

(defn items-page [items list]
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
                    [:td (update-item-form (:id i) (:checked i))]])]]
               [:div.col-sm-offset-1 "There are no items"])]
            [:div.col-sm-6
             [:h2 "Create a new item"]
             (new-item list)]]
           [:script {:src "http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"}]
           [:script {:src "/bootstrap/js/bootstrap.min.js"}]]]))

(defn lists-page [lists]
  (html5 {:lang "en"}
         [:head
          [:title "BFG 9000 TODO lists"]
          [:meta {:name :viewport
                  :content "width=device-width, initial-scale=1.0"}]
          [:link {:href "/bootstrap/css/bootstrap.min.css"
                  :rel :stylesheet}]
          [:body
           [:h1 "Existing TODO lists"]
           (if (seq lists)
             [:ul
              (for [list (map :list lists)]
                [:li
                 [:a {:href (str "/items/" list)} list]])]
             [:div.col-sm
              [:em "There are no lists"]])

           [:form.form-horizontal
            {:method "POST"
             :action "/lists"}
            [:div.form-group
             [:div.col-sm.col-sm-10
              [:input
               {:type :text
                :placeholder "New list name"
                :name "list"}]]]
            [:div.form-group
             [:div.col-sm.col-sm-10
              [:input.btn.btn-primary
               {:type :submit
                :value "New list"}]]]]]]))
