package com.example.firstcompose

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// ---------- 底部导航架构 ----------
sealed class UniTab(val route: String, val title: String, val icon: ImageVector) {
    object Home : UniTab("home", "今日校园", Icons.Default.Home)
    object Schedule : UniTab("schedule", "课表矩阵", Icons.Default.DateRange)
    object DDL : UniTab("ddl", "DDL追踪", Icons.Default.Notifications)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UniHubTheme {
                UniHubApp()
            }
        }
    }
}

@Composable
fun UniHubApp() {
    var currentTab by remember { mutableStateOf<UniTab>(UniTab.Home) }
    val tabs = listOf(UniTab.Home, UniTab.Schedule, UniTab.DDL)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Fake_THU INFO", fontWeight = FontWeight.Bold)
                },
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.primary,
                elevation = 2.dp
            )
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 16.dp
            ) {
                tabs.forEach { tab ->
                    BottomNavigationItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title, fontSize = 12.sp) },
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        selectedContentColor = MaterialTheme.colors.primary,
                        unselectedContentColor = Color.LightGray
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF4F7FB)) // 清新的淡蓝色背景
        ) {
            when (currentTab) {
                UniTab.Home -> HomeScreen()
                UniTab.Schedule -> CourseMatrixScreen()
                UniTab.DDL -> DDLTrackerScreen()
            }
        }
    }
}

// ---------- 1. 首页 (番茄钟、下节课动画、解压悬浮球) ----------
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 顶部问候语

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 左侧：原 CounterDemo (包装为自习番茄钟)
            Box(modifier = Modifier.weight(1f)) {
                PomodoroCounter()
            }
            // 右侧：学习状态展示
            Card(
                modifier = Modifier.weight(1f).height(120.dp),
                shape = MaterialTheme.shapes.large,
                elevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("本周超越了", fontSize = 12.sp, color = Color.Gray)
                    Text("85% 同学", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colors.primary)
                }
            }
        }

        Text("提醒：下一节课", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 8.dp))
        // 原 AnimationDemo：包装为可展开的课程详细信息卡片
        ExpandableCourseCard()

        Text("专注悬浮球 (互动体验)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 8.dp))
        // 原 ElasticCardDemo：包装为屏幕悬浮小组件
        InteractiveFocusWidget()
    }
}

