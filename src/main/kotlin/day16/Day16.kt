package day16

import common.Direction
import common.Direction.*
import common.Grid
import common.Point
import common.readInput
import java.util.PriorityQueue

fun main() {
    val testinput = readInput("main/kotlin/day16/testinput")
    val input = readInput("main/kotlin/day16/input")
    val grid = parseToGrid(input)
    println("Part 1 cheapest path: ${part1(grid)}")
    println("Part 2 number of good seats: ${part2(grid)}")
}

fun part1(grid: Grid<MazePart>): Int {
    return solve(grid).first().cost
}

fun part2(grid: Grid<MazePart>): Int {
    val solutions = solve(grid)
    val minCost = solutions.minOf { it.cost }
    return solutions
        .filter { it.cost == minCost }
        .map { it.path() }
        .fold(mutableSetOf<Point>()) { acc, value ->
            acc.addAll(value)
            acc
        }
        .count()

}

fun solve(grid: Grid<MazePart>): List<Node> {
    val start = grid.entries().find { it.value == MazePart.START }!!.key
    val goal = grid.entries().find { it.value == MazePart.GOAL }!!.key
    val startNode = Node(start, RIGHT, 0, goal)

    val costMap = mutableMapOf<Pair<Point, Direction>, Int>(startNode.key() to startNode.leastPossibleCost())
    val queue = PriorityQueue<Node> { a, b -> a.leastPossibleCost().compareTo(b.leastPossibleCost()) }
    queue.add(startNode)

    val solutions = mutableListOf<Node>()

    while (queue.isNotEmpty()) {
        val current = queue.poll()
        if(current.position == goal) {
            solutions.add(current)
        }

        val next = findNextNodes(current, grid)
        next.filter { it.cost <= costMap.getOrDefault(it.key(), Int.MAX_VALUE) }
            .forEach {
                costMap[it.key()] = it.cost
                queue.add(it)
            }
    }
    return solutions
}

fun findNextNodes(node: Node, grid: Grid<MazePart>): List<Node> {
    return with(node) {
        listOf(
            copy(position = position.move(direction), cost = cost + 1, parent=this),
            copy(position = position.move(direction.rotateLeft()), direction = direction.rotateLeft(), cost = cost + 1001, parent=this),
            copy(position = position.move(direction.rotateRight()), direction = direction.rotateRight(), cost = cost + 1001, parent=this)
        )
            .filter { grid.valueOf(it.position) != MazePart.WALL }
    }
}

fun parseToGrid(input: List<String>): Grid<MazePart> {
    return input.map { it.toCharArray().map { MazePart.of(it) } }.let { Grid(it) }
}


data class Node(val position: Point, val direction: Direction, val cost: Int, val goal: Point, val parent: Node? = null) {
    fun leastPossibleCost(): Int {
        return cost + position.manhattanDistance(goal)
    }

    fun key(): Pair<Point, Direction> {
        return position to direction
    }

    fun path(): Set<Point> {
        return generateSequence(this) { node ->
            node.parent
        }
            .fold(mutableSetOf(this.position)) { acc, node ->
                acc.add(node.position)
                acc
            }
    }

}

enum class MazePart(val symbol: Char) {
    WALL('#'), EMPTY('.'), START('S'), GOAL('E'), DOWN('^'), UP('v'), LEFT('<'), RIGHT('>');

    companion object {
        fun of(input: Char): MazePart {
            return MazePart.entries.find { it.symbol == input }!!
        }
    }
}