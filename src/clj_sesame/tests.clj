(ns clj-sesame.tests 
  (:use clj-sesame.core
        clj-sesame.rdf
        clj-sesame.repository 
        clj-sesame.sparql))



(def q1 "SELECT * WHERE { GRAPH ?g  { ?s ?p ?o }}")
(def q2 "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }")
(def q3 "ASK { ?s ?p ?o } WHERE { ?s ?p ?o }")
; (def repo (get-remote-repository "http://localhost:8080/openrdf-sesame" "uuu"))

(def repo (create-mem-repository))
(def vf (get-value-factory repo))

; creates values 
(def context (create-uri vf "http://context"))
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
; (add-rdf-file repo "./test.ttl" :turtle context)
; (add-rdf-uri repo "http://opentox.org/data/documents/development/RDF%20files/ExampleTURTLE/at_download/file" :turtle context)
; (add-rdf-file repo "./provenance-ontology-test1.n3" :n3 context)
; (add-rdf-uri repo "http://dbpedia.org/data/Scots_Pine.n3" :n3 context)
; (add-rdf-file repo "./Scots_Pine.rdf" :xml context)
; (add-rdf-uri repo "http://dbpedia.org/data/Scots_Pine.rdf" :xml context)
; (add-n3-file repo "./test.n3" context)
; (add-jsonld-file repo "./test.json" context)

; sparql query 
(println (json-sparql-query repo q1))
; (println (xml-sparql-query repo q1))
; ; graph query
; (println (turtle-graph-query repo q2))
; (println (xml-graph-query repo q2))
; (println (jsonld-graph-query repo q2))



