package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures

@Composable
fun KiddoSparkApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            KiddoBottomNav(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("learn") { LearnScreen(navController) }
            composable("create") { CreateScreen() }
            composable("parent") { ParentDashboardScreen() }
            composable("topic/{topicName}") { backStackEntry ->
                val topic = backStackEntry.arguments?.getString("topicName") ?: ""
                TopicScreen(topic, navController)
            }
        }
    }
}

@Composable
fun KiddoBottomNav(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("home", "Home", Icons.Filled.Home),
        BottomNavItem("learn", "Learn", Icons.Filled.School),
        BottomNavItem("create", "Create", Icons.Filled.Brush),
        BottomNavItem("parent", "Parents", Icons.Filled.Settings)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title, fontWeight = FontWeight.Bold) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

data class BottomNavItem(val route: String, val title: String, val icon: ImageVector)

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Hi Leo! \uD83D\uDC4B", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                Text("Ready to learn today?", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(
                color = BrightYellow,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Filled.Face, contentDescription = "Avatar", modifier = Modifier.padding(12.dp), tint = Color.DarkGray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Daily Challenge", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    Text("Complete 3 Math puzzles", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                }
                Icon(Icons.Filled.Star, contentDescription = "Star", tint = BrightYellow, modifier = Modifier.size(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Your Favorites", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        val favorites = listOf(
            Category("Math", Icons.Filled.Calculate, CoralOrange),
            Category("Animals", Icons.Filled.Pets, MintGreen),
            Category("Stories", Icons.AutoMirrored.Filled.MenuBook, PurpleAccent),
            Category("Colors", Icons.Filled.Palette, SkyBlue)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(favorites) { cat ->
                CategoryCard(cat) {
                    navController.navigate("topic/${cat.name}")
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: Category, onClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = category.color.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(category.icon, contentDescription = category.name, modifier = Modifier.size(48.dp), tint = category.color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(category.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

data class Category(val name: String, val icon: ImageVector, val color: Color)

@Composable
fun LearnScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("Learning Center", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(16.dp))
        
        val topics = listOf(
            Category("Alphabets", Icons.Filled.Language, CoralOrange),
            Category("Numbers", Icons.Filled.Calculate, BrightYellow),
            Category("Science", Icons.Filled.Science, MintGreen),
            Category("Shapes", Icons.Filled.Category, SkyBlue),
            Category("Drawing", Icons.Filled.Brush, PurpleAccent),
            Category("Stories", Icons.AutoMirrored.Filled.MenuBook, CoralOrange)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(topics) { topic ->
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = topic.color.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth().height(140.dp).clickable { 
                        navController.navigate("topic/${topic.name}")
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(topic.icon, contentDescription = topic.name, tint = topic.color, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(topic.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateScreen() {
    var lines by remember { mutableStateOf(emptyList<Line>()) }
    var currentColor by remember { mutableStateOf(Color.Red) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Drawing Board", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
            IconButton(onClick = { lines = emptyList() }) {
                Icon(Icons.Filled.Delete, contentDescription = "Clear", tint = MaterialTheme.colorScheme.error)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth().weight(1f).shadow(4.dp, RoundedCornerShape(16.dp)),
            color = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Canvas(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            lines = lines + Line(start = offset, end = offset, color = currentColor, strokeWidth = 10f)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val line = lines.last()
                            // Store smaller segments or update the last line? 
                            // Quick line segments implementation
                            lines = lines + Line(start = line.end, end = change.position, color = currentColor, strokeWidth = 10f)
                        }
                    )
                }
            ) {
                lines.forEach { line ->
                    drawLine(
                        color = line.color,
                        start = line.start,
                        end = line.end,
                        strokeWidth = line.strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            ColorSwatch(Color.Red, currentColor == Color.Red) { currentColor = Color.Red }
            ColorSwatch(Color.Blue, currentColor == Color.Blue) { currentColor = Color.Blue }
            ColorSwatch(Color.Green, currentColor == Color.Green) { currentColor = Color.Green }
            ColorSwatch(BrightYellow, currentColor == BrightYellow) { currentColor = BrightYellow }
            ColorSwatch(Color.Black, currentColor == Color.Black) { currentColor = Color.Black }
        }
    }
}

data class Line(val start: Offset, val end: Offset, val color: Color, val strokeWidth: Float)

@Composable
fun ColorSwatch(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = color,
        modifier = Modifier
            .size(48.dp)
            .shadow(if (isSelected) 8.dp else 2.dp, CircleShape)
            .clickable(onClick = onClick),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface) else null
    ) {}
}

@Composable
fun Center(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

@Composable
fun ParentDashboardScreen() {
    var screenTimeEnabled by remember { mutableStateOf(true) }
    var appLock by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("Parent Dashboard", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Settings & Analytics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Time Spent Today", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("1h 15m", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(progress = { 0.6f }, modifier = Modifier.fillMaxWidth(), color = MintGreen)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Controls", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Daily Screen Time Limit (2h)", style = MaterialTheme.typography.bodyLarge)
                    Switch(checked = screenTimeEnabled, onCheckedChange = { screenTimeEnabled = it })
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Safe App Lock", style = MaterialTheme.typography.bodyLarge)
                    Switch(checked = appLock, onCheckedChange = { appLock = it })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicScreen(topic: String, navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topic, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (topic == "Math" || topic == "Numbers") {
                MathGame()
            } else if (topic == "Alphabets") {
                AlphabetGame()
            } else if (topic == "Stories") {
                StoryView()
            } else {
                Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(100.dp), tint = BrightYellow)
                Spacer(modifier = Modifier.height(24.dp))
                Text("Lots of fun activities for $topic coming soon!", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun MathGame() {
    var num1 by remember { mutableIntStateOf((1..10).random()) }
    var num2 by remember { mutableIntStateOf((1..10).random()) }
    var answer by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("What is $num1 + $num2?", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = answer,
            onValueChange = { answer = it },
            label = { Text("Answer") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (answer.toIntOrNull() == num1 + num2) {
                message = "Correct! 🌟"
                num1 = (1..10).random()
                num2 = (1..10).random()
                answer = ""
            } else {
                message = "Oops, try again!"
            }
        }) {
            Text("Check")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, style = MaterialTheme.typography.titleLarge, color = if (message.startsWith("Correct")) MintGreen else Color.Red)
    }
}

@Composable
fun AlphabetGame() {
    val letters = ('A'..'Z').toList()
    var currentIndex by remember { mutableIntStateOf(0) }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = CoralOrange.copy(alpha = 0.2f)),
            modifier = Modifier.size(200.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(letters[currentIndex].toString(), style = MaterialTheme.typography.displayLarge.copy(fontSize = 120.sp, fontWeight = FontWeight.ExtraBold), color = CoralOrange)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { if (currentIndex > 0) currentIndex-- }) { Text("Previous") }
            Button(onClick = { if (currentIndex < letters.size - 1) currentIndex++ }) { Text("Next") }
        }
    }
}

@Composable
fun StoryView() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("The Brave Little Lion", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Once upon a time in a sunny savanna, there lived a small lion named Leo. " +
            "Leo was smaller than the other lions, but he had a very loud ROAR! " +
            "One day, Leo helped a little bird who was lost. The bird became his best friend, " +
            "and they explored the savanna together.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(16.dp)
        )
        Icon(Icons.Filled.Pets, contentDescription = null, modifier = Modifier.size(64.dp), tint = MintGreen)
    }
}
