package day10

import java.util.Optional

fun main() {
    Optional.ofNullable(2).ifPresent { println(it) }
}