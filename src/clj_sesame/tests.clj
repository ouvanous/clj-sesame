(ns clj-sesame.tests 
  (:require [clj-sesame.repository :as repository]
            [clj-sesame.sparql :as sparql]
            [clj-sesame.rdf :as rdf]))





(def repo (repository/get-remote-repository "http://localhost:8585/openrdf-sesame/" "sam"))
(def vf (repository/get-value-factory repo))
(def context (rdf/create-uri vf "http://context"))

; (set-namespace repo :rapex "http://rapex.ouvanous.com/")
; (set-namespace repo :rapexVocab "http://rapex.ouvanous.com/vocabulary/#")
; (set-namespace repo :schema "http://schema.org/")
; (set-namespace repo "schema" "http://schema.org/")
; (set-namespace repo :prov "http://www.w3.org/ns/prov#")


(def prefixes-string (sparql/get-prefixes-string (repository/get-namespaces repo) {:test "http://test.com/"}))

; (rdf/add-uri repo "http://rapex.ouvanous.com/report" :rdfa context)





; (println (add-uri repo "http://rapex.ouvanousg.com/report" :rdfa))
; (println (all-contexts-size repo))
; (println (get-context-ids repo))
; (println (sparql/prefix-query prefixes-string construct-query-1) )

(println (sparql/n3-graph repo (sparql/prefix-query prefixes-string "
  CONSTRUCT {
    ?s ?p ?o.
  } 
  WHERE 
  {
    ?s a rapexVocab:Report .
    ?s ?p ?o.
  }
  ")))