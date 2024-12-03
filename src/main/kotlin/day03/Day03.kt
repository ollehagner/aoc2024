package day03

import common.readInput

const val MULTIPLICATION_INSTRUCTION_PATTERN = "mul\\(\\d{1,3},\\d{1,3}\\)"

fun main() {
    val testinput = readInput("main/kotlin/day03/testinput")
    val testinput2 = listOf("xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))")
    val input = readInput("main/kotlin/day03/input")

    println("Part 1 Resulting sum = ${part1(input)}")
    println("Part 2 Resulting sum = ${part2(input)}")
}

fun part1(input: List<String>): Long {
    val multiplicationRegex = MULTIPLICATION_INSTRUCTION_PATTERN.toRegex()

    return input
        .flatMap { line -> multiplicationRegex.findAll(line) }
        .map { multiplicationMatch ->
            val multiplication = multiplicationMatch.value
            multiply(multiplication)
        }
        .sum()
}

fun part2(input: List<String>): Long {
    val instructionRegex = "$MULTIPLICATION_INSTRUCTION_PATTERN|don't\\(\\)|do\\(\\)".toRegex()
    return input
        .flatMap { line -> instructionRegex.findAll(line) }
        .map { it.value }
        .fold(MultiplicationState(true, 0L)) {
            acc, instruction ->
                when(instruction) {
                    "do()" -> MultiplicationState(true, acc.value)
                    "don't()" -> MultiplicationState(false, acc.value)
                    else -> if(acc.enabled) MultiplicationState(true, acc.value + multiply(instruction)) else acc
                }
        }
        .value
}

fun multiply(instruction: String): Long {
    val firstTerm = instruction.substringAfter("mul(").substringBefore(",").toLong()
    val secondTerm = instruction.substringAfter(",").substringBefore(")").toLong()
    return firstTerm * secondTerm
}

class MultiplicationState(val enabled: Boolean, val value: Long)