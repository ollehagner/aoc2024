package day20

import common.Grid
import common.Point
import common.readInput
import java.util.PriorityQueue

const val WALL = '#'

fun main() {
    val testinput = readInput("main/kotlin/day20/testinput")
    val input = readInput("main/kotlin/day20/input")

    println("Part 1 No of cheats saving 100ps: ${part1(input)}")
    println("Part 2 No of cheats saving 100ps: ${part2(input)}")
}

fun part1(input: List<String>): Int {
    return solve(input, 2, 100)
}

fun part2(input: List<String>): Int {
    return solve(input, 20, 100)
}

fun solve(input: List<String>, maxCheatSeconds: Int, minimumSave: Int): Int {
    val path = traverseTrack(input)
    val cheats = findShortcuts(path, maxCheatSeconds, minimumSave)
    return cheats
        .values
        .sum()
}

fun traverseTrack(input: List<String>): Set<PathPosition> {
    val grid = input.map { it.toCharArray().toList() }.let { Grid(it) }
    val start = grid.entries().find { it.value == 'S' }!!.key
    val end = grid.entries().find { it.value == 'E' }!!.key
    val queue = PriorityQueue<PathPosition>( compareBy( { it.minimumExpectedCost} ))
    val startPathPosition = PathPosition(start, 0, end)
    queue.add(startPathPosition)
    val seen = mutableSetOf<PathPosition>()
    seen.add(startPathPosition)
    while (queue.isNotEmpty()) {
        val current = queue.poll()
        if(current.isFinished()) break
        current.position
            .cardinalNeighbors()
            .filter { grid.valueOf(it) != WALL }
            .map { current.copy(position = it, distance = current.distance + 1) }
            .filter { it !in seen }
            .forEach {
                seen.add(it)
                queue.add(it)
            }
    }
    return seen
}

fun findShortcuts(path: Set<PathPosition>, maximumCheatSeconds: Int, minimumCheatSave: Int = 0): Map<Int, Int> {
    return path
        .flatMap { pathPosition ->
            path
                .filter { pathPosition.position.manhattanDistance(it.position) <= maximumCheatSeconds }
                .map {
                    val saved = it.distance - pathPosition.distance - pathPosition.position.manhattanDistance(it.position)
                    saved
                }
                .filter { it >= minimumCheatSave }
        }
        .groupingBy { it }.eachCount()
}

data class PathPosition(val position: Point, val distance: Int, val end: Point) {
    val minimumExpectedCost = distance + position.manhattanDistance(end)

    fun isFinished(): Boolean = position == end

    override fun equals(other: Any?): Boolean {
        return other is PathPosition && position == other.position
    }

    override fun hashCode(): Int {
        return position.hashCode()
    }
}

