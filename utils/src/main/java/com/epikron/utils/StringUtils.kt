package com.epikron.utils

fun String.splitToWords(): List<String> =
    this.lowercase().trim().replace(".", "").replace(",", "").split(" ")

fun String.containsAllChars(query: String, ignoreCase: Boolean = true): Boolean {
    var result = true
    if (query.isNotBlank() && query.trim().contains(" ")) {
        query.splitToWords().forEach { word ->
            if (!this.contains(word, ignoreCase) && result) result = false
        }
    } else result = this.contains(query, ignoreCase)
    return result
}

fun String.containsAnyWord(query: String, ignoreWords: List<String> = listOf(), ignoreCase: Boolean = true): Boolean {
    var result = false
    if (query.isNotBlank() && query.trim().contains(" ")) {
        query.splitToWords().forEach { word ->
            if (!ignoreWords.contains(word) && this.contains(word, ignoreCase)) return true
        }
    } else result = this.contains(query, ignoreCase)
    return result
}

fun String.containsAllWords(query: String, ignoreCase: Boolean = true): Boolean {
    var result = true
    val wordList = this.splitToWords()
    if (query.isNotBlank() && query.trim().contains(" ")) {
        query.splitToWords().forEach { word ->
            if (!wordList.contains(word) && result) result = false
        }
    } else result = this.contains(query, ignoreCase)
    return result
}

fun String.containsAnyWords(query: String, ignoreCase: Boolean = true): Boolean {
    var result = false
    val wordList = this.splitToWords()
    if (query.isNotBlank() && query.trim().contains(" ")) {
        query.splitToWords().forEach { word ->
            if (wordList.contains(word)) return true
        }
    } else result = this.contains(query, ignoreCase)
    return result
}
