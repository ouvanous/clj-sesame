(ns clj-sesame.sparql
  (:use clj-sesame.core
        clj-sesame.rdf
        clj-sesame.repository)
  (import 
    org.openrdf.repository.manager.RemoteRepositoryManager
    org.openrdf.query.TupleQuery
    org.openrdf.query.TupleQueryResult
    org.openrdf.query.QueryLanguage
    org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter
    org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter
    org.openrdf.rio.Rio
    org.openrdf.rio.RDFFormat
    org.openrdf.rio.RDFWriter))






(defn process-tuple-query
  "(process-tuple-query repository query-string my-function)"
  [repo query process]
  (with-open [conn (get-connection repo)]
    (let [tuple-query (.prepareTupleQuery conn QueryLanguage/SPARQL query)]
      (with-open [results (.evaluate tuple-query)]
        (doseq [tuple (sesame-iterator-seq results)]
          (process tuple))))))







;- ----------------------------------------------------------------------------
;- 

(defn sparql-query 
  "doc-string"
  [repo query format]
  (with-open [conn (get-connection repo)
              bs (java.io.ByteArrayOutputStream.)]
    (let [sparql-query (.prepareTupleQuery conn QueryLanguage/SPARQL query)
          writer (case format
                    :json (SPARQLResultsJSONWriter. bs)
                    :xml (SPARQLResultsXMLWriter. bs))]
      (.evaluate sparql-query writer)
      (.toString bs))))

(defn json-sparql-query
  [repo query]
  (sparql-query repo query :json))

(defn xml-sparql-query
  [repo query]
  (sparql-query repo query :xml))







;- ----------------------------------------------------------------------------
;- 

(defn graph-query
  [repo query format]
  (with-open [conn (get-connection repo)
              bs (java.io.ByteArrayOutputStream.)]
    (let [sparql-query (.prepareGraphQuery conn QueryLanguage/SPARQL query)
          writer (case format
                    :turtle (Rio/createWriter RDFFormat/TURTLE bs)
                    :xml (Rio/createWriter RDFFormat/RDFXML bs)
                    :jsonld (Rio/createWriter RDFFormat/JSONLD bs))]
      (.evaluate sparql-query writer)
      (.toString bs))))


(defn turtle-graph-query
  [repo query]
  (graph-query repo query :turtle))

(defn xml-graph-query
  [repo query]
  (graph-query repo query :xml))

(defn jsonld-graph-query
  [repo query]
  (graph-query repo query :jsonld))

;- ----------------------------------------------------------------------------
;- 


; (def repo (get-repository "http://localhost:8080/openrdf-sesame" "uuu"))
; ; (def conn (get-connection repository))




; (process-tuple-query repo q1 println)








;- ----------------------------------------------------------------------------
;- 



