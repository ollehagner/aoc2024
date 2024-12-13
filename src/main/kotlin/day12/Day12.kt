package day12

import common.Direction
import common.Direction.*
import common.Grid
import common.Point
import common.readInput
import java.util.LinkedList

fun main() {
    val testinput = readInput("main/kotlin/day12/testinput4")
    val input = readInput("main/kotlin/day12/input")
    val grid = Grid(input.map { it.toCharArray().toList() })
    println("Part 1 total price: ${part1(grid)}")
    println("Part 2 total price: ${part2(grid)}")
}

fun part1(grid: Grid<Char>): Int {
    return regions(grid).sumOf { it.price() }
}

fun part2(grid: Grid<Char>): Int {
    return regions(grid)
        .sumOf {
            val edges = countEdges(it)
            it.area() * edges
        }
}

private fun regions(grid: Grid<Char>): List<Region> {
    val handled = mutableSetOf<Point>()
    val positions = LinkedList(grid.entries()
        .map { it.key })
    val regions = mutableListOf<Region>()

    while (positions.isNotEmpty()) {
        val position = positions.removeFirst()
        if (position !in handled) {
            val region = findRegion(position, grid)
            handled.addAll(region.positions())
            regions.add(region)
        }
    }
    return regions
}

private fun findRegion(start: Point, grid: Grid<Char>): Region {
    val queue = LinkedList<Plot>().also { it.add(toPlot(start, grid)!!) }
    val seen = mutableSetOf<Point>().also { it.add(start) }
    val region = Region()
    while (queue.isNotEmpty()) {
        val currentPlot = queue.pop()
        region.add(currentPlot)
        currentPlot.position.cardinalNeighbors()
            .map { position ->
                toPlot(position, grid)
            }
            .filterNotNull()
            .filter { it.type == currentPlot.type && it.position !in seen }
            .forEach {
                queue.add(it)
                seen.add(it.position)
            }
    }
    return region
}

fun countEdges(region: Region): Int {
    var size = region.plots.size
    var sides = region.plots
        .flatMap { plot ->
            plot.edges.map { Side(IntRange(plot.position.x, plot.position.x), IntRange(plot.position.y, plot.position.y), it.direction) }
        }.toMutableSet()

    do {
        size = sides.size
        sides = sides
            .fold(mutableSetOf<Side>()) { acc, value ->
                val adjacent = acc.filter { it.isAdjacent(value) }
                if(adjacent.isNotEmpty()) {
                    val merged =
                        (adjacent + value)
                            .reduce { a, b -> a.merge(b) }
                    adjacent.forEach { acc.remove(it) }
                    acc.add(merged)
                } else {
                    acc.add(value)
                }
                acc
            }
    } while (size != sides.size)

    return sides.size
}


fun toPlot(position: Point, grid: Grid<Char>): Plot? {
    return grid.maybeValue(position)
        ?.let { type ->
            val edges = listOf(UP, DOWN, LEFT, RIGHT)
                .map { direction -> grid.maybeValue(position.move(direction)) to direction }
                .filter { (neighborType, _) -> neighborType != type }
                .map { Edge(it.second) }
            Plot(type, position, edges)
        }
}

data class Plot(val type: Char, val position: Point, val edges: List<Edge>)

data class Edge(val direction: Direction)

data class Side(val xValues: IntRange, val yValues: IntRange, val direction: Direction) {
    fun merge(other: Side): Side {
        require(other.direction == direction) { "Can only merge sides with same direction" }
        return when (direction) {
            LEFT, RIGHT -> this.copy(
                yValues = IntRange(
                    minOf(yValues.first, other.yValues.first()),
                    maxOf(yValues.last, other.yValues.last())
                )
            )
            else -> this.copy(
                xValues = IntRange(
                    minOf(xValues.first, other.xValues.first()),
                    maxOf(xValues.last, other.xValues.last())
                )
            )
        }
    }

    fun isAdjacent(other: Side): Boolean {
        return this.direction == other.direction &&
                when(direction) {
                    UP, DOWN -> (xValues.contains(other.xValues.first - 1) || xValues.contains(other.xValues.last() + 1)) && yValues == other.yValues
                    else -> (yValues.contains(other.yValues.first - 1) || yValues.contains(other.yValues.last() + 1)) && xValues == other.xValues
                }
    }

}

data class Region(val plots: MutableList<Plot> = mutableListOf<Plot>()) {

    fun add(plot: Plot) {
        plots.add(plot)
    }

    fun area() = plots.size

    fun perimeter(): Int = plots
        .sumOf { it.edges.size }

    fun price(): Int {
        return area() * perimeter()
    }

    fun positions(): List<Point> {
        return plots.map { it.position }
    }

}