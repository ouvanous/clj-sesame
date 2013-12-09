(ns clj-sesame.rdf
  (:use clj-sesame.core
        clj-sesame.repository)
  (import org.openrdf.repository.RepositoryException
          (org.openrdf.model Resource Statement)
          info.aduna.iteration.Iterations
          org.openrdf.model.impl.LinkedHashModel
          org.openrdf.rio.Rio
          org.openrdf.rio.RDFFormat))





(def default-context (org.openrdf.model.impl.URIImpl. "default:graph"))






(defn get-contexts
  "transform an URI or a vector of URI's to a Resource[] type
  URI -> Resource[]"
  [context]
  (if (= clojure.lang.PersistentVector (type context))
    (into-array Resource context)
    (into-array Resource [context])))






(defn subject 
  "get the subject of a statement 
  Statement -> URI"
  [statement]
  (.getSubject statement))






(defn predicate
  "get the predicate of a statement 
  Statement -> URI"
  [statement]
  (.getPredicate statement))






(defn object
  "get the object of a statement 
  Statement -> URI | Literal"
  [statement]
  (.getObject statement))






(defn create-uri
  "Create an URI from String
  String -> URI"
  ([value-factory uri-string]
    (.createURI value-factory uri-string))
  ([value-factory namespace local-name]
    (.createURI value-factory namespace local-name)))






(defn create-resource
  "Cast to Resource an URI
  URI -> Resource"
  [uri]
  ^Resource uri)






(defn create-literal
  "Create a Literal from String 
  String -> Literal 
  or a Literal with a language attribute
  String -> String -> Literal"
  ([value-factory str] 
    (.createLiteral value-factory str))
  ([value-factory str lang] 
    (.createLiteral value-factory str lang)))






(defn get-language 
  "get the language of a Literal
  Literal -> String"
  [literal]
  (.getLanguage literal))






(defn string-value
  "get the value of a URI or Literal
  Value -> String"
  [value]
  (.stringValue value))






(defn create-statement 
  "Create a statement from s, p, o with optional context
  ValueFactory -> URI -> URI -> URI | Literal -> URI"
  ([value-factory s p o]
    (create-statement value-factory s p o default-context))
  ([value-factory s p o context]
    (.createStatement value-factory s p o context)))






(defn add-statement
  "Add a statement in a repository"
  ([repo s p o]
    (add-statement repo s p o default-context))
  ([repo s p o context]
    (repo-transaction repo
      (.add s p o (get-contexts context))))
  ([repo statement]
    (add-statement repo statement default-context))
  ([repo statement context]
    (repo-transaction repo
      (.add statement (get-contexts context)))))






(defn add-statements
  "Add a vector of statements in a repository"
  ([repo statements]
    (add-statements repo statements default-context))
  ([repo statements context]
      (repo-transaction repo
        (.add (java.util.ArrayList. statements) (get-contexts context)))))






(defn process-repository-results 
  "process each result"
  [process results]
  (doseq [result (sesame-iterator-seq results)]
    (process result)))






(defn process-statements-query
  "process a statements query"
  [repo s p o process]
  (with-open [conn (get-connection repo)]
    (let [stmts (.getStatements conn s p o true (into-array Resource []))]
      (process-repository-results process stmts))))






(defn- add-rdf
  [repo stream format context]
  (repo-transaction repo
    (.add stream "" format (get-contexts context))))






(defn add-file
  "add an rdf file to a repository"
  [repo file-name format context]
  (let [format (get-rdf-format format)]
    (let [file (java.io.File. file-name)]
      (add-rdf repo file format context))))






(defn add-uri
  "add an rdf uri to a repository"
  [repo uri-string format context]
  (let [format (get-rdf-format format)]
    (with-open [stream (clojure.java.io/input-stream uri-string)]
      (add-rdf repo stream format context))))







(defn get-context-ids
  "return all existing contexts in a repo as a vector of URI's"
  [repo]
  (with-open [conn (get-connection repo)
              results (.getContextIDs conn)]
    (let [seq (sesame-iterator-seq results)]
      (vec (doall (map identity seq))))))






(defn context-size
  "count the statements for a context"
  [repo context]
  (with-open [conn (get-connection repo)]
    (let [size (.size conn (get-contexts context))]
      size)))






(defn all-contexts-size 
  "coutn all the statements for all contexts"
  [repo]
  (context-size repo (get-context-ids repo)))






(defn remove-statement
  "remove a statement"
  ([repo s p o context]
    (repo-transaction repo
      (.remove s p o (get-contexts context))))
  ([repo statement context]
    (repo-transaction repo
      (.remove statement (get-contexts context)))))






(defn remove-context-statements
  "remove all the statements for a context"
  [repo context]
  (with-open [conn (get-connection repo)]
    (.clear conn (get-contexts context))))






(defn remove-all-statements
  [repo]
  (with-open [conn (get-connection repo)]
    (.clear conn (get-contexts (get-context-ids repo)))))





