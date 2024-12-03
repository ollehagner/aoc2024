package day01

import common.readInput
import kotlin.math.abs

fun main() {
    val testInput = readInput("main/kotlin/day01/testinput")
    val input = readInput("main/kotlin/day01/input")
//    println("Part 1 Total distance: " + part1(toColumnLists(input)))
    println("Part 2 Total similarity score: " + part2(toColumnLists(input)))
}

fun part1(locations: Pair<List<Int>, List<Int>>): Int {
    return locations.first.sorted().zip(locations.second.sorted())
        .map { (a, b) -> abs(a - b) }
        .sum()
}

fun part2(locations: Pair<List<Int>, List<Int>>): Int {
    val count = locations.second.groupingBy { it }.eachCount()
    return locations.first
        .map { it * count.getOrDefault(it, 0) }
        .sum()

}

fun toColumnLists(rows: List<String>): Pair<List<Int>, List<Int>> {
    return rows
        .map { it.substringBefore("   ").toInt() to it.substringAfter("   ").toInt() }
        .fold(Pair<MutableList<Int>, MutableList<Int>>(mutableListOf(), mutableListOf())) {
                acc, pair ->
            acc.first.add(pair.first)
            acc.second.add(pair.second)
            acc
        }
}