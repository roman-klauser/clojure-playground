(defproject clojure-simple-http "0.1.0-SNAPSHOT"
  :author "Divyum Rastogi"
  :description "A simple HTTP server"
  :min-lein-version "2.7.1"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [http-kit "2.2.0"]
                 [compojure "1.6.0"]
                 [com.novemberain/monger "3.1.0"]
                 [clojure-csv "2.0.2"]
                 [org.clojure/data.json "0.2.6"]
                 [jumblerg/ring-cors "2.0.0"]]
  :main clojure-simple-http.core)
