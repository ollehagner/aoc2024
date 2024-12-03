package day02

import common.readInput
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.windowed
import kotlin.math.abs

fun main() {
    val testinput = readInput("main/kotlin/day02/testinput")
    val input = readInput("main/kotlin/day02/input")
    println("Part 1 Safe reports: ${part1(input)}")
    println("Part 2 Safe reports: ${part2(input)}")

}

fun part1(input: List<String>): Int {
    val levelsList = input.map { line -> line.split(" ").filter { it.isNotEmpty() }.map { it.toInt() } }
    return levelsList
        .filter { isSafe(it) }
        .count()

}

fun part2(input: List<String>): Int {
    val levelsList: List<List<Int>> =
        input.map { line -> line.split(" ").filter { it.isNotEmpty() }.map { it.toInt() } }

    return levelsList
        .filter { levels: List<Int> ->
            levels.indices.any { index ->
                val subList = removeIndexed(levels, index)
                isSafe(subList)
            }
        }
        .count()
}

fun isSafe(list: List<Int>): Boolean {
    val delta = toDeltaList(list)
    return hasValidDistance(delta) && hasOneDirection(delta)
}

fun hasValidDistance(levels: List<Int>): Boolean {
    return levels
        .map { abs(it) }
        .all { it >= 1 && it <= 3 }
}

fun hasOneDirection(levels: List<Int>): Boolean {
    return levels.all { it < 0 } || levels.all { it > 0 }
}

fun toDeltaList(values: List<Int>): List<Int> = values.windowed(2).map { (a, b) -> a - b }

fun removeIndexed(list: List<Int>, index: Int): List<Int> {
    val subList = list.toMutableList()
    subList.removeAt(index)
    return subList
}