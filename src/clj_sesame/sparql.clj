(ns clj-sesame.sparql
  (:use clj-sesame.core
        clj-sesame.rdf
        clj-sesame.repository
        [clojure.string :only (join)])
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
    org.openrdf.repository.RepositoryException
    (org.openrdf.rio.helpers JSONLDMode JSONLDSettings BasicWriterSettings)))







(defn- bindingset-to-set
  [bs]
  (reduce #(assoc %1 (keyword (.getName %2)) (str (.getValue %2)) ) {} bs))


;- ----------------------------------------------------------------------------
;- 

(defn select 
  "sparql select"
  ([repo query]
    (with-open [conn (get-connection repo)
                bs (java.io.ByteArrayOutputStream.)]
      (let [sparql-query (.prepareTupleQuery conn QueryLanguage/SPARQL query)]
        (with-open [results (.evaluate sparql-query)]
          (doall (map bindingset-to-set (sesame-iterator-seq results)))))))
  ([repo query format]
    (with-open [conn (get-connection repo)
                bs (java.io.ByteArrayOutputStream.)]
      (let [sparql-query (.prepareTupleQuery conn QueryLanguage/SPARQL query)
            writer (case format
                      :json (SPARQLResultsJSONWriter. bs)
                      :xml (SPARQLResultsXMLWriter. bs))]
        (.evaluate sparql-query writer)
        (.toString bs)))))

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

(defn get-writer 
  [format bs]
  (case format
    :turtle (Rio/createWriter RDFFormat/TURTLE bs)
    :n3     (Rio/createWriter RDFFormat/N3 bs)
    :xml    (Rio/createWriter RDFFormat/RDFXML bs)
    :jsonld (Rio/createWriter RDFFormat/JSONLD bs)))


(defn graph-query
  [repo query format]
  (with-open [conn (get-connection repo)
              bs (java.io.ByteArrayOutputStream.)]
    (let [sparql-query (.prepareGraphQuery conn QueryLanguage/SPARQL query)
          writer (get-writer format bs)
          conf (.getWriterConfig writer)]
      (case format
        :jsonld (do 
                  (.set conf JSONLDSettings/COMPACT_ARRAYS true)
                  (.set conf JSONLDSettings/JSONLD_MODE JSONLDMode/COMPACT))
        nil)
      (.evaluate sparql-query writer)
      (.toString bs))))


(defn turtle-graph
  [repo query]
  (graph-query repo query :turtle))

(defn xml-graph
  [repo query]
  (graph-query repo query :xml))

(defn n3-graph
  [repo query]
  (graph-query repo query :n3))

(defn jsonld-graph
  [repo query]
  (graph-query repo query :jsonld))






(defn get-prefixes-string
  [& prefixes]
  (let [p (reduce #(merge % %2) {} prefixes)]
    (join "\n" (map (fn [[k v]] (str "PREFIX " (name k) ": <" v "> ")) p))))
  


(defn prefix-query
  [prefix-string query]
  (str prefix-string query))


