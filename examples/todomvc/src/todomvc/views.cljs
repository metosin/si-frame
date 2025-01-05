(ns todomvc.views
  (:require [re-frame.alpha :refer [subscribe dispatch sub]]
            [re-frame.alpha.uix :refer [use-subscribe use-sub]]
            [uix.core :as uix :refer [defui $]]
            [clojure.string :as str]))

(defui todo-input [{:keys [title on-save on-stop] :as props}]
  (let [[val set-val] (uix/use-state (or title ""))
        stop #(do (set-val "")
                  (when on-stop (on-stop)))
        save #(let [v (-> val str str/trim)]
                (on-save v)
                (stop))]
    ($ :input (merge (dissoc props :on-save :on-stop :title)
                     {:type        "text"
                      :value       val
                      :auto-focus  true
                      :on-blur     save
                      :on-change   #(set-val (-> % .-target .-value))
                      :on-key-down #(case (.-which %)
                                      13 (save)
                                      27 (stop)
                                      nil)}))))

(defui todo-item
  [{:keys [todo]}]
  (let [{:keys [id done title]} todo
        [editing set-editing] (uix/use-state false)]
    ($ :li {:class (str (when done "completed ")
                        (when editing "editing"))}
       ($ :div.view
          ($ :input.toggle
             {:type "checkbox"
              :checked done
              :on-change #(dispatch [:toggle-done id])})
          ($ :label
             {:on-double-click #(set-editing true)}
             title)
          ($ :button.destroy
             {:on-click #(dispatch [:delete-todo id])}))
       (when editing
         ($ todo-input
            {:class "edit"
             :title title
             :on-save #(if (seq %)
                         (dispatch [:save id %])
                         (dispatch [:delete-todo id]))
             :on-stop #(set-editing false)})))))

(defui task-list
  []
  (let [visible-todos (use-subscribe [:visible-todos])
        all-complete? (use-subscribe [:all-complete?])]
    ($ :section#main
       ($ :input#toggle-all
          {:type "checkbox"
           :checked all-complete?
           :on-change #(dispatch [:complete-all-toggle])})
       ($ :label
          {:for "toggle-all"}
          "Mark all as complete")
       ($ :ul#todo-list
          (for [todo visible-todos]
            ($ todo-item {:key (:id todo)
                          :todo todo}))))))

(defui footer-controls
  []
  (let [[active done] (use-subscribe [:footer-counts])
        showing       (use-subscribe [:showing])
        a-fn          (fn [filter-kw txt]
                        ($ :a {:class (when (= filter-kw showing) "selected")
                               :href (str "#/" (name filter-kw))} txt))]
    ($ :footer#footer
       ($ :span#todo-count
          ($ :strong active)
          " "
          (case active 1 "item" "items")
          " left")
       ($ :ul#filters
          ($ :li (a-fn :all    "All"))
          ($ :li (a-fn :active "Active"))
          ($ :li (a-fn :done   "Completed")))
       (when (pos? done)
         ($ :button#clear-completed {:on-click #(dispatch [:clear-completed])}
            "Clear completed")))))

(defui task-entry
  []
  ($ :header#header
     ($ :h1 "todos")
     ($ todo-input
        {:id "new-todo"
         :placeholder "What needs to be done?"
         :on-save #(when (seq %)
                     (dispatch [:add-todo %]))})))

(defui alpha []
  (let [alpha? (use-sub :alpha?)]
    ($ :a {:href "#"
           :style #js {:color (if alpha? "red" "gray")}
           :on-click #(dispatch [:toggle-alpha])}
     (if alpha? "alpha is running!" "try alpha?"))))

(defui todo-app
  []
  ($ :<>
     ($ alpha)
     ($ :section#todoapp
        ($ task-entry)
        (when (seq (use-subscribe [:todos]))
          ($ task-list))
        ($ footer-controls))
     ($ :footer#info
        ($ :p "Double-click to edit a todo"))))
