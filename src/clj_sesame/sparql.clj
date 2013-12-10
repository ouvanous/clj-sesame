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
    org.openrdf.rio.RDFWriter
    org.openrdf.repository.RepositoryException))






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

(defn select 
  "sparql select"
  [repo query format]
  (with-open [conn (get-connection repo)
              bs (java.io.ByteArrayOutputStream.)]
    (let [sparql-query (.prepareTupleQuery conn QueryLanguage/SPARQL query)
          writer (case format
                    :json (SPARQLResultsJSONWriter. bs)
                    :xml (SPARQLResultsXMLWriter. bs))]
      (.evaluate sparql-query writer)
      (.toString bs))))

(defn json-select
  "helper to get results as json"
  [repo query]
  (select repo query :json))

(defn xml-select
  "helper to get results as rdfxml"
  [repo query]
  (select repo query :xml))






;- ----------------------------------------------------------------------------
;- 

(defn update 
  [repo query]
  (with-open [conn (get-connection repo)]
    (try 
      (.begin conn)
      (let [update-query (.prepareUpdate conn QueryLanguage/SPARQL query)] 
        (.execute update-query))
      (.commit conn)
      (catch RepositoryException e
        (.rollback conn)))))






;- ----------------------------------------------------------------------------
;- 

(defn ask
  [repo query]
  (with-open [conn (get-connection repo)]
    (let [ask-query (.prepareBooleanQuery conn QueryLanguage/SPARQL query)]
      (.evaluate ask-query))))






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


(defn turtle-graph
  [repo query]
  (graph-query repo query :turtle))

(defn xml-graph
  [repo query]
  (graph-query repo query :xml))

(defn jsonld-graph
  [repo query]
  (graph-query repo query :jsonld))



