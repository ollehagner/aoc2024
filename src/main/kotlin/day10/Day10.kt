package day10

import common.Grid
import common.Point
import common.readInput

fun main() {
    val testinput = readInput("main/kotlin/day10/testinput")
    val input = readInput("main/kotlin/day10/input")
    val grid = Grid<Int>(input.map { it.toCharArray().map { it.digitToInt() }.toList() })
    println("Part 1 total trailheads: ${part1(grid)}")
    println("Part 2 total trailheads: ${part2(grid)}")
}

private fun part1(grid: Grid<Int>): Int {
    return startingPoints(grid)
        .sumOf { findTrails(it, grid).distinct().count() }
}

private fun part2(grid: Grid<Int>): Int {
    return startingPoints(grid)
        .sumOf { findTrails(it, grid).count() }
}

fun startingPoints(grid: Grid<Int>): List<Point> {
    return grid.entries()
        .filter { (_, value) -> value == 0 }
        .map { it.key }
}

fun findTrails(currentPosition: Point, grid: Grid<Int>): List<Point> {
    val currentValue = grid.valueOf(currentPosition)
    if (currentValue == 9) {
        return listOf(currentPosition)
    }
    val pointsToExplore = currentPosition.cardinalNeighbors()
        .filter { neighbor -> grid.maybeValue(neighbor) == (currentValue + 1) }
    return if(pointsToExplore.isNotEmpty()) pointsToExplore.flatMap { findTrails(it, grid) }.toList() else emptyList()
}



