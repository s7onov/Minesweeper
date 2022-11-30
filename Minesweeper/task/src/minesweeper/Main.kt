package minesweeper

import kotlin.random.Random

const val HIDDEN = '.'
const val MINE = 'X'
const val MARKER = '*'
const val FREE = '/'

class MineGame {
    class Cell {
        var data = FREE
        var explored = false
        var marked = false

        override fun toString(): String {
            return if (marked) MARKER.toString()
            else if (!explored) HIDDEN.toString()
            else data.toString()
        }
    }
    private val width = 9
    private val height = 9
    private val field = Array(height) { Array(width) { Cell() } }
    private var minesCount = 0
    private var turn = 1

    fun start() {
        print("How many mines do you want on the field? ")
        minesCount = readln().toInt()
        printField()
        while (true) {
            print("Set/unset mines marks or claim a cell as free: ")
            val input = readln().split(" ")
            val y = input[0].toInt() - 1
            val x = input[1].toInt() - 1
            val command = input[2]
            if (turn == 1) generateMineField(minesCount, x, y)
            when (command) {
                "free" -> if (!explore(x, y)) break
                "mine" -> if (!mark(x, y)) continue
                else -> continue
            }
            turn++
            printField()
            if (isPlayerWin()) { println("Congratulations! You found all the mines!"); break }
        }
    }

    private fun generateMineField(minesCount: Int, x: Int, y: Int) {
        var count = 0
        do {
            val h = Random.nextInt(height)
            val w = Random.nextInt(width)
            if (h == x && w == y) continue
            if (field[h][w].data != MINE) {
                field[h][w].data = MINE
                count++
            }
        } while (count < minesCount)
        placeNumbers()
    }

    private fun mark(x: Int, y: Int): Boolean {
        val cell = field[x][y]
        if (cell.explored) { println("There is a number here!"); return false }
        cell.marked = !cell.marked
        return true
    }

    private fun explore(x: Int, y: Int): Boolean {
        if (x !in 0 until height || y !in 0 until width) return true
        val cell = field[x][y]
        if (cell.explored) return true
        cell.explored = true
        cell.marked = false
        if (cell.data == MINE) { showAllMines(); println("You stepped on a mine and failed!"); return false }
        else if (cell.data in '1'..'8') return true
        else if (cell.data == FREE) {
            for (i in -1 .. 1)
                for (j in -1..1) {
                    if (i == 0 && j == 0) continue
                    explore(x + i, y + j)
                }
        }
        return true
    }

    private fun showAllMines() {
        for (i in 0 until height)
            for (j in 0 until width)
                if (field[i][j].data == MINE) field[i][j].explored = true
        printField()
    }

    private fun isPlayerWin(): Boolean {
        var exploredCount = 0
        var unmarkedMines = 0
        for (i in 0 until height)
            for (j in 0 until width) {
                val cell = field[i][j]
                if ((cell.data == MINE) != (cell.marked)) unmarkedMines++
                if (cell.explored) exploredCount++
            }
        return unmarkedMines == 0 || (exploredCount == width * height - minesCount)
    }

    private fun placeNumbers() {
        for (i in 0 until height)
            for (j in 0 until width) {
                if (field[i][j].data != MINE) continue
                for (x in -1..1)
                    for (y in -1..1) {
                        if (i + x !in 0 until height || j + y !in 0 until width ) continue
                        if (x == 0 && y == 0) continue
                        if (field[i + x][j + y].data == MINE) continue
                        when (field[i + x][j + y].data) {
                            FREE -> field[i + x][j + y].data = '1'
                            in '1'..'7' -> field[i + x][j + y].data++
                        }
                    }
            }
    }

    private fun printField() {
        println(Array(width){ it + 1 }.joinToString("", " │", "│"))
        println(Array(width){ '-' }.joinToString("", "-│", "│"))
        for (i in 0 until height)
            println(field[i].joinToString("", "${i + 1}│", "│"))
        println(Array(width){ '-' }.joinToString("", "-│", "│"))
    }
}

fun main() {
    val game = MineGame()
    game.start()
}
