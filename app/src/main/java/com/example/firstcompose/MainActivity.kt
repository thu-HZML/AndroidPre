package com.example.firstcompose

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

sealed class UniTab(val title: String, val icon: ImageVector) {
    object Home : UniTab("今日校园", Icons.Default.Home)
    object Schedule : UniTab("课表矩阵", Icons.Default.DateRange)
    object Ddl : UniTab("DDL 追踪", Icons.Default.Notifications)
    object Compare : UniTab("性能对比", Icons.Default.Star)
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
    val tabs = listOf(UniTab.Home, UniTab.Schedule, UniTab.Ddl, UniTab.Compare)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentTab) {
                            UniTab.Home -> "UniHub 校园空间"
                            UniTab.Schedule -> "本学期专属课表"
                            UniTab.Ddl -> "DDL 危机化解中心"
                            UniTab.Compare -> "列表性能对比"
                        },
                        fontWeight = FontWeight.Bold
                    )
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
                .background(Color(0xFFF4F7FB))
        ) {
            when (currentTab) {
                UniTab.Home -> HomeScreen()
                UniTab.Schedule -> CourseMatrixScreen()
                UniTab.Ddl -> DdlTrackerScreen()
                UniTab.Compare -> ListCompareContent(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun BackButton(onBack: () -> Unit) {
    IconButton(onClick = onBack) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "返回"
        )
    }
}

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                PomodoroCounter()
            }
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                shape = MaterialTheme.shapes.large,
                elevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("本周超越了", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        "85% 同学",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.primary
                    )
                }
            }
        }

        Text(
            text = "提醒：下一节课",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 8.dp)
        )
        ExpandableCourseCard()

        Text(
            text = "专注悬浮球（互动体验）",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 8.dp)
        )
        InteractiveFocusWidget()
    }
}

