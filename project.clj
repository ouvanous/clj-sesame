(defproject clj-sesame "0.1.0"
  :description "A simple clojure wrapper for sesame"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.openrdf.sesame/sesame-runtime "2.7.9"]
                 [org.apache.marmotta/sesame-tools-rio-jsonld "3.0.0-incubating"]
                 ; [com.github.jsonld-java/jsonld-java-sesame "0.2"]
                 [org.apache.directory.studio/org.apache.commons.logging "1.1.3"]
                 [org.semarglproject/semargl-sesame "0.6.1"]
                 [org.semarglproject/semargl-rdfa "0.6.1"]
                 [org.semarglproject/semargl-rdf "0.6.1"]
                 [org.ccil.cowan.tagsoup/tagsoup "1.2.1"]]
  :user {:plugins [[lein-midje "3.0.0"]]})
