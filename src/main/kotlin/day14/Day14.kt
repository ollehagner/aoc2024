package day14

import common.Point
import common.readInput

fun main() {
    val testinput = readInput("main/kotlin/day14/testinput")
    val testGridSize = Point(11, 7)
    val input = readInput("main/kotlin/day14/input")
    val gridSize = Point(101, 103)
    val robots = parseToRobots(input, gridSize)
    println("Part 1 safety factor: ${part1(robots)}")
    println("Part 2 christmas tree seconds: ${part2(robots)}")
}

fun part1(robots: List<Robot>): Int {
    return moveRobots(robots)
        .drop(100)
        .first().let { calculateSafetyFactor(it) }
}

fun part2(robots: List<Robot>): Int {
    return moveRobots(robots)
        .map { robots -> robots.map { it.position } }
        .takeWhile { it.toSet().size != robots.size }
        .count()
}

fun moveRobots(robots: List<Robot>): Sequence<List<Robot>> = generateSequence(robots) { acc ->
    acc.map { it.move() }
}

fun parseToRobots(input: List<String>, gridSize: Point): List<Robot> {
    return input.map { line ->
        val position = line.substringAfter("p=").substringBefore(" v").split(",")
            .let { (x, y) -> Point(x.trim().toInt(), y.trim().toInt()) }
        val velocity = line.substringAfter("v=").split(",").let { (x, y) -> Velocity(x.toInt(), y.toInt()) }
        Robot(position, velocity, gridSize)
    }
}

fun calculateSafetyFactor(robots: List<Robot>): Int {
    require(robots.isNotEmpty()) { "There must be at least one robot" }

    return toQuadrants(robots)
        .fold(1) { acc, value -> acc * value.size }
}

fun toQuadrants(robots: List<Robot>): List<List<Robot>> {
    val gridSize = robots.first().gridSize
    val halfX = (gridSize.x / 2)
    val halfY = (gridSize.y / 2)
    return robots
        .filter { it.position.x != halfX && it.position.y != halfY }
        .map { robot ->
            val quadrant = when {
                robot.position.x < halfX && robot.position.y < halfY -> 1
                robot.position.x > halfX && robot.position.y < halfY -> 2
                robot.position.x < halfX && robot.position.y > halfY -> 3
                else -> 4
            }
            quadrant to robot
        }
        .fold(mutableMapOf<Int, MutableList<Robot>>()) { acc, (quadrant, robot) ->
            acc.getOrPut(quadrant) { mutableListOf<Robot>() }.add(robot)
            acc
        }
        .values.toList()
}

data class Robot(val position: Point, val velocity: Velocity, val gridSize: Point) {
    fun move(): Robot {
        val moved = position.move(velocity.horizontal, velocity.vertical)
        val newX = if (moved.x < 0) gridSize.x + moved.x else moved.x % gridSize.x
        val newY = if (moved.y < 0) gridSize.y + moved.y else moved.y % gridSize.y
        return copy(position = Point(newX, newY))
    }
}

data class Velocity(val horizontal: Int, val vertical: Int)