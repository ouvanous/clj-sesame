(ns clj-sesame.tests 
  (:use clj-sesame.core
        clj-sesame.rdf
        clj-sesame.repository)
  (:require [clj-sesame.sparql :as sparql]))





(def repo (create-mem-repository))
(def vf (get-value-factory repo))
(def context (create-uri vf "http://context"))

(add-uri repo "http://rapex.ouvanous.com/report" :rdfa context)


(def construct-query-1 "
  CONSTRUCT {
    ?s a <http://schema.org/Product>;
       <http://rapex.ouvanous.com/vocabulary/#productName> ?o.
  } 
  WHERE 
  {
    ?s a <http://schema.org/Product>;
       <http://rapex.ouvanous.com/vocabulary/#productName> ?o.
  }
  ")

; (println (add-uri repo "http://rapex.ouvanousg.com/report" :rdfa))
; (println (all-contexts-size repo))
; (println (get-context-ids repo))


(println (sparql/jsonld-graph repo construct-query-1))