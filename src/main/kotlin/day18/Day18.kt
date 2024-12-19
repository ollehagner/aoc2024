package day18

import common.Grid
import common.Point
import common.readInput
import java.util.PriorityQueue
import java.util.concurrent.TimeUnit

fun main() {
    val testinput = readInput("main/kotlin/day18/testinput").map { it.toPoint() }
    val input = readInput("main/kotlin/day18/input").map { it.toPoint() }
    val testEndPoints = Point(0, 0) to Point(6,6)
    val endPoints = Point(0, 0) to Point(70, 70)
    println("Part 1 minimum steps: ${part1(input, endPoints)}")
    println("Part 2 last byte: ${part2(input, endPoints)}")
}

fun part1(input: List<Point>, endPoints: Pair<Point, Point>): Int {
    val bytes = input.take(1024)
    val grid = Grid<Char>()
    grid.set(endPoints.first, 'S')
    grid.set(endPoints.second, 'E')
    bytes.forEach { grid.set(it, '#') }
    return solve(grid, endPoints)
}

fun part2(input: List<Point>, endPoints: Pair<Point, Point>): Point {
    val grid = Grid<Char>()
    grid.set(endPoints.first, 'S')
    grid.set(endPoints.second, 'E')
    return input
        .asSequence()
        .map {
            grid.set(it, '#')
            it to solve(grid, endPoints)
        }
        .first { it.second == 0 }.first
}

fun solve(grid: Grid<Char>, endPoints: Pair<Point, Point>): Int {
    val start = endPoints.first
    val goal = endPoints.second
    val queue = PriorityQueue<Pair<Point, Int>>(compareBy { (position, pathSoFar) -> pathSoFar + position.manhattanDistance(goal) })
    val seen = mutableSetOf<Point>()
    queue.add(Pair(start, 0))
    val startTime = System.nanoTime()
    while (queue.isNotEmpty()) {
        val current = queue.poll()
        if(current.first == goal) {
            println("Solved in ${TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)} ms")
            return current.second
        }
        current.first.cardinalNeighbors()
            .filter { it !in seen }
            .filter { grid.withinBounds(it) && grid.valueOrDefault(it, '.') != '#' }
            .map { it to current.second + 1 }
            .forEach {
                seen.add(it.first)
                queue.add(it)
            }
    }
    return 0
}

private fun String.toPoint(): Point {
    return Point(this.split(",")[0].toInt(), this.split(",")[1].toInt())
}