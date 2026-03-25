package com.example.firstcompose

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CampusResourceFilterTest {

    @Test
    fun filterCampusResources_filtersByCategory() {
        val resources = generateCampusResources(totalCount = 20)

        val result = filterCampusResources(
            allItems = resources,
            selectedCategory = "讲座",
            query = ""
        )

        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.category == "讲座" })
    }

    @Test
    fun filterCampusResources_matchesQueryAcrossFields() {
        val resources = listOf(
            generateCampusResources(totalCount = 1).first().copy(
                id = 1,
                title = "图书馆安静自习位 1",
                category = "自习",
                place = "图书馆 B1",
                summary = "适合晚上复习"
            ),
            generateCampusResources(totalCount = 1).first().copy(
                id = 2,
                title = "项目路演训练营 1",
                category = "讲座",
                place = "理科楼 203",
                summary = "偏向创业项目展示"
            )
        )

        val result = filterCampusResources(
            allItems = resources,
            selectedCategory = "全部",
            query = "图书馆"
        )

        assertEquals(1, result.size)
        assertEquals(1, result.first().id)
    }
}
