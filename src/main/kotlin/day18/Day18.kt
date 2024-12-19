package day18

import common.println

fun main() {
    (0 until 8)
        .forEach { (it xor 6).println()
        }

}