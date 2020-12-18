(ns clojureize-keys.core-test
  (:require
    ;[clojure.spec.test :as stest]
    ; [clojure.spec.gen :as gen]
    ; [clojure.spec :as s]
    [clojure.test :refer [testing deftest is]]
    [clojureize-keys.core :refer [clojureize-keys]]
    ))

;; TODO:
;; - use  prob based testing to run though different variants of keywords and strings are keys
;; - do some basic performance testing -> use snake kebab as base

(deftest clojureize-keys-test
  (testing "basics"
    (is (= {:a-b :x} (clojureize-keys {:a_b :x})))
    (is (= {:a-b :x} (clojureize-keys {:aB :x})))
    (is (= {:x.y.z/a-b :x} (clojureize-keys {:x.y.z/a_b :x}))))
  ;(stest/check-var #'clojureize-keys :num-tests 100)
  )
