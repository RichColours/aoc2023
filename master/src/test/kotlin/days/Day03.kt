package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.filePathToLines

class Day03 {

    data class Coord(
        val x: Int,
        val y: Int
    )

    fun surroundingCoords(coord: Coord, width: Int, height: Int): List<Coord> {

        val returnList = mutableListOf<Coord>()

        val isOnTopEdge = coord.y == 0
        val isOnBottomEdge = coord.y == height - 1
        val isOnRightEdge = coord.x == 0
        val isOnLeftEdge = coord.x == width - 1

        // Top row

        if (!isOnTopEdge && !isOnLeftEdge) returnList.add(Coord(coord.x - 1, coord.y - 1))

        if (!isOnTopEdge) returnList.add(Coord(coord.x, coord.y - 1))

        if (!isOnTopEdge && !isOnRightEdge) returnList.add(Coord(coord.x + 1, coord.y - 1))

        // Mid row

        if (!isOnLeftEdge) returnList.add(Coord(coord.x - 1, coord.y))

        if (!isOnRightEdge) returnList.add(Coord(coord.x + 1, coord.y))

        // Bottom row

        if (!isOnBottomEdge && !isOnLeftEdge) returnList.add(Coord(coord.x - 1, coord.y + 1))

        if (!isOnBottomEdge) returnList.add(Coord(coord.x, coord.y + 1))

        if (!isOnBottomEdge && !isOnRightEdge) returnList.add(Coord(coord.x + 1, coord.y + 1))

        return returnList
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/03/samp1.txt, 4361",
            "src/test/resources/days/03/prod1.txt, 550934"
        ]
    )
    fun day03Question1(inputFile: String, expected: Int) {

        data class Symbol(
            val symbol: Char,
            val x: Int,
            val y: Int
        )

        data class Number(
            val str: String,
            val n: Int,
            val x: Int,
            val y: Int
        )


        val lines = filePathToLines(inputFile)

        val symbols = mutableListOf<Symbol>()
        val numbers = mutableListOf<Number>()

        lines.forEachIndexed { lineIndex, s ->

            val numberMatches = "\\d+".toRegex().findAll(s)

            numberMatches.forEach {
                numbers.add(
                    Number(it.value, it.value.toInt(), it.range.first, lineIndex)
                )
            }

            val symbolMatches = "[^.0123456789]".toRegex().findAll(s)

            symbolMatches.forEach {
                symbols.add(
                    Symbol(it.value[0], it.range.first, lineIndex)
                )
            }
        }

        val boardWidth = lines[0].length
        val boardHeight = lines.size

        //println(numbers)
        //println(symbols)

        println(symbols.map { it.symbol }.toSet())

        // Build coord -> symbol map
        val coordToSymbolMap = symbols.flatMap { symbol ->
            val surrounds = surroundingCoords(Coord(symbol.x, symbol.y), boardWidth, boardHeight)
            surrounds.map { it to symbol }
        }
            .toMap()

        println(coordToSymbolMap)

        val participatingNumbers = numbers.flatMap { number ->

            val numLength = number.str.length
            val allNumberXVals = number.x..<number.x + numLength

            val allNumCoords = allNumberXVals.map { Coord(it, number.y) }

            if (allNumCoords.any { coordToSymbolMap.containsKey(it) }) {
                listOf(number)
            } else {
                emptyList()
            }
        }

        println(participatingNumbers)

        val trueNumbers = participatingNumbers.map { it.n }

        val sum = trueNumbers.sum()
        assertThat(sum).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/03/samp2.txt, 467835",
            "src/test/resources/days/03/prod1.txt, 81997870"
        ]
    )
    fun day03Question2(inputFile: String, expected: Int) {

        data class Symbol(
            val symbol: Char,
            val x: Int,
            val y: Int
        )

        data class Number(
            val str: String,
            val n: Int,
            val x: Int,
            val y: Int
        )

        val lines = filePathToLines(inputFile)

        val boardWidth = lines[0].length
        val boardHeight = lines.size

        val symbols = mutableListOf<Symbol>()
        val numbers = mutableListOf<Number>()

        lines.forEachIndexed { lineIndex, s ->

            val numberMatches = "\\d+".toRegex().findAll(s)

            numberMatches.forEach {
                numbers.add(
                    Number(it.value, it.value.toInt(), it.range.first, lineIndex)
                )
            }

            val symbolMatches = "[^.0123456789]".toRegex().findAll(s)

            symbolMatches.forEach {
                symbols.add(
                    Symbol(it.value[0], it.range.first, lineIndex)
                )
            }
        }

        //println(numbers)
        //println(symbols)

        println(symbols.map { it.symbol }.toSet())

        // Build coord -> Number map
        val coordToNumbers = numbers.flatMap { number ->

            val numLength = number.str.length
            val allNumberXVals = number.x..<number.x + numLength
            val allNumCoords = allNumberXVals.map { Coord(it, number.y) }

            allNumCoords.map { it to number }
        }
            .groupBy(
                { it.first },
                { it.second }
            )

        val starSymbols = symbols.filter { it.symbol == '*' }

        println(starSymbols)

        val starSymbolsWithTwoNumbers = starSymbols.flatMap { symbol ->

            val symbolSurrounds = surroundingCoords(Coord(symbol.x, symbol.y), boardWidth, boardHeight)

            val numbers = symbolSurrounds.flatMap {
                coordToNumbers[it] ?: emptyList()
            }
                .toSet()

            if (numbers.size == 2)
                listOf(symbol to Pair(numbers.toList()[0], numbers.toList()[1]))
            else
                emptyList()
        }
            .toMap()

        println(starSymbolsWithTwoNumbers)

        val gearRatios = starSymbolsWithTwoNumbers.map {
            it.value.first.n * it.value.second.n
        }

        val sum = gearRatios.sum()
        assertThat(sum).isEqualTo(expected)
    }

}