(ns clojureize-keys.core
  (:require
    [clojure.spec.alpha :as s]
    [clojure.string :as string]
    [clojure.walk :refer [postwalk]]))

(defn- invalid-as-keyword?
  [s]
  (string/includes? s " "))

(defn keyword-or-str
  "Returns a keyword iff it would be valid, string otherwise."
  ([s]
   (if (invalid-as-keyword? s)
     s
     (keyword s)))
  ([ns s]
   (if (invalid-as-keyword? s)
     (str ns "/" s)
     (keyword ns s))))
(s/fdef keyword-or-str
        :args (s/or :just-str (s/cat :s string?)
                    :ns-and-str (s/cat :ns string? :s string?))
        :ret (s/or :keyword keyword? :string string?))


(defmulti key-fn type)

;; (defmethod key-fn java.lang.String [k] k)

(defmethod key-fn clojure.lang.Keyword
  [k]
  (let [transform #(string/replace % #"_" "-")]
    (if-let [ns (namespace k)]
      (keyword-or-str ns (transform (name k)))
      (keyword-or-str (transform (name k))))))

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
