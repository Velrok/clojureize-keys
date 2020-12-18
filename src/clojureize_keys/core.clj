(ns clojureize-keys.core
  (:require
    [clojure.spec.alpha :as s]
    [clojure.string :as string]
    [clojure.walk :refer [postwalk]]))

(defmulti key-fn type)

;; (defmethod key-fn java.lang.String [k] k)

(defmethod key-fn clojure.lang.Keyword
  [k]
  (let [transform #(string/replace % #"_" "-")]
    (if-let [ns (namespace k)]
      (keyword ns (transform (name k)))
      (keyword (transform (name k))))))

(defmethod key-fn :default [k] k) ;; no transform by default

(defn clojureize-keys
  [m]
  (let [transform (fn [x]
                    (if (map? x)
                      (into {}
                            (for [[k v] x]
                              [(key-fn k) v]))
                      x))]
    (postwalk transform m)))
(s/fdef clojureize-keys
        :args (s/cat :m map?)
        :ret map?)
