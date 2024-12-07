package day06

import common.Direction
import common.Direction.*
import common.Grid
import common.Point
import common.readInput

val directions = mapOf(
    '>' to RIGHT, '<' to LEFT, '^' to UP, 'v' to DOWN
)
const val OBSTACLE = '#'
const val FLOOR = '.'
const val START = '^'

fun main() {
    val testinput = readInput("main/kotlin/day06/testinput")
    val input = readInput("main/kotlin/day06/input")
    val grid = parseToGrid(input)
    println("Part 1 num of visited: ${part1(grid)}")
    println("Part 2 num of loops: ${part2(grid)}")
}

private fun part1(grid: Grid<Char>): Int {
    val start = grid.entries()
        .first { it.value == START }
    var position = start.key
    var direction: Direction = directions[start.value]!!
    val visited = mutableSetOf(position)
    while(grid.hasValue(position.move(direction))) {
        val nextPosition = position.move(direction)
        if(grid.valueOf(nextPosition) == OBSTACLE) {
            direction = direction.rotateRight()
        } else {
            position = nextPosition
            visited.add(position)
        }
    }
    return visited.size
}

private fun part2(grid: Grid<Char>): Int {
    val start = grid.entries()
        .first { it.value == START }
    var position = start.key
    var direction: Direction = directions[start.value]!!
    val startingState = State(position, direction)
    return grid.entries()
        .filter { it.value == FLOOR }
        .count { (point, _) -> isLoop(addObstacleToGrid(grid, point), startingState) }
}

private fun isLoop(grid: Grid<Char>, start: State): Boolean {
    var position = start.position
    var direction = start.direction
    val visited = mutableSetOf(State(position, direction))

    while(grid.hasValue(position.move(direction)) && !(visited.contains(State(position.move(direction), direction)))) {
        val nextPosition = position.move(direction)
        if(grid.valueOf(nextPosition) == OBSTACLE) {
            direction = direction.rotateRight()
        } else {
            position = nextPosition
        }
        visited.add(State(position, direction))
    }
    return grid.hasValue(position.move(direction))
}

private fun addObstacleToGrid(grid: Grid<Char>, point: Point): Grid<Char> {
    val copy = grid.entries()
    .filter { it.key != point }
    .map { it.key to it.value }
    .toMap()
    .toMutableMap().also { it.put(point, OBSTACLE) }
    return Grid(copy)
}

private fun parseToGrid(input: List<String>): Grid<Char> {
    return Grid(input.reversed().map { row -> row.toCharArray().toList() })
}

fun Direction.rotateRight(): Direction {
    return when(this) {
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
        UP -> RIGHT
        else -> throw IllegalStateException("Unknown direction")
    }
}

private data class State(val position: Point, val direction: Direction)

