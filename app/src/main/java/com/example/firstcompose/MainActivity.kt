
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import kotlin.math.roundToInt
import kotlinx.coroutines.launch

// 屏幕枚举
sealed class Screen {
    object Menu : Screen()
    object Counter : Screen()
    object TwoDScroll : Screen()
    object Animation : Screen()
    object ElasticCard : Screen()
    object TodoList : Screen()
    object ListCompare : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                Navigation()
            }
        }
    }
}

@Composable
fun Navigation() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Menu) }

    when (currentScreen) {
        Screen.Menu -> MenuScreen(onScreenSelected = { currentScreen = it })
        Screen.Counter -> CounterScreen(onBack = { currentScreen = Screen.Menu })
        Screen.TwoDScroll -> TwoDScrollScreen(onBack = { currentScreen = Screen.Menu })
        Screen.Animation -> AnimationScreen(onBack = { currentScreen = Screen.Menu })
        Screen.ElasticCard -> ElasticCardScreen(onBack = { currentScreen = Screen.Menu })
        Screen.TodoList -> TodoListScreen(onBack = { currentScreen = Screen.Menu })
        Screen.ListCompare -> ListCompareScreen(onBack = { currentScreen = Screen.Menu })
    }
}

// 主菜单界面
@Composable
fun MenuScreen(onScreenSelected: (Screen) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compose 功能演示") },
                backgroundColor = MaterialTheme.colors.primarySurface
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 计数器演示按钮
            MenuButton(
                text = "计数器演示（状态驱动 UI）",
                onClick = { onScreenSelected(Screen.Counter) }
            )
            // 二维滚动演示按钮
            MenuButton(
                text = "二维滚动演示（上下+左右）",
                onClick = { onScreenSelected(Screen.TwoDScroll) }
            )
            // 动画演示按钮
            MenuButton(
                text = "复杂动画演示（高度/颜色变化）",
                onClick = { onScreenSelected(Screen.Animation) }
            )
            // 弹性卡片演示按钮
            MenuButton(
                text = "弹性卡片演示（拖拽+回弹）",
                onClick = { onScreenSelected(Screen.ElasticCard) }
            )
            // 待办事项演示按钮
            MenuButton(
                text = "待办列表演示（增删改）",
                onClick = { onScreenSelected(Screen.TodoList) }
            )
            MenuButton(
                text = "列表性能对比（XML vs Compose）",
                onClick = { onScreenSelected(Screen.ListCompare) }
            )
        }
    }
}

@Composable
fun MenuButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = ButtonDefaults.elevation(defaultElevation = 4.dp)
    ) {
        Text(text = text, fontSize = 18.sp)
    }
}

// 通用返回按钮
@Composable
fun BackButton(onBack: () -> Unit) {
    IconButton(onClick = onBack) {
        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
    }
}

// ---------- 演示界面 ----------

// 计数器演示界面
@Composable
fun CounterScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("计数器演示") },
                navigationIcon = { BackButton(onBack) },
                backgroundColor = MaterialTheme.colors.primarySurface
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CounterDemo()
        }
    }
}

// 二维滚动演示界面
@Composable
fun TwoDScrollScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("二维滚动演示") },
                navigationIcon = { BackButton(onBack) },
                backgroundColor = MaterialTheme.colors.primarySurface
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TwoDScrollDemo()
        }
    }
}

// 动画演示界面
@Composable
fun AnimationScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("复杂动画演示") },
                navigationIcon = { BackButton(onBack) },
                backgroundColor = MaterialTheme.colors.primarySurface
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimationDemo()
        }
    }
}

// 弹性卡片演示界面
@Composable
fun ElasticCardScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("弹性卡片演示") },
                navigationIcon = { BackButton(onBack) },
                backgroundColor = MaterialTheme.colors.primarySurface
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ElasticCardDemo()
        }
    }
}

// 待办事项列表演示界面
@Composable
fun TodoListScreen(onBack: () -> Unit) {
    val todoListState = rememberTodoListState()
    var text by remember { mutableStateOf("") }
    val onAdd: () -> Unit = {
        if (todoListState.addTask(text)) {
            text = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("待办事项演示") },
                navigationIcon = { BackButton(onBack) },
                backgroundColor = MaterialTheme.colors.primarySurface
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TaskInputBar(
                text = text,
                onTextChange = { text = it },
                onAdd = onAdd,
                enabled = text.isNotBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TaskList(
                tasks = todoListState.tasks,
                onToggle = todoListState::toggleTask,
                onDelete = todoListState::deleteTask
            )
        }
    }
}

// ---------- 原有组件（无需修改，但保留） ----------

data class Task(
    val id: Int,
    val title: String,
    val isCompleted: Boolean
)

