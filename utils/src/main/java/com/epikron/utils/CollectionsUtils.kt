package com.epikron.utils

import kotlin.math.abs
import kotlin.random.Random

/** Shifts the elements of a collection with the number of [positions]. Ex: {A,B,C,D}.shift(2) => {C,D,A,B}*/
fun <T> Collection<T>.shift(positions: Int): Collection<T> {
    if (positions == this.size || positions == 0) return this
    val result: MutableCollection<T> = mutableListOf()
    var elementIndex = if (abs(positions) >= this.size) positions.mod(this.size) else abs(positions)
    if (elementIndex == 0) return this
    if (positions > 0) elementIndex = this.size - elementIndex
    repeat(this.size) {
        result.add(this.elementAt(elementIndex))
        elementIndex = elementIndex.ascendInRange(this.size - 1)
    }
    return result
}

/** Shifts the elements of a collection with the number of [positions]. Ex: {A,B,C,D}.shift(2) => {C,D,A,B}*/
fun <T> MutableCollection<T>.shift(positions: Int) {
    val result = this.toList().shift(positions)
    this.clear()
    this.addAll(result)
}

/**Returns a list containing [elementCount] number of randomly picked unique elements from the input list, excluding [exceptElement], if is included in the input list*/
fun <T> List<T>.takeRandomExcluding(elementCount: Int, exceptElement: T?): List<T> {
    if (this.size <= elementCount) return this.shuffled()
    val list: List<T> = exceptElement?.let { element -> if (this.contains(element)) this.filter { it != element } else this } ?: this
    val result: MutableList<T> = mutableListOf()
    val chosenIndices: MutableList<Int> = mutableListOf()
    repeat(elementCount) {
        var nextIndex = Random.nextInt(list.size)
        while (nextIndex in chosenIndices) {
            nextIndex = Random.nextInt(list.size)
        }
        chosenIndices.add(nextIndex)
        result.add(list[nextIndex])
    }
    return result
}

/**Returns a list containing [elementCount] number of randomly picked unique elements from the input list, including [includeElement] even if is not found in the input list*/
fun <T> List<T>.takeRandomIncluding(elementCount: Int, includeElement: T): List<T> {
    return (this.takeRandomExcluding(elementCount - 1, includeElement) + includeElement).shuffled()
}

/**Returns a [defaultItem] instead of throwing an exception when the element at [index] of the list does not exist.*/
fun <T> List<T>.getOrDefault(index: Int, defaultItem: T): T {
    return try {
        this[index]
    } catch (e: Exception) {
        defaultItem
    }
}
