package ru.emkn.kotlin.sms

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class MSL<T> (
    val msList: SnapshotStateList<T>
): MutableList<T> {

    constructor(): this(mutableStateListOf())
    private val actualList: MutableList<T> = msList.map{it}.toMutableList()

    override val size: Int
        get() = msList.size

    override fun contains(element: T): Boolean {
        return actualList.contains(element)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        elements.forEach {
            if (!contains(it))
                return false
        }
        return true
    }

    override fun get(index: Int): T {
        return actualList[index]
    }

    override fun indexOf(element: T): Int {
        return actualList.indexOf(element)
    }

    override fun isEmpty(): Boolean {
        return actualList.size == 0
    }

    override fun iterator(): MutableIterator<T> {
        return actualList.listIterator()
    }

    override fun lastIndexOf(element: T): Int {
        return actualList.lastIndexOf(element)
    }

    override fun listIterator(): MutableListIterator<T> {
        return actualList.listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<T> {
        return actualList.listIterator()
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        return msList.subList(fromIndex, toIndex)
    }

    override fun add(element: T): Boolean {
        actualList.add(element)
        msList.add(element)
        return true
    }

    override fun add(index: Int, element: T) {
        actualList.add(index, element)
        msList.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        elements.forEachIndexed { i, t ->
            add(index + i, t)
        }
        return elements.isNotEmpty()
    }

    override fun addAll(elements: Collection<T>): Boolean {
        elements.forEach { t ->
            add(t)
        }
        return elements.isNotEmpty()
    }

    override fun clear() {
        msList.clear()
        actualList.clear()
    }

    override fun remove(element: T): Boolean {
        val bool = actualList.indexOf(element)
        if (bool == -1)
            return false
        actualList.removeAt(bool)
        msList.removeAt(bool)
        return true
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        var ans = false
        elements.forEach {
            while(remove(it)){
                ans = true
            }
        }
        return ans
    }

    override fun removeAt(index: Int): T {

        msList.removeAt(index)
        return actualList.removeAt(index)
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val toRemove = mutableListOf<Int>()
        actualList.forEachIndexed { i, t ->
            if (t !in elements)
                toRemove.add(i)
        }
        toRemove.reversed().forEach {
            removeAt(it)
        }
        return toRemove.isNotEmpty()
    }

    override fun set(index: Int, element: T): T {
        msList[index] = element
        return actualList.set(index, element)
    }
}