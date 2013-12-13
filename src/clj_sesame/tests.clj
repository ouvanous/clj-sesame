(ns clj-sesame.tests 
  (:use clj-sesame.core
        
        clj-sesame.repository)
  (:require [clj-sesame.sparql :as sparql]
            [clj-sesame.rdf :as rdf]))





(def repo (get-remote-repository "http://localhost:8585/openrdf-sesame/" "sam"))
(def vf (get-value-factory repo))
(def context (rdf/create-uri vf "http://context"))

; (set-namespace repo :rapex "http://rapex.ouvanous.com/")
; (set-namespace repo "schema" "http://schema.org/")
(println(get-namespaces repo))
; (rdf/add-uri repo "http://rapex.ouvanous.com/report" :rdfa context)

(def construct-query-1 "
  prefix rapex: <http://rapex.ouvanous.com/>
  prefix rapexVocab: <http://rapex.ouvanous.com/vocabulary/#>
  prefix schema: <http://schema.org/>
  prefix prov: <http://www.w3.org/ns/prov#>
  CONSTRUCT {
    rapex:report ?p ?o.
  } 
  WHERE 
  {
    rapex:report ?p ?o.
  }
  ")


; (println (add-uri repo "http://rapex.ouvanousg.com/report" :rdfa))
; (println (all-contexts-size repo))
; (println (get-context-ids repo))


(def results (sparql/n3-graph repo construct-query-1))
(println results)
