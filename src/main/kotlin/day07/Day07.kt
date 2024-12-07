package day07

import common.readInput
import java.math.BigInteger

val operators = mapOf<Char, (Long, Long) -> Long>(
    '0' to Long::plus,
    '1' to Long::times,
    '2' to { a, b -> "$a$b".toLong() } )

fun main() {
    val testinput = readInput("main/kotlin/day07/testinput")
    val input = readInput("main/kotlin/day07/input")
    val equations = parseInput(input)
    println("Part 1 total calibration result ${part1(equations)}")
    println("Part 2 total calibration result ${part2(equations)}")
}

private fun part1(equations: List<Equation>): Long {
    return equations
        .filter { hasSolution(it, 2) }
        .sumOf { it.result }
}

private fun part2(equations: List<Equation>): Long {
    return equations
        .filter { hasSolution(it, 3) }
        .sumOf { it.result }
}

private fun parseInput(input: List<String>): List<Equation> {
    return input
        .map { line ->
            val sum = line.substringBefore(":").toLong()
            val terms = line.substringAfter(": ").split(" ").map { it.toLong() }
            Equation(sum, terms)
        }
}

private fun hasSolution(equation: Equation, base: Int): Boolean {
    val numOfOperators = equation.terms.size - 1
    val operationPermutations = (0 until base.pow(numOfOperators))
        .map { it.toString(base).padStart(numOfOperators, '0') }
        .map { binary -> binary.map { operators[it]!! } }

    return operationPermutations
        .any { operations ->
            calculate(equation.terms, operations) == equation.result
        }
}

private fun calculate(terms: List<Long>, operations: List<(Long, Long) -> Long>): Long {
    if(operations.isEmpty()) { return terms.first() }
    val result = operations.first()(terms[0], terms[1] )
    return calculate(listOf(result) + terms.drop(2), operations.drop(1))
}

data class Equation(val result: Long, val terms: List<Long>)

fun Int.pow(exp: Int): Long {
    return if (exp == -1) 0 else BigInteger.valueOf(this.toLong()).pow(exp).toLong()
}