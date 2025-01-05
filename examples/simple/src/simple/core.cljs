(ns simple.core
  (:require [re-frame.core :as rf]
            [re-frame.uix :refer [use-subscribe]]
            [uix.core :as uix :refer [defui $]]
            [uix.dom :as dom]))

;; A detailed walk-through of this source code is provided in the docs:
;; https://day8.github.io/re-frame/dominoes-live/

;; -- Domino 1 - Event Dispatch -----------------------------------------------

(defn dispatch-timer-event
  []
  (let [now (js/Date.)]
    (rf/dispatch [:timer now])))  ;; <-- dispatch used

;; Call the dispatching function every second.
;; `defonce` is like `def` but it ensures only one instance is ever
;; created in the face of figwheel hot-reloading of this file.
(defonce do-timer (js/setInterval dispatch-timer-event 1000))

;; -- Domino 2 - Event Handlers -----------------------------------------------

(rf/reg-event-db              ;; sets up initial application state
 :initialize                 ;; usage:  (dispatch [:initialize])
 (fn [_ _]                   ;; the two parameters are not important here, so use _
   {:time (js/Date.)         ;; What it returns becomes the new application state
    :time-color "orange"}))  ;; so the application state will initially be a map with two keys

(rf/reg-event-db                ;; usage:  (dispatch [:time-color-change 34562])
 :time-color-change            ;; dispatched when the user enters a new colour into the UI text field
 (fn [db [_ new-color-value]]  ;; -db event handlers given 2 parameters:  current application state and event (a vector)
   (assoc db :time-color new-color-value)))   ;; compute and return the new application state

(rf/reg-event-db                 ;; usage:  (dispatch [:timer a-js-Date])
 :timer                         ;; every second an event of this kind will be dispatched
 (fn [db [_ new-time]]          ;; note how the 2nd parameter is destructured to obtain the data value
   (assoc db :time new-time)))  ;; compute and return the new application state

;; -- Domino 4 - Query  -------------------------------------------------------

(rf/reg-sub
 :time
 (fn [db _]     ;; db is current app state. 2nd unused param is query vector
   (:time db))) ;; return a query computation over the application state

(rf/reg-sub
 :time-color
 (fn [db _]
   (:time-color db)))

;; -- Domino 5 - View Functions ----------------------------------------------

(defui clock
  []
  (let [colour (use-subscribe [:time-color])
        time   (-> (use-subscribe [:time])
                   .toTimeString
                   (clojure.string/split " ")
                   first)]
    ($ :div.example-clock {:style #js {:color colour}} time)))

(defui color-input
  []
  (let [time-color (use-subscribe [:time-color])
        gettext (fn [e] (-> e .-target .-value))
        emit    (fn [e] (rf/dispatch [:time-color-change (gettext e)]))]
    ($ :div.color-input
       "Display color: "
       ($ :input {:type "text"
                  :style #js {:border "1px solid #CCC"}
                  :value time-color
                  :on-change emit}))))

(defui ui
  []
  ($ :div
     ($ :h1 "The time is now:")
     ($ clock)
     ($ color-input)))

;; -- Entry Point -------------------------------------------------------------

(defonce root-container
  (dom/create-root (js/document.getElementById "app")))

(defn mount-ui
  []
  (dom/render-root ($ ui) root-container))

(defn ^:dev/after-load clear-cache-and-render!
  []
  ;; The `:dev/after-load` metadata causes this function to be called
  ;; after shadow-cljs hot-reloads code. We force a UI update by clearing
  ;; the Reframe subscription cache.
  (rf/clear-subscription-cache!)
  (mount-ui))

(defn run               ;; Your app calls this when it starts. See shadow-cljs.edn :init-fn.
  []
  (rf/dispatch-sync [:initialize]) ;; put a value into application state
  (mount-ui))                      ;; mount the application's ui into '<div id="app" />'
