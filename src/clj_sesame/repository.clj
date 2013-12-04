(ns clj-sesame.repository
  (import 
    org.openrdf.repository.manager.RemoteRepositoryManager
    org.openrdf.repository.Repository
    org.openrdf.repository.sail.SailRepository
    org.openrdf.sail.memory.MemoryStore
    org.openrdf.repository.RepositoryException))












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







