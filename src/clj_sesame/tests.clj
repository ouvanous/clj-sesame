(ns clj-sesame.tests 
  (:use clj-sesame.core
        clj-sesame.rdf
        clj-sesame.repository)
  (:require [clj-sesame.sparql :as sparql]))





(def repo (create-mem-repository))

; (def repo (create-mem-repository))
(def vf (get-value-factory repo))
(def context (create-uri vf "http://context"))
(add-uri repo "http://dbpedia.org/data/Scots_Pine.rdf" :xml context)


; (println (add-uri repo "http://rapex.ouvanous.com/report" :rdfa))
; (println (all-contexts-size repo))
; (println (get-context-ids repo))


(println (sparql/select repo "SELECT * WHERE {GRAPH ?g {?s ?p ?o}}" :json))