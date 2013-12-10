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
    (is (= 0 (rdf/all-contexts-size repo))))


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
        (is (= 4 (rdf/all-contexts-size repo))))

      (testing " - find statement by subject"
        (let [all-results  (rdf/get-statements repo s nil nil (rdf/get-context-ids repo))
              default-graph-results (rdf/get-statements repo s nil nil)
              context-results (rdf/get-statements repo s nil nil context)]
          (is (= 4 (count all-results)))
          (is (= 2 (count default-graph-results)))
          (is (= 2 (count context-results)))))

      (testing " - find statement by predicate"
        (let [all-results  (rdf/get-statements repo nil p nil (rdf/get-context-ids repo))
              default-graph-results (rdf/get-statements repo nil p nil)
              context-results (rdf/get-statements repo nil p nil context)]
          (is (= 4 (count all-results)))
          (is (= 2 (count default-graph-results)))
          (is (= 2 (count context-results)))))

      (testing " - find statement by object"
        (let [all-results  (rdf/get-statements repo nil nil o2 (rdf/get-context-ids repo))
              default-graph-results (rdf/get-statements repo nil nil o2)
              context-results (rdf/get-statements repo nil nil o2 context)]
          (is (= 2 (count all-results)))
          (is (= 1 (count default-graph-results)))
          (is (= 1 (count context-results)))))

      (testing " - process statements-query"
        "should print http://ouva.io/resource-1"
        (rdf/process-statements-query repo s p o1 (fn [stmt]
          (is (= "http://ouva.io/resource-1" (rdf/string-value (rdf/subject stmt)))))))


      (testing " - remove statements"
        (rdf/remove-context-statements repo context)
        (is (= 2 (rdf/all-contexts-size repo)))
        (rdf/remove-all-statements repo)
        (is (= 0 (rdf/all-contexts-size repo))))

      (testing " - add multiple statements"
        (rdf/add-statements repo [stmt stmt2])
        (is (= 2 (rdf/all-contexts-size repo))))

      (testing " - add rdf file"
        (rdf/remove-all-statements repo)
        (is (= 0 (rdf/all-contexts-size repo)))
        (rdf/add-file repo "./test-files/Scots_Pine.rdf" :xml context)
        (is (= 103 (rdf/context-size repo context)))
        (rdf/remove-all-statements repo)
        (is (= 0 (rdf/all-contexts-size repo)))
        (rdf/add-file repo "./test-files/test.ttl" :turtle context)
        (is (= 2 (rdf/context-size repo context))))

      (testing "namespaces"
        (is (= 11 (count (keys (repository/get-namespaces repo)))))
        (repository/set-namespace repo :ouva "http://ouva.io/")
        (is (= 12 (count (keys (repository/get-namespaces repo)))))
        (repository/set-namespaces repo {:ouva "http://ouva.io/" :ouv2 "http://ouva.io/2/"})
        (is (= 13 (count (keys (repository/get-namespaces repo)))))

      ; need to test add-uri 

      )))
