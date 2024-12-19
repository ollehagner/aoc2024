package day17

import common.println
import common.readInput
import java.util.PriorityQueue
import kotlin.collections.set
import kotlin.math.pow

fun main() {
    val testinput = readInput("main/kotlin/day17/testinput2")
    val input = readInput("main/kotlin/day17/input")
    val programInput = ProgramInput.of(input)
    println("Part 1 output: ${part1(programInput)}")
    println("Part 2 Register A value: ${part2(programInput)}")
}

fun part1(programInput: ProgramInput): String {
    val program = Program(programInput.registers, programInput.instructions)
    program.run()
    program.registers.forEach { it.value.println() }
    return program.output.joinToString(",")
}

fun part2(programInput: ProgramInput): Long {

    val initialContext = ProgramContext(programInput.instructions, programInput.instructions, 0)
    val queue = PriorityQueue<ProgramContext>() { a, b -> a.initialAValue.compareTo(b.initialAValue) }
    queue.add(initialContext)

    while(queue.isNotEmpty()) {
        val current = queue.poll()
        if(current.expectedOutcomes.isEmpty()) {
            return current.initialAValue
        }
        findValidVariants(current)
            .forEach { initialAValue -> queue.add(current.copy(expectedOutcomes = current.expectedOutcomes.dropLast(1), initialAValue = initialAValue)) }
    }
    return 0
}

fun findValidVariants(programContext: ProgramContext): List<Long> {
    val wantedOutcome = programContext.expectedOutcomes.last()
    return (0..7)
        .map { mutableMapOf('A' to (programContext.initialAValue * 8 + it), 'B' to 0, 'C' to 0) }
        .map { registers ->
            val initialAValue = registers['A']!!
            val program = Program(registers, programContext.instructions)
            program.run()
            program.output to initialAValue
        }
        .filter { it.first.first() == wantedOutcome }
        .map { it.second }
}

data class ProgramContext(val instructions: List<Int>, val expectedOutcomes: List<Int>, val initialAValue: Long)

class Program(val registers: MutableMap<Char, Long>, val program: List<Int>) {

    var instructionPointer = 0
    val output = mutableListOf<Int>()

    fun run() {
        while (instructionPointer in (0 until program.size)) {
            val opcode = program[instructionPointer]
            val operand = program[instructionPointer + 1]
            when (opcode) {
                0 -> adv(operand)
                1 -> bxl(operand)
                2 -> bst(operand)
                3 -> jnz(operand)
                4 -> bxc(operand)
                5 -> out(operand)
                6 -> bdv(operand)
                7 -> cdv(operand)
                else -> throw IllegalArgumentException("Invalid opcode: $opcode")
            }
        }
    }

    fun adv(operand: Int) {
        registers['A'] = registers['A']!! / 2.pow(comboOperand(operand).toLong()).toInt()
        increasePointer()
    }

    fun bdv(operand: Int) {
        registers['B'] = registers['A']!! / 2.pow(comboOperand(operand).toLong()).toInt()
        increasePointer()
    }

    fun cdv(operand: Int) {
        registers['C'] = registers['A']!! / 2.pow(comboOperand(operand).toLong()).toInt()
        increasePointer()
    }

    fun bxl(operand: Int) {
        registers['B'] = registers['B']!! xor operand.toLong()
        increasePointer()
    }

    fun bst(operand: Int) {
        registers['B'] = comboOperand(operand) % 8L
        increasePointer()
    }

    fun jnz(operand: Int) {
        if (registers['A'] != 0L) {
            instructionPointer = operand
        } else {
            increasePointer()
        }
    }

    fun bxc(operand: Int) {
        registers['B'] = registers['B']!! xor registers['C']!!
        increasePointer()
    }

    fun out(operand: Int) {
        output.add((comboOperand(operand) % 8).toInt())
        increasePointer()
    }

    fun increasePointer() {
        instructionPointer += 2
    }

    fun comboOperand(value: Int): Long {
        return when (value) {
            in 0..3 -> value.toLong()
            4 -> registers['A']
            5 -> registers['B']
            6 -> registers['C']
            else -> throw IllegalStateException("Invalid operand value $value")
        }!!
    }

    override fun toString(): String {
        return output.joinToString(",")
    }
}

class ProgramInput(val registers: MutableMap<Char, Long>, val instructions: List<Int>) {
    companion object {
        fun of(input: List<String>): ProgramInput {
            val registers = mutableMapOf<Char, Long>()
            registers['A'] = input[0].substringAfter("A: ").toLong()
            registers['B'] = input[1].substringAfter("B: ").toLong()
            registers['C'] = input[2].substringAfter("C: ").toLong()
            val instructions = input[4].substringAfter("Program:").split(",").map { it.trim().toInt() }
            return ProgramInput(registers, instructions)
        }
    }
}

fun Long.pow(exp: Long): Long {
    return this.toDouble().pow(exp.toDouble()).toLong()
}
