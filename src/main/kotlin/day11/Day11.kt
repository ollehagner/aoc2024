package day11

import common.infiniteSequence
import common.readInput

fun main() {
    val testinput: List<Long> = listOf(125, 17)
    val input = readInput("main/kotlin/day11/input").first().split(" ").map { it.toLong() }
    println("Part 1 Num of stones: ${part1(input)}")
    println("Part 2 Num of stones: ${part2(input)}")
}

private fun part1(stones: List<Long>): Long {
    return countStones(stones, 25)
}

private fun part2(stones: List<Long>): Long {
    return countStones(stones, 75)
}

private fun countStones(stones: List<Long>, iterations: Int): Long {
    val values = stones.groupingBy { it }.fold(0L) { acc, value -> acc + 1 }
    return infiniteSequence(0)
        .runningFold(values) { acc, value ->
            buildMap<Long, Long> {
                acc
                    .entries
                    .flatMap { (stone, count) ->
                        applyRules(stone)
                            .map { it to count }
                    }
                    .forEach { (stone, count) ->
                        put(stone, getOrDefault(stone, 0) + count)
                    }
            }
        }
        .take(iterations + 1)
        .last()
        .values
        .sumOf { it }
}

fun applyRules(value: Long): List<Long> {
    val valueAsString = "$value"
    val numOfDigits = valueAsString.length
    return when {
        value == 0L -> listOf(1)
        numOfDigits % 2 == 0 -> listOf(valueAsString.substring(0, numOfDigits / 2).toLong(), valueAsString.substring(numOfDigits / 2).toLong())
        else -> listOf(value * 2024)
    }
}