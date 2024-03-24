package days

import assertk.assertThat
import assertk.assertions.hasSameSizeAs
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.Grid
import util.filePathToLines
import util.isEven
import util.toGrid

class Day10 {

    val validPipeChars = setOf('L', 'F', '7', 'J', '-', '|')

    val allowedConnectionsMap = mapOf(
        /*
        'L' to listOf(
            (Grid.Position.T to 'F'),
            (Grid.Position.T to '7'),
            (Grid.Position.T to '|'),
            (Grid.Position.L to '7'),
            (Grid.Position.L to 'J'),
            (Grid.Position.L to '-')
        ),

        'F' to listOf(
            (Grid.Position.L to '7'),
            (Grid.Position.L to 'J'),
            (Grid.Position.L to '-'),
            (Grid.Position.B to 'L'),
            (Grid.Position.B to 'J'),
            (Grid.Position.B to '|')
        ),

        '7' to listOf(
            (Grid.Position.B to 'L'),
            (Grid.Position.B to 'J'),
            (Grid.Position.B to '|'),
            (Grid.Position.R to 'L'),
            (Grid.Position.R to 'F'),
            (Grid.Position.R to '-')
        ),

        'J' to listOf(
            (Grid.Position.T to 'F'),
            (Grid.Position.T to '7'),
            (Grid.Position.T to '|'),
            (Grid.Position.R to 'L'),
            (Grid.Position.R to 'F'),
            (Grid.Position.R to '-')
        ),

        '-' to listOf(
            (Grid.Position.L to '7'),
            (Grid.Position.L to 'J'),
            (Grid.Position.L to '-'),
            (Grid.Position.R to 'L'),
            (Grid.Position.R to 'F'),
            (Grid.Position.R to '-')
        ),

        '|' to listOf(
            (Grid.Position.T to 'F'),
            (Grid.Position.T to '7'),
            (Grid.Position.T to '|'),
            (Grid.Position.B to 'L'),
            (Grid.Position.B to 'J'),
            (Grid.Position.B to '|')
        ),

         */

        'S' to listOf(
            (Grid.Position.T to 'F'),
            (Grid.Position.T to '7'),
            (Grid.Position.T to '|'),
            (Grid.Position.B to 'L'),
            (Grid.Position.B to 'J'),
            (Grid.Position.B to '|'),
            (Grid.Position.R to '7'),
            (Grid.Position.R to 'J'),
            (Grid.Position.R to '-'),
            (Grid.Position.L to 'L'),
            (Grid.Position.L to 'F'),
            (Grid.Position.L to '-')
        ),
    )

