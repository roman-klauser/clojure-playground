(ns clojure-simple-http.core
  (:require [org.httpkit.server :refer [run-server]]
            [clj-time.core :as t]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [monger.core :as mg]
            [monger.collection :as mc]
            [clojure-csv.core :as csv]
            [clojure.data.json :as json]
            [jumblerg.middleware.cors :refer [wrap-cors]]
            )
  (:import [com.mongodb MongoOptions ServerAddress])
  (:gen-class))


(defn csv-to-sets
  [file-name]
  (let [
        imports (csv/parse-csv (slurp file-name) :delimiter \;)
        transfer-keys (first imports)
        transfer-data (drop 1 imports)]
    (map (fn [transfer-data-set]
           (reduce conj
                   (map #(hash-map (keyword (first %)) (second %))
                        (map vector transfer-keys transfer-data-set))))
         transfer-data)))

(defn get-transfers
  []
  (let [^MongoOptions opts (mg/mongo-options {:threads-allowed-to-block-for-connection-multiplier 300})
        ^ServerAddress sa (mg/server-address "127.0.0.1" 27017)
        conn (mg/connect sa opts)
        db (mg/get-db conn "mydb")]
    (mc/find-maps db "documents")))

(defn add-record
  [record]
  (let [^MongoOptions opts (mg/mongo-options {:threads-allowed-to-block-for-connection-multiplier 300})
        ^ServerAddress sa (mg/server-address "127.0.0.1" 27017)
        conn (mg/connect sa opts)
        db (mg/get-db conn "mydb")]
    (mc/insert db "documents" record)
    "done"))

; define routes.
(defroutes my-routes
           (GET "/" [] "<h1>Welcome</h1>")
           (GET "/foo" [] "foo")
           (GET "/data" [] (json/write-str (map (fn [t] (update t :_id (fn [id] (.toString id)))) (get-transfers))))
           (GET "/test" [] (json/write-str (csv-to-sets "export.csv")))
           (POST "/add" req (add-record req))
           (route/not-found "<h1>Page - NOT - found</h1>"))


; starting point of server.
(defn -main [& args]
  (run-server (wrap-cors my-routes #".*") {:port 8080})
  (println "Server started on port 8080"))

