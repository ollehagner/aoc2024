package day20

import common.Direction
import common.Grid
import common.Point
import common.readInput
import java.util.PriorityQueue

const val WALL = '#'

fun main() {
    val testinput = readInput("main/kotlin/day20/testinput")
    val input = readInput("main/kotlin/day20/input")
    println("Part 1 No of cheats saving 100ps: ${part1(input)}")
}

fun part1(input: List<String>): Int {
    val grid = input.map { it.toCharArray().toList() }.let { Grid(it) }
    val start = grid.entries().find { it.value == 'S' }!!.key
    val end = grid.entries().find { it.value == 'E' }!!.key
    val baseline = solve(start, end, grid)
    return findPassableWalls(grid)
        .map { passableWall ->
            val gridCopy = grid.copy()
            gridCopy.set(passableWall, '.')
            solve(start, end, gridCopy)
        }
        .groupingBy { baseline - it }.eachCount()
        .filter { (save, _) -> save >=100 }
        .map { it.value }
        .sum()
}

fun solve(start: Point, end: Point, grid: Grid<Char>): Int {
    val queue = PriorityQueue<PathPosition>( compareBy( { it.minimumExpectedCost} ))
    queue.add(PathPosition(start, 0, end))
    val seen = mutableSetOf<Point>()
    while (queue.isNotEmpty()) {
        val current = queue.poll()
        if(current.isFinished()) return current.distance
        current.position
            .cardinalNeighbors()
            .filter { it !in seen }
            .filter { grid.valueOf(it) != WALL }
            .forEach {
                seen.add(it)
                queue.add(current.copy(position = it, distance = current.distance + 1))
            }
    }
    throw IllegalStateException("Should always have a solution")
}

fun findPassableWalls(grid: Grid<Char>): Set<Point> {
    return grid.entries()
        .filter { (position, _) -> position.x !in setOf(grid.min.x, grid.max.x) && position.y !in setOf(grid.min.y, grid.max.y) }
        .filter { (_, value) -> value == WALL }
        .filter { (position, _) ->
            (grid.valueOf(position.move(Direction.LEFT)) != WALL && grid.valueOf(position.move(Direction.RIGHT)) != WALL)
                    || (grid.valueOf(position.move(Direction.UP)) != WALL && grid.valueOf(position.move(Direction.DOWN)) != WALL)

        }
        .map{ it.key }
        .toSet()

}

data class PathPosition(val position: Point, val distance: Int, val end: Point) {
    val minimumExpectedCost = distance + position.manhattanDistance(end)

    fun isFinished(): Boolean = position == end
}