    val allowedConnectionsEasy = mapOf(
        'L' to listOf(Grid.Position.T, Grid.Position.R),
        'F' to listOf(Grid.Position.R, Grid.Position.B),
        '7' to listOf(Grid.Position.B, Grid.Position.L),
        'J' to listOf(Grid.Position.T, Grid.Position.L),
        '-' to listOf(Grid.Position.R, Grid.Position.L),
        '|' to listOf(Grid.Position.T, Grid.Position.B),
    )

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/10/samp1.txt, 4",
            "src/test/resources/days/10/samp1b.txt, 4",
            "src/test/resources/days/10/samp2.txt, 8",
            "src/test/resources/days/10/samp2b.txt, 8",
            "src/test/resources/days/10/prod1.txt, 6860"
        ]
    )
    fun day10Question1(inputFile: String, expected: Int) {

        val inputLines = filePathToLines(inputFile)

        val grid = inputLines.toGrid()

        val s = grid.find { it.value() == 'S' }!!

        val sConnections = allowedConnectionsMap['S']!!

        val pairFromS = s.neighboursExc()
            .filter { it.position to it.value() in sConnections }

        if (pairFromS.size != 2)
            throw Error("Expected 2 s routes, but found ${pairFromS.size}")

        tailrec fun findWholeLoop(
            chain: List<Grid.GridElem<Char>>,
            endPoint: Grid.GridElem<Char>,
            nowPoint: Grid.GridElem<Char>
        ): List<Grid.GridElem<Char>> {

            if (nowPoint == endPoint) return chain

            val prior = chain.last()

            //println(chain.map { "${it.x}, ${it.y} (${it.value()})"} + " -> ${nowPoint.x}, ${nowPoint.y}")
            //println()

            val twoConnections = allowedConnectionsEasy[nowPoint.value()]!!

            val priorPosition = nowPoint.neighboursExc()
                .find { it.x == prior.x && it.y == prior.y }!!
                .position!!

            val nextPosition = twoConnections.filter { it != priorPosition }.first()

            val next = nowPoint.neighboursExc()
                .filter { it.position == nextPosition }.first()

            return findWholeLoop(chain.plus(nowPoint), endPoint, next)
        }

        val loop = findWholeLoop(listOf(s), s, pairFromS[0])
        if (!loop.size.isEven())
            throw Error("Distance isn't even, cannot halve")

        val dist = loop.size / 2

        assertThat(dist).isEqualTo(expected)
    }

    val lookLocationsMap = mapOf(
        'L' to mapOf(
            Grid.Position.T to emptyList(),
            Grid.Position.R to listOf(Grid.Position.B, Grid.Position.L)
        ),
        'F' to mapOf(
            Grid.Position.R to emptyList(),
            Grid.Position.B to listOf(Grid.Position.T, Grid.Position.L)
        ),
        '7' to mapOf(
            Grid.Position.B to emptyList(),
            Grid.Position.L to listOf(Grid.Position.T, Grid.Position.R)
        ),
        'J' to mapOf(
            Grid.Position.L to emptyList(),
            Grid.Position.T to listOf(Grid.Position.R, Grid.Position.B)
        ),
        '-' to mapOf(
            Grid.Position.L to listOf(Grid.Position.T),
            Grid.Position.R to listOf(Grid.Position.B)
        ),
        '|' to mapOf(
            Grid.Position.T to listOf(Grid.Position.R),
            Grid.Position.B to listOf(Grid.Position.L)
        ),
        'S' to mapOf(
            Grid.Position.B to listOf(Grid.Position.L),
            Grid.Position.T to listOf(Grid.Position.R),
            Grid.Position.L to listOf(Grid.Position.T),
            Grid.Position.R to listOf(Grid.Position.B),
        ),
    )


    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/10/samp3.txt, false, 4",
            "src/test/resources/days/10/samp4.txt, true, 8",
            "src/test/resources/days/10/samp5.txt, true, 10",
            "src/test/resources/days/10/stest1.txt, true, 6",
            "src/test/resources/days/10/fill1.txt, true, 9",
            "src/test/resources/days/10/prod1.txt, true, 343"
        ]
    )
    fun day10Question2(inputFile: String, left: Boolean, expected: Int) {

        val inputLines = filePathToLines(inputFile)

        val grid = inputLines.toGrid()

        val s = grid.find { it.value() == 'S' }!!

        val sConnections = allowedConnectionsMap['S']!!

        val pairFromS = s.neighboursExc()
            .filter { it.position to it.value() in sConnections }

        if (pairFromS.size != 2)
            throw Error("Expected 2 s routes, but found ${pairFromS.size}")

        tailrec fun findWholeLoop(
            chain: List<Grid.GridElem<Char>>,
            endPoint: Grid.GridElem<Char>,
            nowPoint: Grid.GridElem<Char>
        ): List<Grid.GridElem<Char>> {

            if (nowPoint == endPoint) return chain

            val prior = chain.last()

            //println(chain.map { "${it.x}, ${it.y} (${it.value()})"} + " -> ${nowPoint.x}, ${nowPoint.y}")
            //println()

            val twoConnections = allowedConnectionsEasy[nowPoint.value()]!!

            val priorPosition = nowPoint.neighboursExc()
                .find { it.x == prior.x && it.y == prior.y }!!
                .position!!

            val nextPosition = twoConnections.filter { it != priorPosition }.first()

            val next = nowPoint.neighboursExc()
                .filter { it.position == nextPosition }.first()

            return findWholeLoop(chain.plus(nowPoint), endPoint, next)
        }

        val loop = findWholeLoop(listOf(s), s, pairFromS[0])

        val loopSet = loop.toSet()

        assertThat(loopSet).hasSameSizeAs(loop)

        fun <T> gridFromSparse(
            width: Int,
            height: Int,
            on: T,
            off: T,
            sparse: Collection<Grid.GridElem<T>>
        ): Grid<Char> {
            return (0..<height).map { itY ->
                (0..<width).map { itX ->
                    if (sparse.contains(Grid.GridElem(itX, itY))) {
                        on
                    } else {
                        off
                    }
                }.joinToString("")
            }.toGrid()
        }

        val routeGrid = gridFromSparse(grid.width, grid.height, '#', '-', loopSet)

        routeGrid.printGrid()

        println()
        println()

        /**
         * Enclosed is to my left.
         */
        fun findEnclosedLeftSimple(loop: List<Grid.GridElem<Char>>): List<Grid.GridElem<Char>> {

            return loop.flatMapIndexed { i, vElem ->

                val vVal = vElem.value()

                val uElem = if (i > 0) loop[i - 1] else loop[loop.lastIndex]

                val positionOfPrevious =
                    if (uElem.x < vElem.x)
                        Grid.Position.L
                    else if (uElem.x > vElem.x)
                        Grid.Position.R
                    else if (uElem.y < vElem.y)
                        Grid.Position.T
                    else if (uElem.y > vElem.y)
                        Grid.Position.B
                    else
                        throw Error("Cannot identify position of uElem relative to vElem")

                val charToLookForLookingFor = lookLocationsMap[vVal]!!
                val placesToLook = charToLookForLookingFor[positionOfPrevious]!!

                val lookingAts = vElem.neighboursExc().filter { it.position in placesToLook }

                lookingAts.flatMap {
                    if (it !in loop)
                        listOf(it)
                    else
                        emptyList()
                }

            }
        }

        tailrec fun fillFill(
            data: Set<Grid.GridElem<Char>>,
            loop: List<Grid.GridElem<Char>>
        ): Set<Grid.GridElem<Char>> {

            val dataGrid = gridFromSparse(grid.width, grid.height, '*', '-', data)

            val newFills = dataGrid
                .filter { it.value() == '*' }
                .flatMap {
                    val neighbs = it.neighboursExc().filter {
                        it.position in listOf(
                            Grid.Position.T,
                            Grid.Position.L,
                            Grid.Position.B,
                            Grid.Position.R
                        )
                    }
                    neighbs.flatMap {
                        if (it !in loop && it !in data)
                            listOf(it)
                        else
                            emptyList()
                    }
                }

            if (newFills.isEmpty())
                return data
            else
                return fillFill(data + newFills, loop)
        }

        val directedLoop = if (left)
            loop
        else
            listOf(loop[0]) + (loop.subList(1, loop.size).asReversed())

        val enclosed = findEnclosedLeftSimple(directedLoop).toSet()

        val filledEnclosed = fillFill(enclosed, directedLoop)

        gridFromSparse(grid.width, grid.height, '#', '-', filledEnclosed)
            .printGrid()

        println()
        println()

        assertThat(filledEnclosed).hasSize(expected)
    }

}
