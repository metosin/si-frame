(ns re-frame.flow.demo
  (:require [zprint.core :as zp]
            [clojure.string :as str]
            [re-frame.alpha :as rf]
            [re-frame.alpha.uix :refer [use-subscribe use-sub]]
            [re-frame.uix :refer [use-reactive-node]]
            [re-frame.db :refer [app-db]]
            [uix.core :as uix :refer [defui $]]
            [uix.dom :as dom]))

(defui debug-app-db []
  ($ :pre {:style #js {:position "absolute"
                       :bottom   0
                       :right    0
                       :fontSize 8}}
     (some-> (use-reactive-node app-db)
             (zp/zprint-str {:style :justified})
             (str/replace #"re-frame.flow.demo/" ":")
             (str/replace #"re-fine." ":"))))

(rf/reg-sub ::items :-> (comp reverse ::items))

(defui item [{:keys [id]}]
  ($ :div "Item " id))

(defui items []
  (let [item-ids (use-subscribe [::items])]
    ($ :div
       (for [id item-ids]
         ($ item {:key id
                  :id id})))))

(rf/reg-event-db
 ::clear-all
 (fn [db _] (dissoc db ::items)))

(rf/reg-event-db
 ::add-item
 (fn [db [_ id]] (update db ::items conj id)))

(rf/reg-event-db
 ::delete-item
 (fn [db [_ id]] (update db ::items #(remove #{id} %))))

(defui controls []
  (let [[id set-id] (uix/use-state 0)]
    ($ :div
       ($ :button {:on-click #(do (rf/dispatch [::add-item (inc id)])
                                  (set-id inc))} "Add")
       " "
       ($ :button {:on-click #(do (rf/dispatch [::delete-item id])
                                  (set-id dec))} "Delete")
       " "
       ($ :button {:on-click #(do (rf/dispatch [::clear-all])
                                  (set-id 0))} "Clear")
       " ")))

(def error-state-flow
  {:id ::error-state
   :path [::error-state]
   :inputs {:items [::items]}
   :output (fn [{:keys [items]}]
             (cond
               (> (count items) 2) :too-many
               (empty? items)      :none))
   :live-inputs {:items [::items]}
   :live? (fn [{:keys [items]}]
            (let [ct (count items)]
              (or (zero? ct) (> ct 3))))})

(rf/reg-flow error-state-flow)

(rf/reg-event-fx
 ::clear-flow
 (fn [_ _] {:fx [[:clear-flow ::error-state]]}))

(rf/reg-event-fx
 ::reg-flow
 (fn [_ _] {:fx [[:reg-flow error-state-flow]]}))

(defui flow-controls []
  ($ :div
     ($ :button {:on-click #(do (rf/dispatch [::clear-flow]))}
        "Clear flow")
     " "
     ($ :button {:on-click #(do (rf/dispatch [::reg-flow]))}
        "Register flow")))

(defui warning []
  (let [error-state (use-subscribe [:flow {:id ::error-state}])]
    ($ :div {:style #js {:color "red"}}
     (->> error-state
          (get {:too-many "Warning: only the first 3 items will be used."
                :none     "No items. Please add one."})))))

(defui root []
  ($ :div ($ controls) ($ flow-controls) ($ warning) ($ items) ($ debug-app-db)))

(rf/reg-event-db
 ::init
 (fn [db _] db))

(defonce root-container
  (dom/create-root (js/document.getElementById "app")))

(defn run
  []
  (rf/dispatch-sync [::init])
  (dom/render-root ($ root) root-container))
