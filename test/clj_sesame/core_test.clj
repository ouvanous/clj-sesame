(ns clj-sesame.core-test
  (:use clj-sesame.core)
  (:require [clojure.test :refer :all]
            [clj-sesame.repository :as repository]
            [clj-sesame.rdf :as rdf]
            [clj-sesame.sparql :as sparql])
  (import (org.openrdf.model URI Literal Statement)
          org.openrdf.repository.sail.SailRepository))




(deftest mem-repo-test
  (testing "Testing mem repository type"
    
    "create a memory repository"
    (def repo (repository/create-mem-repository))
    
    "get the value factory to create uri, literal, ..."
    (def vf (repository/get-value-factory repo))

    "check if Repository type is ok "
    (is (= (type repo) SailRepository)))


  (testing "Mem repo should be empty"
    (is (= 0 (rdf/context-size repo nil))))


  (testing "create and add a statement"
    "Create 3 resource statements"
    (let [context (rdf/create-uri vf "http://ouva.io/")
          s (rdf/create-uri vf "http://ouva.io/resource-1")
          p (rdf/create-uri vf"http://ouva.io/predicate-1")
          o1 (rdf/create-literal vf "This is a literal ")
          o2 (rdf/create-literal vf "This is a literal with lang" "en")
          stmt (rdf/create-statement vf s p o1)
          stmt2 (rdf/create-statement vf s p o2 context)]
      (is (instance? URI s))
      (is (instance? URI p))
      (is (instance? Literal o1))
      (is (instance? Literal o2))
      (is (instance? Statement stmt))
      (is (instance? Statement stmt2))
      (is (= (rdf/get-language o1)))
      (is (= (rdf/get-language o2) "en"))
      (testing " - add the statement"
        (rdf/add-statement repo s p o1)
        (rdf/add-statement repo stmt context)
        (rdf/add-statement repo stmt2)
        (rdf/add-statement repo stmt2 context)
        (is (= 4 (rdf/all-contexts-size repo)))
      ))))

; (defn t 
;   [e]
;   e)


; (def repo (repository/create-mem-repository))
; (let [context (rdf/create-uri vf "http://ouva.io/")
;           s (rdf/create-uri vf "http://ouva.io/resource-1")
;           p (rdf/create-uri vf"http://ouva.io/predicate-1")
;           o1 (rdf/create-literal vf "This is a literal ")
;           o2 (rdf/create-literal vf "This is a literal with lang" "en")
;           stmt (rdf/create-statement vf s p o1)
;           stmt2 (rdf/create-statement vf s p o2 context)]
;           (rdf/add-statement repo s p o1))
; (defn tt 
;   [repo]
;   (with-open [conn (repository/get-connection repo)
;             results (.getContextIDs conn)]
;       (let [r (sesame-iterator-seq results)]
;       (doall (map t r)))))


; (println "????" (tt repo))