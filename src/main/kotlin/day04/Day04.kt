package day04

import common.Direction
import common.Direction.*
import common.Grid
import common.Point
import common.readInput

fun main() {
    val grid = parseToGrid(readInput("main/kotlin/day04/input"))
    println("Part 1 Number of xmas: ${part1(grid)}")
    println("Part 2 Number of xmas: ${part2(grid)}")
}

fun part1(grid: Grid<Char>): Int {
    val startingPoints = grid.entries().filter { (_, value) -> value == 'X' }.map { it.key }

    return startingPoints.flatMap { startingPoint ->
            Direction.entries
                .filter { it != NONE }
                .map { direction -> Point.sequence(startingPoint, direction).drop(1).take(3).iterator() }
        }
        .filter { spellsXmas(grid, it) }
        .count()
}

fun part2(grid: Grid<Char>): Int {
    val startingPoints = grid.entries().filter { (_, value) -> value == 'A' }.map { it.key }
    return startingPoints
        .filter { point ->
            hasDiagonalMas(grid, point)
        }
        .count()
}

fun spellsXmas(grid: Grid<Char>, points: Iterator<Point>): Boolean {
    return grid.valueOrDefault(points.next(), ' ') == 'M'
            && grid.valueOrDefault(points.next(), ' ') == 'A'
            && grid.valueOrDefault(points.next(), ' ') == 'S'
}

fun hasDiagonalMas(grid: Grid<Char>, point: Point): Boolean {
    val diagonalLetters = setOf('M', 'S')
    return setOf(grid.valueOrDefault(point.move(UP_LEFT), ' '), grid.valueOrDefault(point.move(DOWN_RIGHT), ' ')) == diagonalLetters
            && setOf(grid.valueOrDefault(point.move(UP_RIGHT), ' '), grid.valueOrDefault(point.move(DOWN_LEFT), ' ')) == diagonalLetters
}

fun parseToGrid(input: List<String>): Grid<Char> {
    return Grid(input.map { line -> line.toCharArray().toList() })
}