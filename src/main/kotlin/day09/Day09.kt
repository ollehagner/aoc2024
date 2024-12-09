package day09

import common.println
import common.readInput
import java.util.Optional

fun main() {
    val testinput = "2333133121414131402"
    val input = readInput("main/kotlin/day09/input").first()
    val diskData = parseDiskData(input)
    println("Part 1 checksum: ${part1(diskData)}")
    println("Part 2 checksum: ${part2(diskData)}")
}

fun part1(diskData: List<Block>): Long {

    var reversed = reverseBlocks(diskData)

    val totalDataBlocks = diskData.filter { it is DataBlock }.sumOf { it.size() }

    val flattened =
        diskData.flatMap { block ->
            (0 until block.size())
                .map { _ ->
                    when (block) {
                        is DataBlock -> DataBlock(block.id, 1)
                        is EmptyBlock -> EmptyBlock(1)
                    }
                }

        }

    return flattened
        .fold(Pair(0, 0L)) { acc, block ->
            var index = acc.first
            if (index < totalDataBlocks) {
                val checksum = acc.second + when (block) {
                    is DataBlock -> {
                        block.checksum(index++)
                    }
                    is EmptyBlock -> {
                        reversed.take(1)
                            .sumOf { it.checksum(index++) }
                    }
                }
                Pair(index, checksum)
            } else acc
        }
        .second
}

fun part2(diskData: List<Block>): Long {
    val copy = diskData.toMutableList()

    val reversed = copy.filter { it is DataBlock }.reversed()

    (reversed)
        .fold(copy) { blockList, blockToCheck ->
            val firstFreeIndex = freeSpaceIndex(blockList, blockToCheck)
            if (firstFreeIndex.isPresent) {
                val newIndex = firstFreeIndex.get()
                val oldIndex = blockList.indexOf(blockToCheck)
                blockList.removeAt(oldIndex)
                blockList.add(oldIndex, EmptyBlock(blockToCheck.size()))
                val blockToRemove = blockList.removeAt(newIndex)
                blockList.add(newIndex, blockToCheck)
                if (blockToRemove.size() > blockToCheck.size()) {
                    blockList.add(newIndex + 1, EmptyBlock(blockToRemove.size() - blockToCheck.size()))
                }
            }
            blockList
        }

    return calculateChecksum(copy)
}

fun calculateChecksum(diskData: List<Block>): Long {
    var index = 0
    return diskData
        .map { block ->
            val checksum = block.checksum(index)
            index += block.size()
            checksum
        }
        .sum()
}

fun freeSpaceIndex(diskData: List<Block>, block: Block): Optional<Int> {

    return Optional.ofNullable(diskData.subList(0, diskData.indexOf(block))
        .mapIndexed { index, block -> index to block }
        .filter { (_, candidate) -> candidate is EmptyBlock && candidate.size >= block.size() }
        .firstOrNull()?.first)

}

fun reverseBlocks(blocks: List<Block>): Iterator<Block> {
    return blocks
        .reversed()
        .flatMap { block ->
            when (block) {
                is DataBlock -> (0 until block.size()).map { _ -> DataBlock(block.id, 1) }
                else -> emptyList<Block>()
            }
        }.iterator()
}

fun parseDiskData(input: String): List<Block> {
    var id = 0
    return input
        .chunked(2)
        .flatMap { data ->
            listOf(DataBlock(id++, data.first().digitToInt()), EmptyBlock(data.last().digitToInt()))
        }
}


sealed interface Block {

    fun toChars(): List<Char>
    fun split(wantedSize: Int): Pair<DataBlock, DataBlock>
    fun size(): Int
    fun checksum(index: Int): Long
}

data class DataBlock(val id: Int, val size: Int) : Block {
    override fun toChars(): List<Char> {
        return "$id".repeat(size).toCharArray().toList()
    }

    override fun split(wantedSize: Int): Pair<DataBlock, DataBlock> {
        return DataBlock(this.id, wantedSize) to DataBlock(this.id, this.size - wantedSize)
    }

    override fun size(): Int {
        return size
    }

    override fun checksum(index: Int): Long {
        return id * ((size * (index + (index + size - 1))) / 2).toLong()
    }

}

data class EmptyBlock(val size: Int) : Block {
    override fun toChars(): List<Char> {
        return emptyList()
    }

    override fun split(wantedSize: Int): Pair<DataBlock, DataBlock> {
        TODO("Not yet implemented")
    }

    override fun size(): Int {
        return size
    }

    override fun checksum(index: Int): Long {
        return 0
    }
}

fun <T> Iterator<T>.take(num: Int): List<T> {
    return (0 until num)
        .map { next() }
}

