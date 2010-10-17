(defproject fetcher "1.0.0-SNAPSHOT"
  :description "work based fetcher service."
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
		 [http.async.client "0.2.0"]
                 [javax.mail/mail "1.4.3"]
                 [work "0.0.1-SNAPSHOT"]
                 [crane "1.0-SNAPSHOT"]
                 [org.clojars.mattrepl/lein-daemon "0.2.1-SNAPSHOT"]]
  :dev-dependencies [[swank-clojure "1.2.1"]
                     [crane/lein-crane "0.0.1-SNAPSHOT"]
                     [org.clojars.mattrepl/lein-daemon "0.2.1-SNAPSHOT"]]
  :repositories {"java.net" "http://download.java.net/maven/2"
                 "clojars" "http://clojars.org/repo"}
  :daemon {"aws-poller-scheduler" {:ns "aws-poller-scheduler-daemon"
                                   :args []
                                   :options {:pidfile "aws-poller-scheduler.pid"
                                             :errfile "err.out"
                                             :user "ubuntu"}
                                   :extra-classpath ["crane"]}})