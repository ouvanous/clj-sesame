(ns clj-sesame.repository
  (:use clj-sesame.core)
  (import 
    org.openrdf.repository.manager.RemoteRepositoryManager
    org.openrdf.repository.Repository
    org.openrdf.repository.sail.SailRepository
    org.openrdf.sail.memory.MemoryStore
    org.openrdf.repository.RepositoryException
    org.openrdf.repository.http.HTTPRepository))






(defmacro repo-transaction 
  [repo body]
  `(with-open [conn# (get-connection ~repo)]
    (try 
     (doto conn#
       (.begin)
       (~@body)
       (.commit))
     (catch RepositoryException e# 
       (.rollback conn#)))))






;- ----------------------------------------------------------------------------
;- REMOTE REPO

(defn remote-manager 
  "Get the remote manager"
  [uri]
  (RemoteRepositoryManager/getInstance uri))






(defn get-remote-repository 
  "Get the repository from a remote manager"
  [uri repository-name]
  (let [manager (remote-manager uri)
        repository (.getRepository manager repository-name)]
    repository))






(defn get-connection 
  [repository]
  (.getConnection repository))






(defn get-value-factory 
  [repo]
  (.getValueFactory repo))






;- ----------------------------------------------------------------------------
;- 

(defn create-mem-repository
  []
  (let [repo (SailRepository. (MemoryStore.))]
    (.initialize repo)
    repo))







;- ----------------------------------------------------------------------------
;- 

(defn http-repository
  ([end-point-uri]
    (let [repo (HTTPRepository. end-point-uri)])))






(defn namespace-to-vec
  [namespace]
  [(keyword (.getPrefix namespace)) (.getName namespace)])






(defn get-namespaces
  [repo]
  (with-open [conn (get-connection repo)]
    (let [results (.getNamespaces conn)]
      (into {} (doall (map namespace-to-vec (sesame-iterator-seq results)))))))






(defn set-namespace
  [repo prefix namespace]
  (repo-transaction repo 
    (.setNamespace (name prefix) namespace)))






(defn set-namespaces
  [repo namespaces]
  (with-open [conn (get-connection repo)]
    (try 
      (.begin conn)
      (doseq [[prefix namespace] namespaces]
        (.setNamespace conn (name prefix) namespace))
      (.commit conn)
      (catch RepositoryException e 
        (.rollback conn)))))








