package com.example.firstcompose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val resourceCategories = listOf("全部", "自习", "讲座", "社团", "办事", "兼职")

internal data class CampusResource(
    val id: Int,
    val title: String,
    val category: String,
    val place: String,
    val timeLabel: String,
    val heat: Int,
    val summary: String
)

internal fun generateCampusResources(totalCount: Int = 36): List<CampusResource> {
    val templates = listOf(
        CampusResource(
            id = 0,
            title = "图书馆四层安静自习位",
            category = "自习",
            place = "逸夫馆 4F",
            timeLabel = "今日 13:30 前可预约",
            heat = 96,
            summary = "靠窗区域余位充足，适合复习和个人写作业，支持按时段预约。"
        ),
        CampusResource(
            id = 1,
            title = "算法竞赛晚间答疑",
            category = "讲座",
            place = "信息楼 B203",
            timeLabel = "今晚 19:00",
            heat = 91,
            summary = "面向准备蓝桥杯和程序设计竞赛的同学，提供题单讲解与现场答疑。"
        ),
        CampusResource(
            id = 2,
            title = "学生会宣传部招新咨询",
            category = "社团",
            place = "学生活动中心大厅",
            timeLabel = "本周持续开放",
            heat = 84,
            summary = "可现场了解部门分工、面试流程和本学期活动安排。"
        ),
        CampusResource(
            id = 3,
            title = "教务大厅成绩证明办理",
            category = "办事",
            place = "行政服务中心 1F",
            timeLabel = "工作日 08:30-17:00",
            heat = 76,
            summary = "提供成绩单打印、在读证明和课程成绩盖章等常用服务。"
        ),
        CampusResource(
            id = 4,
            title = "校内信息助理兼职",
            category = "兼职",
            place = "网络中心",
            timeLabel = "报名截止 周五 18:00",
            heat = 88,
            summary = "面向熟悉 Office 与基础运维的同学，岗位时间稳定，可按周排班。"
        ),
        CampusResource(
            id = 5,
            title = "考研共享讨论室",
            category = "自习",
            place = "南区共享空间 204",
            timeLabel = "明日 09:00 起开放",
            heat = 82,
            summary = "适合 3 到 5 人小组讨论，配有白板、电源和投屏设备。"
        ),
        CampusResource(
            id = 6,
            title = "职业发展中心简历门诊",
            category = "讲座",
            place = "就业指导中心 301",
            timeLabel = "周四 15:00",
            heat = 86,
            summary = "提供一对一简历修改建议，也可现场咨询实习投递方向。"
        ),
        CampusResource(
            id = 7,
            title = "志愿服务项目报名台",
            category = "社团",
            place = "紫荆操场东侧",
            timeLabel = "本周六 10:00",
            heat = 71,
            summary = "集中发布本月校园服务与社区支教活动，可直接扫码报名。"
        ),
        CampusResource(
            id = 8,
            title = "一卡通补办与充值",
            category = "办事",
            place = "后勤服务大厅",
            timeLabel = "今日 16:30 前",
            heat = 79,
            summary = "支持一卡通挂失补办、余额查询和校园支付问题处理。"
        ),
        CampusResource(
            id = 9,
            title = "实验室资料整理助理",
            category = "兼职",
            place = "综合实验楼 A105",
            timeLabel = "下周一开始排班",
            heat = 74,
            summary = "主要负责资料录入、设备登记和日常值班，适合课余时间固定的同学。"
        )
    )

    return List(totalCount) { index ->
        val template = templates[index % templates.size]
        template.copy(
            id = index,
            title = if (index < templates.size) template.title else "${template.title} ${index / templates.size + 1}",
            heat = (template.heat - 4 + index % 9).coerceIn(68, 99)
        )
    }
}

internal fun filterCampusResources(
    allItems: List<CampusResource>,
    selectedCategory: String,
    query: String
): List<CampusResource> {
    val normalizedQuery = query.trim()
    return allItems.filter { item ->
        val matchCategory = selectedCategory == "全部" || item.category == selectedCategory
        val matchQuery = normalizedQuery.isBlank() ||
            item.title.contains(normalizedQuery, ignoreCase = true) ||
            item.place.contains(normalizedQuery, ignoreCase = true) ||
            item.summary.contains(normalizedQuery, ignoreCase = true)
        matchCategory && matchQuery
    }
}

@Composable
fun ListCompareContent(modifier: Modifier = Modifier) {
    val allResources = remember { generateCampusResources() }
    var selectedCategory by remember { mutableStateOf(resourceCategories.first()) }
    var query by remember { mutableStateOf("") }

    val visibleResources = remember(allResources, selectedCategory, query) {
        filterCampusResources(
            allItems = allResources,
            selectedCategory = selectedCategory,
            query = query
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7FB))
    ) {
        ResourceOverviewCard(
            totalCount = allResources.size,
            currentCount = visibleResources.size,
            selectedCategory = selectedCategory
        )
        SearchBar(
            query = query,
            onQueryChange = { query = it }
        )
        CategorySelector(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )
        ResourceList(
            items = visibleResources,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ResourceOverviewCard(
    totalCount: Int,
    currentCount: Int,
    selectedCategory: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFDBEEF9))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "查看自习、讲座、办事和兼职等校园信息，支持按分类筛选与关键词搜索。",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color(0xFF466276)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryPill(label = "资源总数", value = totalCount.toString())
                SummaryPill(label = "当前结果", value = currentCount.toString())
                SummaryPill(label = "当前分类", value = selectedCategory)
            }
        }
    }
}

@Composable
private fun SummaryPill(
    label: String,
    value: String
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.88f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF607D8B)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF12344D)
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        placeholder = { Text("搜索资源名称、地点或关键词") },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = Color.White,
            unfocusedBorderColor = Color(0xFFD7E3EA),
            focusedBorderColor = MaterialTheme.colors.primary
        )
    )
}

@Composable
private fun CategorySelector(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        resourceCategories.forEach { category ->
            val selected = category == selectedCategory
            Surface(
                modifier = Modifier.clickable { onCategorySelected(category) },
                shape = RoundedCornerShape(50),
                color = if (selected) MaterialTheme.colors.primary else Color.White
            ) {
                Text(
                    text = category,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    color = if (selected) Color.White else Color(0xFF607D8B),
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun ResourceList(
    items: List<CampusResource>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = items, key = { it.id }) { item ->
            ResourceCard(item = item)
        }
    }
}

@Composable
private fun ResourceCard(item: CampusResource) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B2A34),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                HeatBadge(heat = item.heat)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetaChip(text = item.category)
                MetaChip(text = item.place)
                MetaChip(text = item.timeLabel)
            }
            Text(
                text = item.summary,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = Color(0xFF617887)
            )
        }
    }
}

@Composable
private fun MetaChip(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color(0xFFF2F6F8)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 12.sp,
            color = Color(0xFF607D8B)
        )
    }
}

@Composable
private fun HeatBadge(heat: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFFFF3E0))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.clip(CircleShape),
            color = Color(0xFFFF9800)
        ) {
            Spacer(modifier = Modifier.width(6.dp))
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "热度 $heat",
            fontSize = 12.sp,
            color = Color(0xFF8D5A00)
        )
    }
}
