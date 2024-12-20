package day19

import common.readInput

fun main() {
    val testinput = readInput("main/kotlin/day19/testinput")
    val input = readInput("main/kotlin/day19/input")
    println("Part 1 num of possible designs: ${part1(input)}")
    println("Part 2 all possible variants: ${part2(input)}")
}

var designs: Set<String> = setOf()
val cache = mutableMapOf<String, Boolean>()
val countCache = mutableMapOf<String, Long>()

fun part1(input: List<String>): Int {
    cache.clear()
    designs = input.first().split(",").map { it.trim() }.toSet()
    val towels = input.drop(2).toSet()

    return towels
        .count { designExists(it) }
}

fun part2(input: List<String>): Long {
    cache.clear()
    designs = input.first().split(",").map { it.trim() }.toSet()
    val towels = input.drop(2).toSet()
    return towels.map { findAllDesigns(it) }.sum()
}

fun designExists(target: String): Boolean {
    cache[target]?.let { return it }
    if (target.isEmpty()) {
        cache[target] = true
        return true
    }
    return designs
        .filter { target.startsWith(it) }
        .any { designExists(target.substring(it.length)) }.also { cache[target] = it }
}

fun findAllDesigns(target: String): Long {
    countCache[target]?.let {
        return it
    }
    if (target.isEmpty()) {
        countCache[target] = 1
        return 1
    }
    return designs
        .filter { target.startsWith(it) }
        .map { findAllDesigns(target.substring(it.length)) }
        .sum().also { countCache[target] = countCache.getOrDefault(target, 0) + it }
}



