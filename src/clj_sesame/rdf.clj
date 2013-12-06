(ns clj-sesame.rdf
  (:use clj-sesame.core
        clj-sesame.repository)
  (import org.openrdf.repository.RepositoryException
          (org.openrdf.model Resource Statement)
          info.aduna.iteration.Iterations
          org.openrdf.model.impl.LinkedHashModel
          org.openrdf.rio.Rio
          org.openrdf.rio.RDFFormat))


(defn get-contexts
  [context]
  (if (= clojure.lang.PersistentVector (type context))
    (into-array Resource context)
    (into-array Resource [context])))



(defn subject 
  [statement]
  (.getSubject statement))


(defn predicate 
  [statement]
  (.getPredicate statement))


(defn object
  [statement]
  (.getObject statement))


(defn flat-statement
  [statement]
  [(subject statement) (predicate statement) (object statement) (.getContext statement)])


(defn create-uri
  ([value-factory uri-string]
    (.createURI value-factory uri-string))
  ([value-factory namespace local-name]
    (.createURI value-factory namespace local-name)))

(def default-context (org.openrdf.model.impl.URIImpl. "default:graph"))

(defn create-resource
  [uri]
  ^Resource uri)


(defn create-literal
  ([value-factory str lang] 
    (.createLiteral value-factory str lang))
  ([value-factory str] 
    (.createLiteral value-factory str)))
  

(defn get-language 
  [literal]
  (.getLanguage literal))


(defn string-value 
  [value]
  (.stringValue value))


(defn create-statement 
  ([value-factory s p o]
    (.createStatement value-factory s p o default-context))
  ([value-factory s p o context]
    (.createStatement value-factory s p o context)))



(defn add-statement
  ([repo s p o]
    (repo-transaction repo
      (.add s p o (get-contexts default-context))))
  ([repo s p o context]
    (repo-transaction repo
      (.add s p o (get-contexts context))))
  ([repo statement]
    (repo-transaction repo
      (.add statement (get-contexts default-context))))
  ([repo statement context]
    (repo-transaction repo
      (.add statement (get-contexts context)))))


(defn add-statements
  [repo statements context]
    (repo-transaction repo
      (.add (java.util.ArrayList. statements) (get-contexts context))))


(defn remove-statement
  ([repo s p o context]
    (repo-transaction repo
      (.remove s p o (get-contexts context))))
  ([repo statement context]
    (repo-transaction repo
      (.remove statement (get-contexts context)))))


(defn process-repository-results 
  [process results]
  (doseq [result (sesame-iterator-seq results)]
    (process result)))


(defn process-statements-query
  [repo s p o process]
  (with-open [conn (get-connection repo)]
    (let [stmts (.getStatements conn s p o true (into-array Resource []))]
      (process-repository-results process stmts))))


(defn- add-rdf
  [repo stream format context]
  (repo-transaction repo
    (.add stream "" format (get-contexts context))))
    

(defn add-file
  [repo file-name format context]
  (let [format (get-rdf-format format)]
    (let [file (java.io.File. file-name)]
      (add-rdf repo file format context))))


(defn add-uri
  [repo uri-string format context]
  (let [format (get-rdf-format format)]
    (with-open [stream (clojure.java.io/input-stream uri-string)]
      (add-rdf repo stream format context))))


(defn clear-context
  [repo context]
  (repo-transaction repo 
    (.clear (get-contexts context))))


(defn get-context-ids
  [repo]
  (with-open [conn (get-connection repo)
              results (.getContextIDs conn)]
    (let [seq (sesame-iterator-seq results)]
      (vec (doall (map identity seq))))))


(defn context-size
  [repo context]
  (with-open [conn (get-connection repo)]
    (let [size (.size conn (get-contexts context))]
      size)))


(defn all-contexts-size 
  [repo]
  (context-size repo (get-context-ids repo)))
  