(ns menu
  (:require [clojure.string :as str])
  (:require [clojure.java.io :as io])
  ;(:require [compress :as compress])
  )


; Extracting list of words from file 'frequency.txt'
(def list-words
  (with-open [reader (clojure.java.io/reader "frequency.txt")]
    (vec (mapcat #(clojure.string/split % #"\s+") (line-seq reader)))))

(def distinct-words (distinct list-words))

; Creating map from the list of words
(def word-to-index-map (zipmap distinct-words (range)))
(def index-to-word-map (zipmap (range) distinct-words))

; Wrap numbers in @ symbols to maintain numberical value
(defn wrap-number [word]
  (if (re-matches #"\d+" word)
    (str "@" word "@")
    word))

; Compress word by returning the index or word itself if unfound.
(defn compress [word-to-compress]
  (let [lowercase-word (clojure.string/lower-case word-to-compress)
        result (get word-to-index-map lowercase-word)]
    (if (nil? result)
      (clojure.string/replace (wrap-number word-to-compress) #"\s" "")
      result)))

; Decompress index by returning the word
(defn decompress [index-to-decompress]
  (if (integer? index-to-decompress)
    (let [result (get index-to-word-map index-to-decompress index-to-decompress)]
      result)
    index-to-decompress))


; Display the menu and ask the user for the option
(defn showMenu
  []
  (println "\n\n*** Compression Menu ***")
  (println "------------------\n")
  (println "1. Display list of files")
  (println "2. Display file contents")
  (println "3. Compress a file")
  (println "4. Uncompress a file")
  (println "5. Exit")
  (println "6. Correct Punctuation")
  (do
    (print "\nEnter an option? ")
    (flush)
    (read-line)))


; Write content to a file
(defn write-to-file [file-name ouput]
  (spit file-name (clojure.string/join " " ouput)))


; Display list of files in the current directory (option 1)
(defn list-files []
  (let [current-directory (System/getProperty "user.dir")]
    (doseq [file (file-seq (java.io.File. current-directory))]
      (when (.isFile file)
        (println (.getName file))))))


; Check file existence
(defn file-exists? [file-name]
  (.exists (java.io.File. file-name)))


; Read file (option 2)
(defn read-file [file-name]
  (if (file-exists? file-name)
    (do
      (println "now read" file-name "with slurp and display the contents: \n")
      (let [file-content (slurp file-name)]
        (println file-content)))
    (println "File does not exist.")))


; Split words from symbols
(defn split-words [content]
  (clojure.string/split content #"\s+|(?<=\W)(?=\w)|(?<=\w)(?=\W)"))


; Capitalize the 1st character of a string
(defn capitalize-string [string]
  (str (str/upper-case (subs string 0 1))
       (subs string 1)))


; Uses 'capitalize-string' to capitalize the 1st letter of the sentence
(defn capitalize-sequence [sequence]
  (map-indexed (fn [index element]
                 (if (= index 0)
                   (capitalize-string element) ;
                   element))
               sequence))


; Splits a sequence into strings
(defn split-content [content]
  (clojure.string/split content #"\s+"))


; Decompresses integers
(defn decompress-sequence [sequence]
  (map #(try (decompress (Integer. %)) (catch Exception e %)) sequence))


(defn unwrap-number [text]
  (if (re-matches #"\A@(.*)@\z" text)
    (subs text 1 (dec (count text)))
    text))

; Functions to add a space after a symbol
(defn add-space-after-punctuation [text]
  (clojure.string/replace text #"(?<=\p{P})(?=\S)" " "))


(defn add-space-before-opening-paren [text]
  (clojure.string/replace text #"(?<=\S)(?=[\(\[]) " ""))


(defn add-space-after-closing-paren [text]
  (clojure.string/replace text #"(?<=[])])(?=\S)" " "))


(defn add-space-around-dash [text]
  (clojure.string/replace text #"(?<=\S)(?=-)|(?<=-)(?=\S)" " "))


(defn add-space-before-dollar-sign [text]
  (clojure.string/replace text #"(?<=\S)(?=\$)" " "))


; Functions to remove a space after a symbol
(defn remove-space-after-start-paren [text]
  (clojure.string/replace text #"\(\s+" "("))


(defn remove-space-after-start-bracket [text]
  (clojure.string/replace text #"\[\s+" "["))


(defn remove-space-before-end-bracket [text]
  (clojure.string/replace text #"\s+\]" "]"))


(defn remove-space-before-comma [text]
  (clojure.string/replace text #"\s+(?=\,)" ""))


(defn remove-space-before-end-paren [text]
  (clojure.string/replace text #"\s+(?=[\)\]])" ""))


(defn remove-space-before-period [text]
  (clojure.string/replace text #"\s+\." "."))


(defn remove-space-before-question [text]
  (clojure.string/replace text #"\s+\?" "?"))


(defn remove-space-before-exclamation [text]
  (clojure.string/replace text #"\s+\!" "!"))


(defn apply-functions [text functions]
  (reduce #(%2 %1) text functions))


; Correct punctuation to ensure proper English syntax
(defn correct-punctuation [text]
  (apply-functions text
                   [add-space-after-punctuation
                    add-space-before-opening-paren
                    add-space-after-closing-paren
                    add-space-around-dash
                    add-space-before-dollar-sign
                    remove-space-after-start-paren
                    remove-space-after-start-bracket
                    remove-space-before-end-bracket
                    remove-space-before-comma
                    remove-space-before-end-paren
                    remove-space-before-period
                    remove-space-before-question
                    remove-space-before-exclamation
                    unwrap-number]))


; Display all files in the current folder
(defn option1
  []
  (println "use file-seq to get and print a list of all files in the current folder")
  (list-files))


; Read and display the file contents (if the file exists).
(defn option2
  []
  (print "\nPlease enter a file name => ")
  (flush)
  (let [file_name (read-line)]
    (read-file file_name)))


; Compress the valid file provided by the user and stores output in file.
(defn option3 []
  (print "\nPlease enter a file name => ")
  (flush)
  (let [file-name (read-line)]
    (if (file-exists? file-name)
      (do
        (println "now compress" file-name "with the functions(s) you provide in compress.clj")
        (with-open [reader (clojure.java.io/reader file-name)]
          (let [content (slurp reader)
                words-and-symbols (split-words content)
                compressed-words (map compress words-and-symbols)
                file-output (str file-name ".ct")]
            (println "\nOriginal text: " content) 
            (println "\nCompressed text:" (clojure.string/join " " compressed-words))
            (write-to-file file-output compressed-words))))
      (println "File does not exist."))))


; Decompress the valid file provided by the user and stores output in file.
(defn option4 []
  (print "\nPlease enter a file name => ")
  (flush)
  (let [file-name (read-line)]
    (if (file-exists? file-name)
      (do
        (println "now decompress" file-name "with the functions(s) you provide in compress.clj")
        (with-open [reader (clojure.java.io/reader file-name)]
          (let [content (slurp reader)
                numbers-and-words (split-content content)
                decompressed-numbers (decompress-sequence numbers-and-words)
                capitalized-output (capitalize-sequence decompressed-numbers)]
            (println "\nOriginal text:" content)
            (println "\nDecompressed text:" capitalized-output)
            (println "\nTo have correct punctuation, please check option 6, thank you! :D")
            (println "You can copy paste the decompressed text above and paste it in option 6."))))
      (println "File does not exist."))))



(defn option6 []
  (print "\nPlease enter a sentence to correct its punctuation => ")
  (flush)
  (let [sentence (read-line)]
    (println "\nOriginal text: " sentence)
    (let [new-sentence (correct-punctuation sentence)]
      (println "\nCorrect punctuation: " new-sentence))))


; If the menu selection is valid, call the relevant function to 
; process the selection
(defn processOption
  [option]
  (if (= option "1")
    (option1)
    (if (= option "2")
      (option2)
      (if (= option "3")
        (option3)
        (if (= option "4")
          (option4)
          (if (= option "6")
            (option6)
          (println "Invalid Option, please try again")))))))


; Display the menu and get a menu item selection. Process the
; selection and then loop again to get the next menu selection
(defn menu
  []
  (let [option (str/trim (showMenu))]
    (if (= option "5")
      (println "\nGood Bye\n")
      (do
        (processOption option)
        (recur)))))


; ------------------------------

(menu) 