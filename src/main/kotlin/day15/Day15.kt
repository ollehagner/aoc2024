package day15

import common.Direction
import common.Grid
import common.Point
import common.println
import common.readInput
import day15.WarehouseObject.*

fun main() {
    val testinput = readInput("main/kotlin/day15/testinput3")
    val input = readInput("main/kotlin/day15/input")
    println("Part 1 sum of coordinates= ${part1(parseInput(testinput))}")
    println("Part 2 sum of coordinates= ${part2(parseInput(input, true))}")
}

private fun part1(input: Pair<Grid<WarehouseObject>, List<Direction>>): Int {
    val endState = performAllMoves(input)
    return endState.grid
        .entries()
        .filter { (_, warehouseObject) -> warehouseObject == BOX }
        .map { it.key }
        .map { point -> point.x + point.y * 100 }
        .sum()
}

fun part2(input: Pair<Grid<WarehouseObject>, List<Direction>>): Int {
    val endState = performAllMoves(input)
    return endState.grid
        .entries()
        .filter { (_, warehouseObject) -> warehouseObject == LEFTBOX }
        .map { it.key }
        .map { point -> point.x + point.y * 100 }
        .sum()
}

private fun performAllMoves(input: Pair<Grid<WarehouseObject>, List<Direction>>): State {
    val grid = input.first
    val movements = input.second
    val robotPosition = grid.entries()
        .first { (_, warehouseObject) -> warehouseObject == ROBOT }.key
    return movements
        .fold(State(grid, robotPosition)) { acc, movement ->
            move(acc, movement)
        }
}

private fun move(state: State, direction: Direction): State {
    val toMove = findObjectsToMove(setOf(state.robotPosition), direction, state.grid)
    val newPositionsAndValues = toMove
        .map { it.move(direction) to state.grid.valueOf(it) }
    toMove.forEach { state.grid.set(it, EMPTY) }
    newPositionsAndValues.forEach { (position, value) -> state.grid.set(position, value) }
    return state.copy(robotPosition = if (toMove.isNotEmpty()) state.robotPosition.move(direction) else state.robotPosition)

}

private fun findObjectsToMove(
    objectsToMove: Set<Point>,
    direction: Direction,
    grid: Grid<WarehouseObject>
): Set<Point> {
    val nextEdge = findEdge(objectsToMove, direction)
        .map { it.move(direction) }
        .map { it to grid.valueOf(it) }
    if (nextEdge.any { (_, value) -> value == WALL }) {
        return emptySet()
    } else if (nextEdge.all { (_, value) -> value == EMPTY }) {
        return objectsToMove
    }
    val toAdd = nextEdge.flatMap { (point, warehouseObject) ->
        when (warehouseObject) {
            LEFTBOX -> listOf(point, point.move(Direction.RIGHT))
            RIGHTBOX -> listOf(point, point.move(Direction.LEFT))
            BOX -> listOf(point)
            else -> emptyList()
        }
    }
    return findObjectsToMove(objectsToMove + toAdd, direction, grid)
}

private fun findEdge(objectsToMove: Set<Point>, direction: Direction): List<Point> {
    return when (direction) {
        Direction.LEFT -> objectsToMove.groupBy { it.x }.minBy { it.key }.value
        Direction.RIGHT -> objectsToMove.groupBy { it.x }.maxBy { it.key }.value
        Direction.UP -> objectsToMove.groupBy { it.y }.maxBy { it.key }.value
        Direction.DOWN -> objectsToMove.groupBy { it.y }.minBy { it.key }.value
        else -> throw IllegalArgumentException("Invalid direction $direction")
    }
}

private fun parseInput(input: List<String>, expanded: Boolean = false): Pair<Grid<WarehouseObject>, List<Direction>> {
    val grid = input
        .takeWhile { it.isNotEmpty() }
        .map { line ->
            line.toCharArray().flatMap { if (expanded) WarehouseObject.ofExpanded(it) else WarehouseObject.of(it) }
        }
        .let { Grid(it) }

    val movements = input.dropWhile { it.isNotEmpty() }
        .drop(1)
        .flatMap {
            it.toCharArray().map {
                when (it) {
                    '<' -> Direction.LEFT
                    '>' -> Direction.RIGHT
                    '^' -> Direction.DOWN
                    'v' -> Direction.UP
                    else -> throw IllegalArgumentException("Unknown direction: $it")
                }
            }
        }
    return grid to movements
}

enum class WarehouseObject(val symbol: Char) {
    ROBOT('@'), WALL('#'), BOX('O'), EMPTY('.'), LEFTBOX('['), RIGHTBOX(']');

    companion object {
        fun of(symbol: Char): List<WarehouseObject> = listOf(WarehouseObject.entries.first { it.symbol == symbol })
        fun ofExpanded(symbol: Char): List<WarehouseObject> {
            return when (WarehouseObject.of(symbol).first()) {
                ROBOT -> listOf(ROBOT, EMPTY)
                WALL -> listOf(WALL, WALL)
                BOX -> listOf(LEFTBOX, RIGHTBOX)
                EMPTY -> listOf(EMPTY, EMPTY)
                else -> throw IllegalArgumentException("Unknown warehouseObject: $symbol")
            }
        }
    }
}

data class State(val grid: Grid<WarehouseObject>, val robotPosition: Point)