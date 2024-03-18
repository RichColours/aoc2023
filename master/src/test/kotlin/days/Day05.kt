package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.containsInRanges
import util.filePathToLines
import kotlin.streams.asStream

class Day05 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/05/samp1.txt, 1, 35",
            "src/test/resources/days/05/prod1.txt, 1, 313045984",
        ]
    )
    fun day05Question1(inputFile: String, mode: Int, expected: Long) {

        val lines = filePathToLines(inputFile)

        val seedsLine = lines[0].substring(7)

        val seeds = seedsLine.split(' ').map { it.toLong() }.asSequence()

        //println(seeds)

        val numberedLines = lines.mapIndexed { index, s -> index to s }

        val mapNameLines = numberedLines.filter { it.second.contains("map:") }

        val mapEnds = mapNameLines.map {
            val index = lines.subList(it.first, lines.size).indexOfFirst { it.isEmpty() }
            if (index == -1) lines.lastIndex else it.first + (index - 1)
        }

        fun createMapFunction(longs: List<List<Long>>): (Long) -> Long {

            //val sortedLongs = longs.sortedBy { it[1] }

            fun rowValid(longs: List<Long>, input: Long): Boolean {
                val sourceNum = longs[1]
                val range = longs[2]
                return sourceNum <= input && input < (sourceNum + range)
            }

            val store = object {
                var lastLongs: List<Long>? = null
            }

            return { input ->

                val ans = if ((store.lastLongs != null) && rowValid(store.lastLongs!!, input)) {
                    val sourceNum = store.lastLongs!![1]
                    val destNum = store.lastLongs!![0]
                    val offset = input - sourceNum
                    val output = destNum + offset
                    output
                } else {
                    val rowIndex = longs.indexOfFirst {
                        val sourceNum = it[1]
                        val range = it[2]
                        sourceNum <= input && input < (sourceNum + range)
                    }

                    if (rowIndex == -1) {
                        input
                    } else {
                        val it = longs[rowIndex]
                        val sourceNum = it[1]
                        val destNum = it[0]
                        val offset = input - sourceNum
                        val output = destNum + offset

                        //store.lastLongs = it
                        output
                    }
                }

                ans
            }
        }

        val mappings = mapNameLines.zip(mapEnds)
            .map {
                val mapNameIndex = it.first.first
                val mapNameLine = it.first.second
                val mapDataStartIndex = mapNameIndex + 1
                val mapEndIndex = it.second

                val mapName = "(.*) map:".toRegex().matchEntire(mapNameLine)!!.groupValues[1]

                val dataLines = lines.subList(mapDataStartIndex, mapEndIndex + 1)

                val dataLongs = dataLines.map {
                    it.split(' ').map { it.toLong() }
                }

                mapName to createMapFunction(dataLongs)
            }
            .toMap()

        //println(mappings)

        //println(seeds.asSequence().last())

        val lowestLocation =
            seeds.asStream()
                .map { mappings["seed-to-soil"]!!(it) }
                .map { mappings["soil-to-fertilizer"]!!(it) }
                .map { mappings["fertilizer-to-water"]!!(it) }
                .map { mappings["water-to-light"]!!(it) }
                .map { mappings["light-to-temperature"]!!(it) }
                .map { mappings["temperature-to-humidity"]!!(it) }
                .map { mappings["humidity-to-location"]!!(it) }
                .min(Comparator.naturalOrder())
                .get()

        assertThat(lowestLocation).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            //"src/test/resources/days/05/samp1.txt, 2, 46",
            "src/test/resources/days/05/prod1.txt, 2, 20283860", // 12 minutes
        ]
    )
    fun day05Question2(inputFile: String, mode: Int, expected: Long) {

        val lines = filePathToLines(inputFile)

        val seedsLine = lines[0].substring(7)

        val nums = seedsLine.split(' ').map { it.toLong() }

        val starts = nums.flatMapIndexed { index: Int, l: Long -> if (index.and(1) == 0) listOf(l) else emptyList() }
        val ranges = nums.flatMapIndexed { index: Int, l: Long -> if (index.and(1) == 1) listOf(l) else emptyList() }

        val seedRanges = starts.zip(ranges)
            .map {
                LongRange(it.first, it.first + it.second - 1)
            }
            .sortedBy { it.first }
            .also {
                it.forEach { println(it) }
            }

        val numberedLines = lines.mapIndexed { index, s -> index to s }

        val mapNameLines = numberedLines.filter { it.second.contains("map:") }

        val mapEnds = mapNameLines.map {
            val index = lines.subList(it.first, lines.size).indexOfFirst { it.isEmpty() }
            if (index == -1) lines.lastIndex else it.first + (index - 1)
        }

        fun createMapFunction(longs: List<List<Long>>): (Long) -> Long {

            return { input ->

                val rowIndex = longs.indexOfFirst {
                    val sourceNum = it[0]
                    val range = it[2]
                    sourceNum <= input && input < (sourceNum + range)
                }

                if (rowIndex == -1) {
                    input
                } else {
                    val it = longs[rowIndex]
                    val sourceNum = it[0]
                    val destNum = it[1]
                    val offset = input - sourceNum
                    val output = destNum + offset

                    output
                }
            }
        }

        val dataSets = mapNameLines.zip(mapEnds)
            .map {
                val mapNameIndex = it.first.first
                val mapNameLine = it.first.second
                val mapDataStartIndex = mapNameIndex + 1
                val mapEndIndex = it.second

                val mapName = "(.*) map:".toRegex().matchEntire(mapNameLine)!!.groupValues[1]

                val dataLines = lines.subList(mapDataStartIndex, mapEndIndex + 1)

                val dataLongs = dataLines.map {
                    it.split(' ').map { it.toLong() }
                }

                mapName to dataLongs
            }
            .toMap()

        val mappings = dataSets.map {
            it.key to createMapFunction(it.value)
        }
            .toMap()

        //println(mappings)

        val locationsSeq = //locationRanges.asCombinedSequence()
            LongRange(0, Long.MAX_VALUE)

        val lowestLocation =
            locationsSeq.first { location ->
                val seed = mappings["seed-to-soil"]!!(
                    mappings["soil-to-fertilizer"]!!(
                        mappings["fertilizer-to-water"]!!(
                            mappings["water-to-light"]!!(
                                mappings["light-to-temperature"]!!(
                                    mappings["temperature-to-humidity"]!!(
                                        mappings["humidity-to-location"]!!(
                                            location
                                        )
                                    )
                                )
                            )
                        )
                    )
                )

                seedRanges.containsInRanges(seed)
            }

        assertThat(lowestLocation).isEqualTo(expected)
    }
}