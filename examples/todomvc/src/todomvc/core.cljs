(ns todomvc.core
  (:require-macros [secretary.core :refer [defroute]])
  (:require [goog.events :as events]
            [re-frame.alpha :as rf :refer [dispatch dispatch-sync]]
            [re-frame.uix :refer [use-subscribe]]
            [secretary.core :as secretary]
            [todomvc.events] ;; These two are only required to make the compiler
            [todomvc.subs]   ;; load them (see docs/App-Structure.md)
            [todomvc.views]
            [uix.core :as uix :refer [defui $]]
            [uix.dom :as dom])
  (:import [goog History]
           [goog.history EventType]))

;; -- Debugging aids ----------------------------------------------------------
(enable-console-print!)   ;; so that println writes to `console.log`

;; Put an initial value into app-db.
;; The event handler for `:initialise-db` can be found in `events.cljs`
;; Using the sync version of dispatch means that value is in
;; place before we go onto the next step.
(dispatch-sync [:initialise-db])

;; -- Routes and History ------------------------------------------------------
;; Although we use the secretary library below, that's mostly a historical
;; accident. You might also consider using:
;;   - https://github.com/DomKM/silk
;;   - https://github.com/juxt/bidi
;; We don't have a strong opinion.
;;
(defroute "/" [] (dispatch [:set-showing :all]))
(defroute "/:filter" [filter] (dispatch [:set-showing (keyword filter)]))

(defonce history
  (doto (History.)
    (events/listen EventType.NAVIGATE
                   (fn [^js/goog.History.Event event] (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -- Entry Point -------------------------------------------------------------

(defonce root-container
  (dom/create-root (.getElementById js/document "app")))

(defn render
  []
  ;; Render the UI into the HTML's <div id="app" /> element
  ;; The view function `todomvc.views/todo-app` is the
  ;; root view for the entire UI.
  (dom/render-root ($ todomvc.views/todo-app) root-container))

(defn ^:dev/after-load clear-cache-and-render!
  []
  ;; The `:dev/after-load` metadata causes this function to be called
  ;; after shadow-cljs hot-reloads code. We force a UI update by clearing
  ;; the Reframe subscription cache.
  (rf/clear-subscription-cache!)
  (render))

(defn ^:export main
  []
  (render))
