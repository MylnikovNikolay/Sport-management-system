import TestCompetitions.Companion.testGeneration
import ru.emkn.kotlin.sms.*
import java.io.File
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.*

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class ExperimentingTests {
    @Test
    fun test1() {
        assertNotEquals(ControlPoint("34").hashCode(), ControlPoint("34").hashCode())
    }

    @Test
    fun test2() {
        class LMS<T> (val list: List<MutableState<T>>): MutableList<T> {
            override val size: Int
                get() = list.size

            override fun contains(element: T): Boolean {
                return list.contains(mutableStateOf(element))
            }

            override fun containsAll(elements: Collection<T>): Boolean {
                TODO("Not yet implemented")
            }

            override fun get(index: Int): T {
                TODO("Not yet implemented")
            }

            override fun indexOf(element: T): Int {
                TODO("Not yet implemented")
            }

            override fun isEmpty(): Boolean {
                TODO("Not yet implemented")
            }

            override fun iterator(): MutableIterator<T> {
                TODO("Not yet implemented")
            }

            override fun lastIndexOf(element: T): Int {
                TODO("Not yet implemented")
            }

            override fun listIterator(): MutableListIterator<T> {
                TODO("Not yet implemented")
            }

            override fun listIterator(index: Int): MutableListIterator<T> {
                TODO("Not yet implemented")
            }

            override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
                TODO("Not yet implemented")
            }

            override fun add(element: T): Boolean {
                TODO("Not yet implemented")
            }

            override fun add(index: Int, element: T) {
                TODO("Not yet implemented")
            }

            override fun addAll(index: Int, elements: Collection<T>): Boolean {
                TODO("Not yet implemented")
            }

            override fun addAll(elements: Collection<T>): Boolean {
                TODO("Not yet implemented")
            }

            override fun clear() {
                TODO("Not yet implemented")
            }

            override fun remove(element: T): Boolean {
                TODO("Not yet implemented")
            }

            override fun removeAll(elements: Collection<T>): Boolean {
                TODO("Not yet implemented")
            }

            override fun removeAt(index: Int): T {
                TODO("Not yet implemented")
            }

            override fun retainAll(elements: Collection<T>): Boolean {
                TODO("Not yet implemented")
            }

            override fun set(index: Int, element: T): T {
                TODO("Not yet implemented")
            }
        }
    }
}