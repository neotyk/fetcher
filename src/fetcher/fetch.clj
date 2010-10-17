(ns fetcher.core
  (:use fetcher.handler)
  (:use webmine.feeds)
  (:require [work.core :as work])
  (:require [work.cache :as cache])
  (:require [store.s3 :as s3])
  (:require [store.api :as api])
  (:require [crane.sqs :as sqs])
  (:require [crane.ec2 :as ec2]))

(defn status-check
  "Check if status code is 304, abort if so."
  [_ status]
  (if (= 304 (:code status))
    [status :abort]
    [status :continue]))

;;; Need a closure to capture the feed-url.
(defn dispatch-generator
  "Return a fn to handle a completed response."
  [feed-key feed-url response-callback]
  (fn [state]
    (let [code (-> (c/status state) :code)
          headers (c/headers state)
          body (-> (c/body state) .toString)]
      (response-callback feed-key
                         feed-url
                         code
                         headers
                         body)
      [true :continue])))

(defn fetch
  "fetch a feed for updates.  Responses are handled asynchronously by the provided callback.

  The callback should accept five arguments: k, u, response code, headers, and body."
  [[k u & headers] response-callback]
  (let [callbacks (merge async-req/*default-callbacks*
                         {:status status-check
                          :completed (dispatch-generator k u response-callback)})
        req (async-req/prepare-request :get
			     u
			     :headers headers)]
	resp (apply async-req/execute-request
                    req
                    (apply concat callbacks))]
    resp))

(defn fetch-pool
  [fetch get-work put-done]
  (work/queue-work
   fetch
   get-work
   put-done
   (work/available-processors)
   :async))