@Composable
fun TaskItem(
    task: Task,
    onToggle: (id: Int, checked: Boolean) -> Unit,
    onDelete: (id: Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { checked -> onToggle(task.id, checked) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = task.title,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onDelete(task.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "删除")
            }
        }
    }
}


@Composable
fun TaskInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onAdd: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = onTextChange,
            label = { Text("新任务") },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onAdd,
            enabled = enabled
        ) {
            Icon(Icons.Default.Add, contentDescription = "添加")
            Spacer(modifier = Modifier.width(4.dp))
            Text("添加")
        }
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onToggle: (Int, Boolean) -> Unit,
    onDelete: (Int) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = tasks,
            key = { it.id }
        ) { task ->
            TaskItem(
                task = task,
                onToggle = onToggle,
                onDelete = onDelete
            )
        }
    }
}
@Composable
fun CounterDemo() {
    var count by remember { mutableStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "状态驱动 UI 演示",
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "点击次数: $count",
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { count++ }) {
                Text("点击增加")
            }
        }
    }
}

@Composable
fun TwoDScrollDemo() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(8.dp, shape = MaterialTheme.shapes.medium),
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "二维滚动演示（上下 + 左右滚动）",
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .verticalScroll(rememberScrollState())
                    .horizontalScroll(rememberScrollState())
                    .border(1.dp, Color.Gray, MaterialTheme.shapes.small)
            ) {
                Column {
                    repeat(20) { row ->
                        Row {
                            repeat(10) { col ->
                                Box(
                                    modifier = Modifier
                                        .size(80.dp, 60.dp)
                                        .padding(4.dp)
                                        .background(
                                            if ((row + col) % 2 == 0)
                                                MaterialTheme.colors.primary.copy(alpha = 0.3f)
                                            else
                                                MaterialTheme.colors.secondary.copy(alpha = 0.3f)
                                        )
                                        .border(1.dp, Color.LightGray)
                                ) {
                                    Text(
                                        text = "$row-$col",
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "👉 可以上下左右滚动这个区域",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun AnimationDemo() {
    var expanded by remember { mutableStateOf(false) }

    val height by animateDpAsState(
        targetValue = if (expanded) 200.dp else 0.dp,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "height"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (expanded) Color(0xFFE91E63) else Color(0xFF2196F3),
        animationSpec = tween(durationMillis = 500),
        label = "color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "复杂动画演示",
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.primary
                )
                Button(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "收起" else "展开")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height)
                        .animateContentSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundColor)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✨ 动画区域 ✨\n高度和颜色都在变化",
                            color = Color.White,
                            style = MaterialTheme.typography.body1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ElasticCardDemo() {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    // 修改颜色：天蓝色、品红色
    val cardColor by animateColorAsState(
        targetValue = when {
            kotlin.math.abs(offsetX) > 100f -> Color(0xFF87CEEB) // 天蓝色
            kotlin.math.abs(offsetY) > 100f -> Color(0xFFFF00FF) // 品红色
            else -> Color(0xFF6200EE)         // 紫色
        },
        animationSpec = tween(300),
        label = "color"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    translationX = offsetX
                    translationY = offsetY
                    rotationZ = (offsetX / 20f).coerceIn(-15f, 15f)
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { },
                        onDragEnd = {
                            scope.launch {
                                val springSpec = spring<Float>(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                                animate(
                                    initialValue = offsetX,
                                    targetValue = 0f,
                                    animationSpec = springSpec
                                ) { value, _ ->
                                    offsetX = value
                                }
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
                            offsetX = (offsetX + dragAmount.x).coerceIn(-200f, 200f)
                            offsetY = (offsetY + dragAmount.y).coerceIn(-200f, 200f)
                        }
                    )
                },
            backgroundColor = cardColor  // 关键修改：应用动画颜色
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "拖拽我！\n松开后弹性回弹",
                    color = Color.Black,
                    style = MaterialTheme.typography.h6
                )
            }
        }
    }

    Text(
        text = "偏移量: (${offsetX.roundToInt()}, ${offsetY.roundToInt()})",
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        textAlign = TextAlign.Center
    )
}

// 自定义配色方案
private val LightColorPalette = lightColors(
    primary = Color(0xFF6200EE),
    primaryVariant = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF000000),
    onSurface = Color(0xFF000000),
)

private val DarkColorPalette = darkColors(
    primary = Color(0xFFBB86FC),
    primaryVariant = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette
    MaterialTheme(
        colors = colors,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}

// 预览（仅预览菜单界面）
@Preview(
    name = "浅色模式",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = Devices.PIXEL_4
)
@Preview(
    name = "深色模式",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_4
)
@Preview(
    name = "平板横屏",
    device = Devices.NEXUS_10
)
@Composable
fun PreviewMenu() {
    MyAppTheme {
        MenuScreen(onScreenSelected = {})
    }
}

    
