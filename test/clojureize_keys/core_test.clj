(ns clojureize-keys.core-test
  (:require
    [criterium.core :as criterium]
    [clojure.spec.gen.alpha :as gen]
    [clojure.spec.test.alpha :as stest]
    [clojure.test :refer [testing deftest is]]
    [clojureize-keys.core :refer [clojureize-keys keyword-or-str]]
    ))

(def rand-str-seq (repeatedly #(gen/generate (gen/gen-for-pred string?))))
(def rand-map-seq (repeatedly #(gen/generate (gen/gen-for-pred map?))))

(deftest keyword-or-str-test
  (testing "examples"
    (is (= :a-b (keyword-or-str "a-b")))
    (is (= "a b" (keyword-or-str "a b")))
    (is (= :a/b (keyword-or-str "a/b")))
    (is (= :a.x/b (keyword-or-str "a.x/b")))
    )
  (testing "properties"
    (testing "will not throw exceptions"
      (stest/summarize-results (stest/check  `keyword-or-str))))
  (testing "performance"
    (let [sample-count 1000
          avg-exec-target-ns 1500
          _pregen (doall (take sample-count rand-str-seq))
          [duration-ns _] (criterium/time-body (doseq [s (take sample-count rand-str-seq)]
                                                 (keyword-or-str s))) ]
      (is (<= (/ duration-ns sample-count) 
             avg-exec-target-ns)))))

;; TODO:
;; - use  prob based testing to run though different variants of keywords and strings are keys
;; - do some basic performance testing -> use snake kebab as base

(deftest clojureize-keys-test
  (testing "examples"
    (is (= {:a-b :x} (clojureize-keys {:a_b :x})))
    (is (= {:a-b :x} (clojureize-keys {:aB :x})))
    (is (= {:x.y.z/a-b :x} (clojureize-keys {:x.y.z/a_b :x}))))
  (testing "properties"
    (testing "doesnt throw"
      (stest/summarize-results (stest/check  `clojureize-keys))))
  (testing "performance"
    (let [sample-count 1000
          avg-exec-target-ns 42000
          _pregen (doall (take sample-count rand-map-seq))
          [duration-ns _] (criterium/time-body (doseq [m (take sample-count rand-map-seq)]
                                                 (clojureize-keys m))) ]
      (is (<= (float (/ duration-ns sample-count)) 
             avg-exec-target-ns)))))

(comment
  (keyword-or-str-test)
  (clojureize-keys-test)
  (gen/generate (gen/gen-for-pred map?))
  )

