package day08

import common.Grid
import common.Point
import common.readInput

fun main() {
    val testinput = readInput("main/kotlin/day08/testinput")
    val input = readInput("main/kotlin/day08/input")

    val grid = Grid(input) { point, value -> value }
    println("Part 1 num of antinodes: ${part1(grid)}")
    println("Part 2 num of antinodes: ${part2(grid)}")
}

private const val EMPTY = '.'

fun part1(grid: Grid<Char>): Int {
   val nodePairs = findNodePairs(grid)
   return nodePairs
       .map { it.first.move(it.first - it.second) }
       .filter { grid.hasValue(it) }
       .distinct()
       .count()
}

fun part2(grid: Grid<Char>): Int {
    val nodePairs = findNodePairs(grid)
    return (nodePairs
        .flatMap {
            findAllInLine(it.first, it.first - it.second, grid).toList()
        }
        .toSet() + nodePairs.map { it.first }.toSet()).count()
}

fun findAllInLine(start: Point, vector: Point, grid: Grid<Char>): Sequence<Point> = sequence {
    var current = start
    while(grid.hasValue(current.move(vector))) {
        current = current.move(vector)
        yield(current)
    }
}

fun findNodePairs(grid: Grid<Char>) : List<Pair<Point, Point>> {
    return grid.entries()
        .filter { (_, value) -> value != EMPTY }
        .groupBy( {it.value}, { it.key } )
        .values
        .filter { it.size > 1 }
        .map { nodes ->
            nodes
                .flatMap { source ->
                    nodes
                        .filter { it != source }
                        .map { destination ->
                            source to destination
                        }
                }
        }.flatten()
}