@Composable
fun CourseMatrixScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "本学期专属课表",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF2C3E50)
        )
        Text(
            text = "上下滑动查看全天排课，左右滑动查看全周",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxSize()
                .shadow(8.dp, shape = MaterialTheme.shapes.large),
            elevation = 0.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .horizontalScroll(rememberScrollState())
                    .background(Color.White)
            ) {
                Column {
                    val days = listOf("时间", "周一", "周二", "周三", "周四", "周五", "周六", "周日")
                    repeat(13) { row ->
                        Row {
                            repeat(8) { col ->
                                val isHeader = row == 0 || col == 0
                                var courseName = ""
                                var room = ""
                                var isCourse = false

                                if (!isHeader) {
                                    when {
                                        col == 1 && row == 3 -> {
                                            courseName = "移动应用\n软件开发"
                                            room = "计算机大楼 302"
                                            isCourse = true
                                        }
                                        col == 3 && row == 1 -> {
                                            courseName = "移动应用\n软件开发"
                                            room = "计算机大楼 302"
                                            isCourse = true
                                        }
                                        col == 3 && row == 2 -> {
                                            courseName = "数据库原理"
                                            room = "五教 5202"
                                            isCourse = true
                                        }
                                        col == 4 && row == 2 -> {
                                            courseName = "嵌入式开发"
                                            room = "三教 1103"
                                            isCourse = true
                                        }
                                        col == 4 && row == 4 -> {
                                            courseName = "体育"
                                            room = "北体育馆"
                                            isCourse = true
                                        }
                                    }
                                }

                                val courseColor = when (courseName) {
                                    "移动应用\n软件开发" -> MaterialTheme.colors.primary.copy(alpha = 0.9f)
                                    "数据库原理" -> Color(0xFF7E57C2).copy(alpha = 0.9f)
                                    "嵌入式开发" -> Color(0xFF26A69A).copy(alpha = 0.9f)
                                    "体育" -> Color(0xFFFFA726).copy(alpha = 0.9f)
                                    else -> Color.Transparent
                                }

                                val cellWidth = if (col == 0) 50.dp else 100.dp
                                val cellHeight = if (row == 0) 40.dp else 120.dp

                                Box(
                                    modifier = Modifier
                                        .size(cellWidth, cellHeight)
                                        .background(
                                            when {
                                                isHeader -> Color(0xFFF8F9FA)
                                                isCourse -> courseColor
                                                else -> Color(0xFFFAFAFA)
                                            }
                                        )
                                        .border(0.5.dp, Color(0xFFEEEEEE))
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when {
                                            row == 0 && col == 0 -> "时间"
                                            row == 0 -> days[col]
                                            col == 0 -> "第\n$row\n节"
                                            isCourse -> "$courseName\n\n@$room"
                                            else -> ""
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

@Composable
fun DdlTrackerScreen() {
    val todoListState = rememberTodoListState(
        initialTasks = listOf(
            Task(1, "【周三前】提交 Android 大作业代码", false),
            Task(2, "【本周末】复习六级单词 Unit 1-5", false),
            Task(3, "【已完成】社团活动策划案撰写", true)
        )
    )
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "DDL 危机化解中心",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF2C3E50)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            elevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("添加新的作业或考试...", fontSize = 14.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
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
                        if (todoListState.addTask(text)) {
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
            items(todoListState.tasks, key = { it.id }) { task ->
                TaskItem(
                    task = task,
                    onCheckboxChange = { checked ->
                        todoListState.toggleTask(task.id, checked)
                    },
                    onDelete = {
                        todoListState.deleteTask(task.id)
                    }
                )
            }
        }
    }
}

data class Task(
    val id: Int,
    val title: String,
    val isCompleted: Boolean
)

@Composable
fun TaskItem(
    task: Task,
    onCheckboxChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 0.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp),
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
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = Color(0xFFFF8A65)
                )
            }
        }
    }
}

@Composable
fun PomodoroCounter() {
    var focusCount by remember { mutableStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = MaterialTheme.shapes.large,
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("今日专注次数", fontSize = 13.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$focusCount",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 36.sp,
                    color = Color(0xFFE53935)
                )
                Text(" 次", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { focusCount += 1 },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFFFEBEE),
                        contentColor = Color(0xFFE53935)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "增加打卡",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ExpandableCourseCard() {
    var expanded by remember { mutableStateOf(false) }

    val height by animateDpAsState(
        targetValue = if (expanded) 160.dp else 70.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "height"
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colors.primary else Color.White,
        animationSpec = tween(durationMillis = 300),
        label = "color"
    )
    val contentColor by animateColorAsState(
        targetValue = if (expanded) Color.White else Color.Black,
        animationSpec = tween(durationMillis = 300),
        label = "textColor"
    )
    val cornerRadius by animateDpAsState(
        targetValue = if (expanded) 24.dp else 12.dp,
        animationSpec = tween(durationMillis = 300),
        label = "radius"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = if (expanded) 6.dp else 0.dp,
        shape = RoundedCornerShape(cornerRadius)
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
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (expanded) Color.White else Color(0xFF64B5F6))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "14:00 - 15:35 | 移动应用开发",
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        fontSize = 16.sp
                    )
                }

                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn(animationSpec = tween(400)) + expandVertically(),
                    exit = fadeOut(animationSpec = tween(200)) + shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(top = 16.dp, start = 22.dp)) {
                        Text(
                            text = "教室：计算机大楼 302 机房",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "授课教师：张老师\n备注：今天小组要进行阶段性项目展示，记得带电脑。",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveFocusWidget() {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    val widgetColor by animateColorAsState(
        targetValue = when {
            kotlin.math.abs(offsetX) > 150f || kotlin.math.abs(offsetY) > 150f -> Color(0xFFFF9800)
            else -> Color(0xFF4FC3F7)
        },
        animationSpec = tween(200),
        label = "widgetColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        elevation = 0.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE1F5FE))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "拖拽小球任意位置，松开体验阻尼回弹",
                color = Color.Gray,
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .graphicsLayer {
                        translationX = offsetX
                        translationY = offsetY
                        rotationZ = (offsetX / 10f).coerceIn(-30f, 30f)
                        scaleX = if (kotlin.math.abs(offsetX) > 50f) 0.9f else 1f
                        scaleY = if (kotlin.math.abs(offsetX) > 50f) 0.9f else 1f
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                val springSpec = spring<Float>(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                                scope.launch {
                                    animate(
                                        initialValue = offsetX,
                                        targetValue = 0f,
                                        animationSpec = springSpec
                                    ) { value, _ ->
                                        offsetX = value
                                    }
                                }
                                scope.launch {
                                    animate(
                                        initialValue = offsetY,
                                        targetValue = 0f,
                                        animationSpec = springSpec
                                    ) { value, _ ->
                                        offsetY = value
                                    }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                offsetX = (offsetX + dragAmount.x).coerceIn(-400f, 400f)
                                offsetY = (offsetY + dragAmount.y).coerceIn(-400f, 400f)
                            }
                        )
                    }
                    .background(widgetColor, shape = CircleShape)
                    .shadow(8.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "开始专注",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

private val UniLightPalette = lightColors(
    primary = Color(0xFF42A5F5),
    primaryVariant = Color(0xFF1E88E5),
    secondary = Color(0xFF26A69A),
    background = Color(0xFFF4F7FB),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF2C3E50),
    onSurface = Color(0xFF2C3E50)
)

private val UniDarkPalette = darkColors(
    primary = Color(0xFF90CAF9),
    primaryVariant = Color(0xFF42A5F5),
    secondary = Color(0xFF80CBC4),
    background = Color(0xFF0F1720),
    surface = Color(0xFF16202A),
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun UniHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) UniDarkPalette else UniLightPalette,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}

@Preview(
    name = "浅色模式",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    group = "主题测试"
)
@Preview(
    name = "深色模式",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    group = "主题测试"
)
annotation class ThemePreviews

@Preview(name = "Pixel Fold", device = "id:pixel_fold", showBackground = true, group = "设备适配")
@Preview(name = "平板横屏", device = Devices.NEXUS_10, showBackground = true, group = "设备适配")
annotation class DevicePreviews

@ThemePreviews
@DevicePreviews
@Composable
fun PreviewApp() {
    UniHubTheme {
        UniHubApp()
    }
}
