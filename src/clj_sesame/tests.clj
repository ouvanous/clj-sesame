(ns clj-sesame.tests 
  (:use clj-sesame.core
        clj-sesame.rdf
        clj-sesame.repository)
  (:require [clj-sesame.sparql :as sparql]))



(def q1 "SELECT * WHERE { GRAPH ?g  { ?s ?p ?o }}")
(def q2 "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }")
(def q3 "ASK { ?s ?p ?s }")
(def q4 "INSERT DATA { GRAPH <http://context> {<http://sam/r1> <http://sam/p1> <http://sam/r2> }}")

(def repo (get-remote-repository "http://localhost:8080/openrdf-sesame" "uuu"))

; (def repo (create-mem-repository))
; (def vf (get-value-factory repo))

; creates values 
(def context (create-uri vf "http://context"))
(def context-2 (create-uri vf "http://context-2"))
(def uri-1 (create-uri vf "http://sam"))
(def uri-2 (create-uri vf "http://sam/" "label"))
(def lit-1 (create-literal vf "i am a literal" "en"))
; (println uri-1 uri-2 lit-1 context)
(def stat-1 (create-statement vf uri-1 uri-2 lit-1))
(def stat-2 (create-statement vf uri-1 uri-2 uri-1))
; (println stat-1)
; (add-statement repo uri-1 uri-2 lit-1 context)
; (add-statement repo stat-1 context)
; (add-statements repo [stat-1 stat-2] context)


; ; add files 
; (add-rdf-file repo "./test-files/test.ttl" :turtle context)
; (add-rdf-file repo "./test-files/test.ttl" :turtle context-2)

; (context-size repo [context context-2])
; (add-rdf-uri repo "http://opentox.org/data/documents/development/RDF%20files/ExampleTURTLE/at_download/file" :turtle context)
; (add-rdf-file repo "./test-files/provenance-ontology-test1.n3" :n3 context)
; (add-rdf-uri repo "http://dbpedia.org/data/Scots_Pine.n3" :n3 context)
; (add-rdf-file repo "./test-files/Scots_Pine.rdf" :xml context)
; (add-rdf-uri repo "http://dbpedia.org/data/Scots_Pine.rdf" :xml context)
; (add-n3-file repo "./test-files/test.n3" context)
; (add-jsonld-file repo "./test-files/test.json" context)

; sparql query 
; (println (json-sparql-query repo q1))
; (println (xml-sparql-query repo q1))
; ; graph query
; (println (turtle-graph-query repo q2))
; (println (xml-graph-query repo q2))
; (println (jsonld-graph-query repo q2))
; (clear-context repo context)
; (clear-context repo [context context-2])

; (remove-statement repo uri-1 nil nil context)

; ; namespaces
; (set-namespace repo :sam "http://sam/")
; (set-namespaces repo {:sam "http://sam/"
;                       :test2 "http://test2/"})
; (println (get-namespaces repo))
; (update-query repo q4)
; (println (ask-query repo q3))
; (println (json-sparql-query repo q1))



(def http-repo (http-repository "http://ec.europa.eu/semantic_webgate/query/"))
(def q6 "SELECT {?s ?p ?o } WHERE { ?s ?p ?o} limit 10")

(sparql-query http-repo q6 :json)