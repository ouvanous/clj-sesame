(defproject clj-sesame "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.openrdf.sesame/sesame-runtime "2.7.9"]
                 [org.apache.marmotta/sesame-tools-rio-jsonld "3.0.0-incubating"]
                 [org.apache.directory.studio/org.apache.commons.logging "1.1.3"]]
  :user {:plugins [[lein-midje "3.0.0"]]})
