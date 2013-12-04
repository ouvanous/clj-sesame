(ns clj-sesame.rdf
  (:use clj-sesame.core
        clj-sesame.repository)
  (import org.openrdf.repository.RepositoryException
          (org.openrdf.model Resource Statement)
          info.aduna.iteration.Iterations
          org.openrdf.model.impl.LinkedHashModel
          org.openrdf.rio.Rio
          org.openrdf.rio.RDFFormat))



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


(defn create-resource
  [uri]
  ^Resource uri)

(defn create-literal
  ([value-factory str lang] 
    (.createLiteral value-factory str lang))
  ([value-factory str] 
    (.createLiteral value-factory str)))
  

(defn create-statement 
  ([value-factory s p o]
    (.createStatement value-factory s p o))
  ([value-factory s p o context]
    (.createStatement value-factory s p o context)))


(defn create-resource-statement
  [s p o]
  (let [s (create-uri s)
        p (create-uri p)
        o (create-uri o)]
    (create-statement s p o)))


(defn create-literal-statement
  [s p o lang]
  (let [s (create-uri s)
        p (create-uri p)
        o (create-literal o lang)]
    (create-statement s p o)))


(defn get-contexts
  [context]
  (if (= clojure.lang.PersistentVector (type context))
    (into-array Resource context)
    (into-array Resource [context])))


(defn add-statement
  ([repo s p o context]
    (with-open [conn (get-connection repo)]
      (try 
        (doto conn
          (.begin)
          (.add s p o (get-contexts context))
          (.commit))
        (catch RepositoryException e 
          (.rollback conn)))))
  ([repo statement context]
    (with-open [conn (get-connection repo)]
      (try 
        (doto conn
          (.begin)
          (.add statement (get-contexts context))
          (.commit))
        (catch RepositoryException e 
          (.rollback conn))))))


(defn add-statements
  [repo statements context]
    (with-open [conn (get-connection repo)]
      (try 
        (println statements)
        (println (into-array Statement statements))
        (doto conn
          (.begin)
          (.add (java.util.ArrayList. statements) (get-contexts context))
          (.commit))
        (catch RepositoryException e 
          (.rollback conn)))))


(defn process-statements-query
  [repo s p o process]
  (with-open [conn (get-connection repo)]
    (let [stmts (.getStatements conn s p o true (into-array org.openrdf.model.Resource []))]
        (doseq [tuple (sesame-iterator-seq stmts)]
          (process tuple)))))


(defn- add-rdf
  [repo stream format context]
  (with-open [conn (get-connection repo)]
    (try
      (doto conn
        (.begin)
        (.add stream "" format (get-contexts context))
        (.commit))
      (catch RepositoryException e 
        (.rollback conn)))))
    

(defn add-rdf-file
  [repo file-name format context]
  (let [format (get-rdf-format format)]
    (let [file (java.io.File. file-name)]
      (add-rdf repo file format context))))


(defn add-rdf-uri
  [repo uri-string format context]
  (let [format (get-rdf-format format)]
    (with-open [stream (clojure.java.io/input-stream uri-string)]
      (add-rdf repo stream format context))))




  

