package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.filePathToLines
import util.leastCommonMultiple
import java.math.BigInteger

class Day08 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/08/samp1.txt, 2",
            "src/test/resources/days/08/samp2.txt, 6",
            "src/test/resources/days/08/prod1.txt, 12599"
        ]
    )
    fun day08Question1(inputFile: String, expected: Int) {

        val lines = filePathToLines(inputFile)

        val dirs = lines[0].toList()
        val nodes = lines.drop(2)

        val nodeDefRegex = "(\\w+) = \\((\\w+), (\\w+)\\)".toRegex()

        val entries = nodes.fold(emptyMap()) { acc: Map<String, Pair<String, String>>, v: String ->

            //   AAA = (BBB, CCC)
            val match = nodeDefRegex.matchEntire(v)!!

            val nodeName = match.groups[1]!!.value
            val left = match.groups[2]!!.value
            val right = match.groups[3]!!.value

            acc + (nodeName to (left to right))
        }

        tailrec fun navigate(startAt: String, runsSoFar: Int): Pair<String, Int> {

            val endNode = dirs.fold(startAt) { acc, v ->

                val nextNode = if (v == 'L') entries[acc]!!.first else entries[acc]!!.second
                nextNode
            }

            if (endNode == "ZZZ")
                return "ZZZ" to (runsSoFar + 1)
            else
                return navigate(endNode, runsSoFar + 1)
        }

        val done = navigate("AAA", 0)

        assertThat(done.second * dirs.size).isEqualTo(expected)
    }


    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/08/samp3.txt, 6",
            "src/test/resources/days/08/prod1.txt, 8245452805243"
        ]
    )
    fun day08Question2(inputFile: String, expected: String) {

        val lines = filePathToLines(inputFile)

        val dirs = lines[0].toList()
        val nodes = lines.drop(2)

        val nodeDefRegex = "(\\w+) = \\((\\w+), (\\w+)\\)".toRegex()

        val entries = nodes.fold(emptyMap()) { acc: Map<String, Pair<String, String>>, v: String ->

            //   AAA = (BBB, CCC)
            val match = nodeDefRegex.matchEntire(v)!!

            val nodeName = match.groups[1]!!.value
            val left = match.groups[2]!!.value
            val right = match.groups[3]!!.value

            acc + (nodeName to (left to right))
        }

        fun String.isStartNode(): Boolean = this.endsWith('A')
        fun String.isEndNode(): Boolean = this.endsWith('Z')

        val startNodes = entries.keys.filter { it.isStartNode() }//.subList(0, 3)

        println("Number of dirs = ${dirs.size}")

        tailrec fun navigate(startAt: List<String>, runsSoFar: Long): Pair<List<String>, Long> {

            val dirIndex = (runsSoFar % dirs.size.toLong()).toInt()
            //println("dirIndex = $dirIndex")
            val dir = dirs[dirIndex]

            val endNodes = if (dir == 'L') {
                startAt.map {
                    entries[it]!!.first
                }
            } else {
                startAt.map {
                    entries[it]!!.second
                }
            }

            val areAllNodesAreEndNodes = endNodes.all { it.isEndNode() }

            //if (runsSoFar % 1000000L == 0L) {
            //println("$runsSoFar: $endNodes}")
            //}

            return if (areAllNodesAreEndNodes)
                endNodes to (runsSoFar + 1)
            else
                navigate(endNodes, runsSoFar + 1)
        }

        tailrec fun buildPath(
            startAt: String,
            runsSoFar: Long,
            pathSoFar: List<Pair<Char, String>>
        ): List<Pair<Char, String>> {

            val dirIndex = (runsSoFar % dirs.size.toLong()).toInt()

            val dir = dirs[dirIndex]

            val endNode = if (dir == 'L') {
                entries[startAt]!!.first
            } else {
                entries[startAt]!!.second
            }

            return if (endNode.isEndNode())
                pathSoFar + listOf(dir to endNode)
            else
                buildPath(endNode, runsSoFar + 1, pathSoFar + listOf(dir to endNode))
        }

        tailrec fun followPathFor(startAt: String, pathSoFar: List<String>, stepsRemaining: Int): List<String> {

            val dirIndex = (pathSoFar.size % dirs.size.toLong()).toInt()
            val dir = dirs[dirIndex]

            val endNode = if (dir == 'L') {
                entries[startAt]!!.first
            } else {
                entries[startAt]!!.second
            }

            return if (stepsRemaining == 1)
                pathSoFar + listOf(endNode)
            else
                followPathFor(endNode, pathSoFar + listOf(endNode), stepsRemaining - 1)
        }

        val paths = startNodes.subList(0, startNodes.size).map {
            buildPath(it, 0, emptyList()).size
        }

        /*
        val followLength = 50000
        val longPaths = startNodes.map {
            followPathFor(it, emptyList(), followLength)
        }
            .map {
                // trim down the end

                it.dropLastWhile { !it.isEndNode() }
            }

        val divs = longPaths.zip(paths)
            .map {
                it.first.size.toDouble() / it.second
            }

         */

        // all prod paths end before 50000 steps
        // let's triple that, trim off excess, and see if early looping was happening

        val leastSteps = paths.leastCommonMultiple()

        assertThat(leastSteps).isEqualTo(BigInteger(expected))
        return

        if (true) {

            val doneRecursive = navigate(startNodes, 0)

            val loops = paths.map {
                doneRecursive.second / it
            }
                .map { it.toInt() }

            val leastSteps = loops.leastCommonMultiple()

            assertThat(leastSteps).isEqualTo(BigInteger(expected))
        }

        if (false) {

            var whileNodes = startNodes
            var steps = 0L

            do {

                val dirIndex = (steps % dirs.size.toLong()).toInt()

                val dir = dirs[dirIndex]

                whileNodes = if (dir == 'L') {
                    whileNodes.map {
                        entries[it]!!.first
                    }
                } else {
                    whileNodes.map {
                        entries[it]!!.second
                    }
                }

                steps++
                if (steps % 1000000L == 0L) {
                    println(steps)
                }

            } while (!whileNodes.all { it.isEndNode() })

            val doneWhile = whileNodes to steps

            assertThat(doneWhile.second).isEqualTo(expected.toLong())
        }
    }
}
