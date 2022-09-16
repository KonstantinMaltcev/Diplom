package ru.netology.nerecipe.adapter

interface FilterInteractionListener {

    fun filterOn(category: String)
    fun filterOff(category: String)
    fun getStatus(category: String): Boolean
}