// ---------- 2. 课表矩阵 (原 二维滚动 演示) ----------
// ---------- 2. 课表矩阵 (原 二维滚动 演示) ----------
@Composable
fun CourseMatrixScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("本学期课表", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF2C3E50))
        Text("特性展示：上下滑动查看全天排课，左右滑动查看全周", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))

        Card(
            modifier = Modifier.fillMaxSize().shadow(8.dp, shape = MaterialTheme.shapes.large),
            elevation = 0.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())   // 纵向滚动（看第1到第12节）
                    .horizontalScroll(rememberScrollState()) // 横向滚动（看周一到周日）
                    .background(Color.White)
            ) {
                Column {
                    val days = listOf("时间", "周一", "周二", "周三", "周四", "周五", "周六", "周日")

                    // 13行：1行表头 + 12节课
                    repeat(13) { row ->
                        Row {
                            // 8列：1列表头 + 7天
                            repeat(8) { col ->
                                val isHeader = row == 0 || col == 0

                                // 解析当前格子的课程信息
                                var courseName = ""
                                var room = ""
                                var isCourse = false

                                if (!isHeader) {
                                    if (col == 1 && row == 3) {
                                        courseName = "移动应用\n软件开发"
                                        room = "旧经管报告厅"
                                        isCourse = true
                                    } else if (col == 3 && row == 1) {
                                        courseName = "移动应用\n软件开发"
                                        room = "旧经管报告厅"
                                        isCourse = true
                                    } else if (col == 3 && row == 2) {
                                        courseName = "数据库原理"
                                        room = "五教5202"
                                        isCourse = true
                                    } else if (col == 4 && row == 2) {
                                        courseName = "嵌入式开发"
                                        room = "三教1103"
                                        isCourse = true
                                    } else if (col == 4 && row == 4) {
                                        courseName = "体育"
                                        room = "北体育馆"
                                        isCourse = true
                                    }
                                }

                                // 为不同课程分配不同颜色，更显真实
                                val courseColor = when (courseName) {
                                    "移动应用\n软件开发" -> MaterialTheme.colors.primary.copy(alpha = 0.9f) // 品牌蓝
                                    "数据库原理" -> Color(0xFF7E57C2).copy(alpha = 0.9f) // 紫色
                                    "嵌入式开发" -> Color(0xFF26A69A).copy(alpha = 0.9f) // 蓝绿色
                                    "体育" -> Color(0xFFFFA726).copy(alpha = 0.9f) // 橙色
                                    else -> Color.Transparent
                                }

                                // 【重点修改】加高高度：非表头行高度设为 120.dp，完美展示上下滚动
                                val cellWidth = if (col == 0) 50.dp else 100.dp
                                val cellHeight = if (row == 0) 40.dp else 120.dp

                                Box(
                                    modifier = Modifier
                                        .size(cellWidth, cellHeight)
                                        .background(
                                            when {
                                                isHeader -> Color(0xFFF8F9FA) // 表头浅灰
                                                isCourse -> courseColor       // 有课显示课程颜色
                                                else -> Color(0xFFFAFAFA)     // 没课显示超浅灰
                                            }
                                        )
                                        .border(0.5.dp, Color(0xFFEEEEEE)) // 网格线
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when {
                                            row == 0 && col == 0 -> "时间"
                                            row == 0 -> days[col] // 顶部星期几
                                            col == 0 -> "第\n${row}\n节" // 左侧节数
                                            isCourse -> "$courseName\n\n@$room" // 课程与教室
                                            else -> "" // 没课直接留白，显得清爽
                                        },
                                        fontSize = if (isHeader) 13.sp else 12.sp,
                                        fontWeight = if (isHeader || isCourse) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isHeader) Color.DarkGray else if (isCourse) Color.White else Color.Transparent,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------- 3. DDL追踪 (原 TodoList 演示) ----------
@Composable
fun DDLTrackerScreen() {
    var assignments by remember { mutableStateOf(listOf(
        Task(1, "【周三前】提交Android作业代码", false),
        Task(2, "【本周末】复习单词 Unit 1-5", false),
        Task(3, "【已完成】社团活动策划案撰写", true)
    )) }
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("DDL ", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF2C3E50))
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            elevation = 0.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("添加新的作业或考试...", fontSize = 14.sp) },
                    modifier = Modifier.weight(1f).height(50.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color(0xFFF9F9F9),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colors.primary
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = {
                        if (text.isNotBlank()) {
                            assignments = listOf(Task(assignments.size + 1, text, false)) + assignments
                            text = ""
                        }
                    },
                    enabled = text.isNotBlank(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("添加")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(assignments) { task ->
                TaskItem(
                    task = task,
                    onCheckboxChange = { isChecked ->
                        assignments = assignments.map { if (it.id == task.id) it.copy(isCompleted = isChecked) else it }
                    },
                    onDelete = { assignments = assignments.filter { it.id != task.id } }
                )
            }
        }
    }
}

data class Task(val id: Int, val title: String, val isCompleted: Boolean)

@Composable
fun TaskItem(task: Task, onCheckboxChange: (Boolean) -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 0.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckboxChange,
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colors.primary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = task.title,
                fontSize = 15.sp,
                color = if (task.isCompleted) Color.Gray else Color.Black,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "删除", tint = Color(0xFFFF8A65))
            }
        }
    }
}

// ---------- 业务化重构的组件 (保留原有核心 API，修改视觉表现) ----------

