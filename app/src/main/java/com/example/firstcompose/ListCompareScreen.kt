package com.example.firstcompose

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private data class PerfItem(
    val id: Int,
    val title: String
)

private class RenderMetrics {
    var created by mutableStateOf(0)
    var bound by mutableStateOf(0)
    var disposed by mutableStateOf(0)

    fun onCreate() {
        created += 1
    }

    fun onBind() {
        bound += 1
    }

    fun onDispose() {
        disposed += 1
    }
}

@Composable
fun ListCompareScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("列表性能对比") },
                navigationIcon = { BackButton(onBack) },
                backgroundColor = MaterialTheme.colors.primary
            )
        }
    ) { paddingValues ->
        ListCompareContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
fun ListCompareContent(modifier: Modifier = Modifier) {
    val listData = remember {
        List(10_000) { index -> PerfItem(id = index, title = "列表项 #$index") }
    }
    var selectedTab by remember { mutableStateOf(0) }
    val legacyMetrics = remember { RenderMetrics() }
    val composeMetrics = remember { RenderMetrics() }

    Column(modifier = modifier) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("RecyclerView（XML）") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("LazyColumn（Compose）") }
            )
        }

        if (selectedTab == 0) {
            MetricsPanel(
                title = "传统 RecyclerView",
                metrics = legacyMetrics,
                disposeLabel = "回收"
            )
            LegacyRecyclerList(
                items = listData,
                metrics = legacyMetrics,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        } else {
            MetricsPanel(
                title = "Compose LazyColumn",
                metrics = composeMetrics,
                disposeLabel = "释放"
            )
            ComposeLazyList(
                items = listData,
                metrics = composeMetrics,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun MetricsPanel(
    title: String,
    metrics: RenderMetrics,
    disposeLabel: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.subtitle1)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("创建: ${metrics.created}")
                Text("绑定: ${metrics.bound}")
                Text("$disposeLabel: ${metrics.disposed}")
            }
        }
    }
}

@Composable
private fun LegacyRecyclerList(
    items: List<PerfItem>,
    metrics: RenderMetrics,
    modifier: Modifier = Modifier
) {
    val topPaddingPx = with(LocalDensity.current) { 8.dp.roundToPx() }
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
                clipToPadding = false
                setPadding(0, topPaddingPx, 0, topPaddingPx)
                adapter = LegacyAdapter(
                    items = items,
                    onCreate = metrics::onCreate,
                    onBind = metrics::onBind,
                    onRecycle = metrics::onDispose
                )
            }
        }
    )
}

@Composable
private fun ComposeLazyList(
    items: List<PerfItem>,
    metrics: RenderMetrics,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = items, key = { it.id }) { item ->
            DisposableEffect(item.id) {
                metrics.onCreate()
                metrics.onBind()
                onDispose {
                    metrics.onDispose()
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                elevation = 2.dp
            ) {
                Text(
                    text = item.title,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

private class LegacyAdapter(
    private val items: List<PerfItem>,
    private val onCreate: () -> Unit,
    private val onBind: () -> Unit,
    private val onRecycle: () -> Unit
) : RecyclerView.Adapter<LegacyAdapter.LegacyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LegacyViewHolder {
        onCreate()
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_legacy_row, parent, false)
        return LegacyViewHolder(view)
    }

    override fun onBindViewHolder(holder: LegacyViewHolder, position: Int) {
        onBind()
        holder.bind(items[position])
    }

    override fun onViewRecycled(holder: LegacyViewHolder) {
        onRecycle()
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = items.size

    class LegacyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleText: TextView = view.findViewById(R.id.tvTitle)

        fun bind(item: PerfItem) {
            titleText.text = item.title
        }
    }
}
