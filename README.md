# clojure-compression
> This program compresses .txt files based on its word frequency, and decompresses it back to its original form.

## Word Frequency
A list of common English words, sorted by frequency (ie. more frequent words at the start of the list), can be found [here](/src/frequency.txt).

## Compression
- Compress file contents by converting each word to its value from the frequency map.
- If a word is not found in this list, then it the original word will remain.
- The output of this compression will be printed and stored in a file with '.ct' appended.

## Decompression
- Decompress file contents by converting each number to its word from the frequency map.
- The output of this compression will be printed.

## Symbols/Formatting
Symbols, such as punctuation marks, are handled in such to conform to proper English conventions.