// 原 CounterDemo：自习番茄钟
@Composable
fun PomodoroCounter() {
    var focusCount by remember { mutableStateOf(0) }
    Card(
        modifier = Modifier.fillMaxWidth().height(120.dp),
        shape = MaterialTheme.shapes.large,
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("🍅 今日专注次数", fontSize = 13.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$focusCount", fontWeight = FontWeight.ExtraBold, fontSize = 36.sp, color = Color(0xFFE53935))
                Text(" 次", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { focusCount += 1 },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(40.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFEBEE), contentColor = Color(0xFFE53935))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "增加打卡", modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

// 原 AnimationDemo：动态课程提醒卡片
@Composable
fun ExpandableCourseCard() {
    var expanded by remember { mutableStateOf(false) }

    val height by animateDpAsState(
        targetValue = if (expanded) 160.dp else 70.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "height"
    )
    // 展开时颜色变为品牌蓝，收起时为纯白
    val backgroundColor by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colors.primary else Color.White,
        animationSpec = tween(durationMillis = 300), label = "color"
    )
    val contentColor by animateColorAsState(
        targetValue = if (expanded) Color.White else Color.Black,
        animationSpec = tween(durationMillis = 300), label = "textColor"
    )
    val cornerRadius by animateDpAsState(
        targetValue = if (expanded) 24.dp else 12.dp,
        animationSpec = tween(durationMillis = 300), label = "radius"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = if (expanded) 6.dp else 0.dp,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(cornerRadius)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .background(backgroundColor)
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 左侧小色块
                    Box(modifier = Modifier.size(10.dp).clip(androidx.compose.foundation.shape.CircleShape).background(if(expanded) Color.White else Color(0xFF64B5F6)))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("移动应用软件开发", fontWeight = FontWeight.Bold, color = contentColor, fontSize = 16.sp)
                }

                // 仅在展开时显示的内容，带淡入效果
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn(animationSpec = tween(400)) + expandVertically(),
                    exit = fadeOut(animationSpec = tween(200)) + shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(top = 16.dp, start = 22.dp)) {
                        Text("📍 教室:旧经管报告厅", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("👨‍🏫 授课教师: 王老师\n ", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, lineHeight = 20.sp)
                    }
                }
            }
        }
    }
}

// 原 ElasticCardDemo：专注悬浮球体验区
@Composable
fun InteractiveFocusWidget() {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    // 拖拽幅度大时变色，提醒松手回弹
    val widgetColor by animateColorAsState(
        targetValue = when {
            kotlin.math.abs(offsetX) > 150f || kotlin.math.abs(offsetY) > 150f -> Color(0xFFFF9800) // 警告橙色
            else -> Color(0xFF4FC3F7) // 清新蓝
        },
        animationSpec = tween(200), label = "widgetColor"
    )

    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        elevation = 0.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFFE1F5FE)).padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("拖拽小球任意位置，松开体验阻尼回弹", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp))

            // 弹性悬浮球
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .graphicsLayer {
                        translationX = offsetX
                        translationY = offsetY
                        rotationZ = (offsetX / 10f).coerceIn(-30f, 30f) // 加上细微的旋转，看起来更有灵性
                        scaleX = if (kotlin.math.abs(offsetX) > 50f) 0.9f else 1f // 拖拽时稍微被捏扁
                        scaleY = if (kotlin.math.abs(offsetX) > 50f) 0.9f else 1f
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                val springSpec = spring<Float>(
                                    dampingRatio = Spring.DampingRatioMediumBouncy, // 中等弹力
                                    stiffness = Spring.StiffnessLow // 较低硬度，让动画时间更长、更Q弹
                                )
                                scope.launch { animate(initialValue = offsetX, targetValue = 0f, animationSpec = springSpec) { value, _ -> offsetX = value } }
                                scope.launch { animate(initialValue = offsetY, targetValue = 0f, animationSpec = springSpec) { value, _ -> offsetY = value } }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                offsetX = (offsetX + dragAmount.x).coerceIn(-400f, 400f)
                                offsetY = (offsetY + dragAmount.y).coerceIn(-400f, 400f)
                            }
                        )
                    }
                    .background(widgetColor, shape = androidx.compose.foundation.shape.CircleShape)
                    .shadow(8.dp, androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "开始专注", tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }
    }
}

// ---------- 主题配色 (校园清新风) ----------
private val UniLightPalette = lightColors(
    primary = Color(0xFF42A5F5),      // 清新蓝
    primaryVariant = Color(0xFF1E88E5),
    secondary = Color(0xFF26A69A),    // 治愈绿
    background = Color(0xFFF4F7FB),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF2C3E50),
    onSurface = Color(0xFF2C3E50)
)

@Composable
fun UniHubTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = UniLightPalette,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}

// 预览
@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun PreviewApp() {
    UniHubTheme {
        UniHubApp()
    }
}