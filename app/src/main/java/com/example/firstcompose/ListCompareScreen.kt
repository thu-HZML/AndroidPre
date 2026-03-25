package com.example.firstcompose

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

private class RenderMetrics {
    var composeActivated by mutableStateOf(0)
    var composeDisposed by mutableStateOf(0)
    var xmlCreated by mutableStateOf(0)
    var xmlBound by mutableStateOf(0)
    var xmlRecycled by mutableStateOf(0)

    fun onComposeActivate() {
        composeActivated += 1
    }

    fun onComposeDispose() {
        composeDisposed += 1
    }

    fun onXmlCreate() {
        xmlCreated += 1
    }

    fun onXmlBind() {
        xmlBound += 1
    }

    fun onXmlRecycle() {
        xmlRecycled += 1
    }
}

internal fun generateCampusResources(totalCount: Int = 2400): List<CampusResource> {
    val titles = listOf(
        "图书馆安静自习位",
        "项目路演训练营",
        "竞赛答疑工作坊",
        "校医院夜间值班提醒",
        "勤工助学信息",
        "社团招新咨询台",
        "考研共享自习室",
        "技术沙龙",
        "简历门诊",
        "教务大厅值班窗口"
    )
    val categories = listOf("自习", "讲座", "社团", "办事", "兼职")
    val places = listOf("图书馆 B1", "理科楼 203", "学生活动中心", "信息楼大厅", "行政服务中心", "南区共享空间")
    val timeLabels = listOf("今天 14:00", "今天 19:30", "明天 09:00", "周五 16:00", "本周持续开放")
    val summaries = listOf(
        "适合赶作业和小组讨论，支持按时段查看余量。",
        "可以快速找到同专业同目标同学，降低信息搜索成本。",
        "面向期中周和答辩周，优先展示近期更急需的信息。",
        "支持收藏和稍后处理，避免重要信息被刷过去。",
        "列表会持续滚动加载，适合作为高数据量浏览页面。"
    )

    return List(totalCount) { index ->
        val category = categories[index % categories.size]
        CampusResource(
            id = index,
            title = "${titles[index % titles.size]} ${index / titles.size + 1}",
            category = category,
            place = places[index % places.size],
            timeLabel = timeLabels[index % timeLabels.size],
            heat = 68 + (index % 28),
            summary = summaries[index % summaries.size]
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
    val metrics = remember { RenderMetrics() }
    var selectedCategory by remember { mutableStateOf(resourceCategories.first()) }
    var query by remember { mutableStateOf("") }

    val visibleResources = remember(allResources, selectedCategory, query) {
        filterCampusResources(
            allItems = allResources,
            selectedCategory = selectedCategory,
            query = query
        )
    }
    val useDynamicComposeList = selectedCategory == "全部"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7FB))
    ) {
        ResourceHeroCard(
            totalCount = allResources.size,
            currentCount = visibleResources.size,
            useDynamicComposeList = useDynamicComposeList
        )
        SearchBar(
            query = query,
            onQueryChange = { query = it }
        )
        CategorySelector(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )
        if (useDynamicComposeList) {
            ComposeResourceList(
                items = visibleResources,
                metrics = metrics,
                modifier = Modifier.weight(1f)
            )
        } else {
            LegacyResourceList(
                items = visibleResources,
                metrics = metrics,
                modifier = Modifier.weight(1f)
            )
        }
        ExperimentalStatusRow(
            metrics = metrics,
            resultCount = visibleResources.size,
            useDynamicComposeList = useDynamicComposeList
        )
    }
}

@Composable
private fun ResourceHeroCard(
    totalCount: Int,
    currentCount: Int,
    useDynamicComposeList: Boolean
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "校园资源广场",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF12344D)
            )
            Text(
                text = "“全部”使用 Compose 动态渲染，分类页切回 RecyclerView + XML，直接把两种列表技术整合进真实场景。",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color(0xFF466276)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryPill(label = "总资源", value = totalCount.toString())
                SummaryPill(label = "当前结果", value = currentCount.toString())
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
        placeholder = { Text("搜索资源名、地点或关键词") },
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
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (selected) MaterialTheme.colors.primary
                        else Color.White
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = category,
                    color = if (selected) Color.White else Color(0xFF607D8B),
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun ComposeResourceList(
    items: List<CampusResource>,
    metrics: RenderMetrics,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = items, key = { it.id }) { item ->
            DisposableEffect(item.id) {
                metrics.onComposeActivate()
                onDispose {
                    metrics.onComposeDispose()
                }
            }
            ComposeResourceCard(item = item)
        }
    }
}

@Composable
private fun ComposeResourceCard(item: CampusResource) {
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
private fun LegacyResourceList(
    items: List<CampusResource>,
    metrics: RenderMetrics,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RectangleShape)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                RecyclerView(context).apply {
                    layoutManager = LinearLayoutManager(context)
                    clipToPadding = true
                    setPadding(0, 0, 0, 0)
                    adapter = LegacyResourceAdapter(
                        items = items,
                        onCreate = metrics::onXmlCreate,
                        onBind = metrics::onXmlBind,
                        onRecycle = metrics::onXmlRecycle
                    )
                }
            },
            update = { recyclerView ->
                val adapter = recyclerView.adapter as? LegacyResourceAdapter
                adapter?.submitList(items)
            }
        )
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
        Box(
            modifier = Modifier
                .width(6.dp)
                .height(6.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF9800))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "热度 $heat",
            fontSize = 12.sp,
            color = Color(0xFF8D5A00)
        )
    }
}

@Composable
private fun ExperimentalStatusRow(
    metrics: RenderMetrics,
    resultCount: Int,
    useDynamicComposeList: Boolean
) {
    val message = if (useDynamicComposeList) {
        "实验记录 当前实现 Compose · 当前结果 $resultCount · 激活 ${metrics.composeActivated} · 离场 ${metrics.composeDisposed}"
    } else {
        "实验记录 当前实现 XML · 当前结果 $resultCount · 创建 ${metrics.xmlCreated} · 绑定 ${metrics.xmlBound} · 回收 ${metrics.xmlRecycled}"
    }
    Text(
        text = message,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        color = Color(0xFF97A6B2),
        fontSize = 11.sp
    )
}

private class LegacyResourceAdapter(
    items: List<CampusResource>,
    private val onCreate: () -> Unit,
    private val onBind: () -> Unit,
    private val onRecycle: () -> Unit
) : RecyclerView.Adapter<LegacyResourceAdapter.LegacyResourceViewHolder>() {

    private var items: List<CampusResource> = items

    fun submitList(newItems: List<CampusResource>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LegacyResourceViewHolder {
        onCreate()
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_legacy_row, parent, false)
        return LegacyResourceViewHolder(view)
    }

    override fun onBindViewHolder(holder: LegacyResourceViewHolder, position: Int) {
        onBind()
        holder.bind(items[position])
    }

    override fun onViewRecycled(holder: LegacyResourceViewHolder) {
        onRecycle()
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = items.size

    class LegacyResourceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleText: TextView = view.findViewById(R.id.tvTitle)
        private val metaText: TextView = view.findViewById(R.id.tvMeta)
        private val summaryText: TextView = view.findViewById(R.id.tvSummary)

        fun bind(item: CampusResource) {
            titleText.text = item.title
            metaText.text = "${item.category} · ${item.place} · ${item.timeLabel} · 热度 ${item.heat}"
            summaryText.text = item.summary
        }
    }
}
