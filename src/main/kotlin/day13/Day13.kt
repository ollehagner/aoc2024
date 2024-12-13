package day13

import common.readInput
import kotlin.ranges.contains

fun main() {
    val testinput = readInput("main/kotlin/day13/testinput")
    val input = readInput("main/kotlin/day13/input")
    val machines = parseInput(input)
    println("Part 1 least cost: ${part1(machines)} tokens")
    println("Part 2 least cost: ${part2(machines)} tokens")

}

private fun part1(machines: List<ClawMachine>): Long {
    return machines
        .map { it to calculateNumOfButtonAPresses(it) }
        .filter { (machine, aPresses) -> machine.isValid(aPresses) }
        .filter { (machine, aPresses) -> aPresses in (0..100) && machine.buttonBPresses(aPresses) in (0..100)}
        .sumOf { (machine, aPresses) -> machine.cost(aPresses) }
}

private fun part2(machines: List<ClawMachine>): Long {
    return machines
        .map { it.copy(target = Pair(it.target.first + 10000000000000, it.target.second + 10000000000000)) }
        .map { it to calculateNumOfButtonAPresses(it) }
        .filter { (machine, aPresses) -> machine.isValid(aPresses) }
        .sumOf { (machine, aPresses) -> machine.cost(aPresses) }
}

fun calculateNumOfButtonAPresses(machine: ClawMachine): Long {
    return with(machine) { (target.first * buttonB.second - buttonB.first * target.second) / ((buttonA.first * buttonB.second) - (buttonB.first * buttonA.second)) }
}

fun parseInput(input: List<String>): List<ClawMachine> {
    return input.chunked(4)
        .map { rows ->
            val buttonA = parseButton(rows[0])
            val buttonB = parseButton(rows[1])
            val target = Pair(
                rows[2].substringAfter("X=").substringBefore(",").toLong(),
                rows[2].substringAfter("Y=").toLong()
            )
            ClawMachine(buttonA, buttonB, target)
        }
}

fun parseButton(row: String): Pair<Long,Long> {
    val x = row.substringAfter("X+").substringBefore(",").toLong()
    val y = row.substringAfter("Y+").toLong()
    return Pair<Long,Long>(x, y)
}


data class ClawMachine(val buttonA: Pair<Long,Long>, val buttonB: Pair<Long,Long>, val target: Pair<Long, Long>) {

    fun distanceLeft(aButtonPresses: Long): Pair<Long, Long> {
        val vector = pushAButton(aButtonPresses)
        return Pair(target.first - vector.first, target.second - vector.second)
    }

    fun pushAButton(times: Long): Pair<Long,Long> {
        return Pair(buttonA.first * times, buttonA.second * times)
    }

    fun buttonBPresses(aButtonPresses: Long): Long {
        return distanceLeft(aButtonPresses).first / buttonB.first
    }

    fun cost(aButtonPresses: Long): Long {
        return aButtonPresses * 3 + buttonBPresses(aButtonPresses)
    }

    fun isValid(aButtonPresses: Long): Boolean {
        val distanceLeft = distanceLeft(aButtonPresses)
        return distanceLeft.first % buttonB.first == 0L
                && distanceLeft.second % buttonB.second == 0L
                && (distanceLeft.first / buttonB.first) == (distanceLeft.second / buttonB.second)
    }

}