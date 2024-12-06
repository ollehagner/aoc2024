package day05

import common.readInput

fun main() {
    val testinput = readInput("main/kotlin/day05/testinput")
    val input = readInput("main/kotlin/day05/input")
    println("Part 1 Sum of ordered updates ${part1(input)}")
    println("Part2 Sum of ordered unordered updates: ${part2(input)}")
}

private fun part1(input: List<String>): Int {
    val rules = parseRules(input)
    val updates = parseUpdates(input)

    return updates
        .filter { update -> inOrder(update, rules) }
        .map { it[it.size / 2] }
        .sum()
}

private fun part2(input: List<String>): Int {
    val rules = parseRules(input)
    val updates = parseUpdates(input)

    return updates
    .filter { update -> !inOrder(update, rules) }
        .map { unOrderedUpdate -> order(unOrderedUpdate, rules.filterKeys { unOrderedUpdate.contains(it) }) }
        .map { it[it.size / 2] }
        .sum()
}

private fun inOrder(update: List<Int>, rules: Map<Int, Set<Int>>): Boolean {
    val handled = mutableSetOf<Int>()
    return update
        .all { pageNumber ->
            handled.add(pageNumber)
            rules.getOrDefault(pageNumber, emptySet()).none { handled.contains(it) }
        }
}

private fun order(update: List<Int>, rules: Map<Int, Set<Int>>): List<Int> {
    if(update.size == 1) return update
    val next = update
        .filter { pageNumber ->
            rules.filterKeys { it != pageNumber }
                .values.none { it.contains(pageNumber) }
        }.first()
    return listOf(next) + order(update - next, rules.filterKeys { it != next }.toMap())
}

private fun parseUpdates(input: List<String>) : List<List<Int>> {
    return input
        .dropWhile { it.contains("|") || it.isEmpty() }
        .map { line -> line.split(",").map { it.toInt() } }
}

private fun parseRules(input: List<String>) : Map<Int, Set<Int>> {
    return input
        .takeWhile { !it.isEmpty() }
        .map { line -> line.substringBefore("|").toInt() to line.substringAfter("|").toInt() }
        .fold(mutableMapOf<Int, MutableSet<Int>>()) { acc, rulePair ->
            acc.getOrPut(rulePair.first) { mutableSetOf() }.add(rulePair.second)
            acc
        }
}
