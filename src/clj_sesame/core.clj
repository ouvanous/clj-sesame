(ns clj-sesame.core
  (import org.openrdf.rio.RDFFormat
          org.openrdf.repository.RepositoryException))






(defn sesame-iterator-seq [results]
  (lazy-seq
   (when (.hasNext results)
     (cons 
      (.next results)
      (sesame-iterator-seq results)))))






(defn get-rdf-format
  [format]
  (case format
    :turtle RDFFormat/TURTLE
    :n3     RDFFormat/N3
    :nt     RDFFormat/NTRIPLES 
    :xml    RDFFormat/RDFXML
    :jsonld RDFFormat/JSONLD
    :rdfa   RDFFormat/RDFA))

