package com.example.coffeebeans

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.NumberPicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CoffeeApp() }
    }
}

@Entity(tableName = "beans")
data class CoffeeBean(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roaster: String = "",
    val roastDate: String = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
    val variety: String = "",
    val process: String = "",
    val roastLevel: String = "",
    val country: String = "",
    val province: String = "",
    val village: String = "",
    val estate: String = "",
    val washingStation: String = "",
    val batch: String = "",
    val packageWeight: Double = 0.0,
    val remainingGrams: Double = 0.0,
    val totalPrice: Double = 0.0,
    val pricePerGram: Double = 0.0,
    val restingDays: Int = 7,
    val flavorDescription: String = "",
    val flavorNotes: String = "",
    val imageUri: String = "",
    val rating: Int = 0,
    val inventoryStatus: String = "有库存",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "recipes",
    foreignKeys = [
        ForeignKey(
            entity = CoffeeBean::class,
            parentColumns = ["id"],
            childColumns = ["beanId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("beanId")]
)
data class BrewingRecipe(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val beanId: Long,
    val brewer: String = "",
    val grindSize: String = "",
    val waterTemperature: Double = 93.0,
    val dose: Double = 15.0,
    val waterAmount: Double = 250.0,
    val ratio: String = "1:16.7",
    val time: String = "2:45",
    val tastingNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "consumption_logs",
    foreignKeys = [
        ForeignKey(
            entity = CoffeeBean::class,
            parentColumns = ["id"],
            childColumns = ["beanId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("beanId")]
)
data class ConsumptionLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val beanId: Long,
    val date: String = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
    val grams: Double = 15.0,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class BeanWithRecipes(
    val bean: CoffeeBean,
    val recipes: List<BrewingRecipe>
)

data class ReportStats(
    val title: String,
    val totalGrams: Double,
    val totalCost: Double,
    val topBean: String
)

@Dao
interface CoffeeDao {
    @Query("SELECT * FROM beans ORDER BY createdAt DESC")
    fun observeBeans(): Flow<List<CoffeeBean>>

    @Query("SELECT * FROM beans WHERE id = :id")
    fun observeBean(id: Long): Flow<CoffeeBean?>

    @Query("SELECT * FROM recipes WHERE beanId = :beanId ORDER BY createdAt DESC")
    fun observeRecipes(beanId: Long): Flow<List<BrewingRecipe>>

    @Query("SELECT * FROM beans ORDER BY createdAt DESC")
    suspend fun allBeansOnce(): List<CoffeeBean>

    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    suspend fun allRecipesOnce(): List<BrewingRecipe>

    @Query("SELECT * FROM consumption_logs ORDER BY date DESC, createdAt DESC")
    fun observeLogs(): Flow<List<ConsumptionLog>>

    @Query("SELECT * FROM consumption_logs ORDER BY date DESC, createdAt DESC")
    suspend fun allLogsOnce(): List<ConsumptionLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBean(bean: CoffeeBean): Long

    @Update
    suspend fun updateBean(bean: CoffeeBean)

    @Query("DELETE FROM beans WHERE id = :id")
    suspend fun deleteBean(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: BrewingRecipe): Long

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteRecipe(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ConsumptionLog): Long
}

@Database(entities = [CoffeeBean::class, BrewingRecipe::class, ConsumptionLog::class], version = 4, exportSchema = false)
abstract class CoffeeDatabase : RoomDatabase() {
    abstract fun dao(): CoffeeDao

    companion object {
        @Volatile private var instance: CoffeeDatabase? = null

        fun get(context: Context): CoffeeDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CoffeeDatabase::class.java,
                    "coffee-cellar.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build().also { instance = it }
            }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE beans ADD COLUMN roastLevel TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE beans ADD COLUMN country TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE beans ADD COLUMN province TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE beans ADD COLUMN village TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE beans ADD COLUMN estate TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE beans ADD COLUMN washingStation TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE beans ADD COLUMN batch TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE beans ADD COLUMN packageWeight REAL NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE beans ADD COLUMN remainingGrams REAL NOT NULL DEFAULT 0")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS consumption_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        beanId INTEGER NOT NULL,
                        date TEXT NOT NULL,
                        grams REAL NOT NULL,
                        note TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY(beanId) REFERENCES beans(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_consumption_logs_beanId ON consumption_logs(beanId)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE beans ADD COLUMN totalPrice REAL NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE beans ADD COLUMN flavorDescription TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}

class CoffeeViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = CoffeeDatabase.get(application).dao()

    val beans: StateFlow<List<CoffeeBean>> = dao.observeBeans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val logs: StateFlow<List<ConsumptionLog>> = dao.observeLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val roasterSuggestions: StateFlow<List<String>> = beans.map { list ->
        list.map { it.roaster }.filter { it.isNotBlank() }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val processSuggestions: StateFlow<List<String>> = beans.map { list ->
        (list.map { it.process }.filter { it.isNotBlank() } + listOf("水洗", "日晒", "蜜处理", "厌氧发酵", "湿刨")).distinct()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val roastLevelSuggestions: StateFlow<List<String>> = beans.map { list ->
        (list.map { it.roastLevel }.filter { it.isNotBlank() } + listOf("浅烘", "中浅烘", "中烘", "中深烘", "深烘")).distinct()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun beanWithRecipes(beanId: Long): StateFlow<BeanWithRecipes?> {
        return dao.observeBean(beanId).combine(dao.observeRecipes(beanId)) { bean, recipes ->
            bean?.let { BeanWithRecipes(it, recipes) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    }

    fun recipeCount(beanId: Long): StateFlow<Int> {
        return dao.observeRecipes(beanId).map { it.size }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    }

    fun saveBean(bean: CoffeeBean, onSaved: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = if (bean.id == 0L) dao.insertBean(bean) else {
                dao.updateBean(bean)
                bean.id
            }
            onSaved(id)
        }
    }

    fun deleteBean(id: Long, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            dao.deleteBean(id)
            onDone()
        }
    }

    fun saveRecipe(recipe: BrewingRecipe) {
        viewModelScope.launch { dao.insertRecipe(recipe) }
    }

    fun deleteRecipe(id: Long) {
        viewModelScope.launch { dao.deleteRecipe(id) }
    }

    fun logDrink(bean: CoffeeBean, grams: Double, note: String = "") {
        viewModelScope.launch {
            dao.insertLog(ConsumptionLog(beanId = bean.id, grams = grams, note = note))
            val remaining = (bean.remainingGrams - grams).coerceAtLeast(0.0)
            val status = when {
                remaining <= 0.0 && bean.packageWeight > 0.0 -> "已喝完"
                remaining <= 30.0 && bean.packageWeight > 0.0 -> "快喝完"
                bean.drinkWindow().startsWith("还需") -> "养豆中"
                else -> "有库存"
            }
            dao.updateBean(bean.copy(remainingGrams = remaining, inventoryStatus = status))
        }
    }

    fun exportCsv(context: Context) {
        viewModelScope.launch {
            val beans = dao.allBeansOnce()
            val recipes = dao.allRecipesOnce()
            val logs = dao.allLogsOnce()
            val file = File(context.cacheDir, "bean-cellar-export.csv")
            file.writeText(buildCsv(beans, recipes, logs))
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val share = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(share, "导出咖啡数据"))
        }
    }
}

class CoffeeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CoffeeViewModel(application) as T
    }
}

private val LightColors = lightColorScheme(
    primary = Color(0xFF7B4E2D),
    secondary = Color(0xFF4F6F52),
    tertiary = Color(0xFFC06C3E),
    background = Color(0xFFFBF8F4),
    surface = Color(0xFFFFFCF8),
    surfaceVariant = Color(0xFFECE0D4),
    onPrimary = Color.White,
    onSecondary = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFE7B98E),
    secondary = Color(0xFFA9C7A5),
    tertiary = Color(0xFFF0A06E),
    background = Color(0xFF171411),
    surface = Color(0xFF211B16),
    surfaceVariant = Color(0xFF3A3129),
    onPrimary = Color(0xFF3F250F)
)

@Composable
fun CoffeeApp() {
    val context = LocalContext.current
    val viewModel: CoffeeViewModel = viewModel(
        factory = CoffeeViewModelFactory(context.applicationContext as Application)
    )
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors) {
        Surface(Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "beans") {
                composable("beans") {
                    BeanListScreen(
                        viewModel = viewModel,
                        onAdd = { navController.navigate("edit/0") },
                        onOpen = { navController.navigate("detail/$it") },
                        onEdit = { navController.navigate("edit/$it") },
                        onReport = { navController.navigate("report") },
                        onExport = { viewModel.exportCsv(context) }
                    )
                }
                composable("report") {
                    ReportScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    "detail/{beanId}",
                    arguments = listOf(navArgument("beanId") { type = NavType.LongType })
                ) { entry ->
                    val beanId = entry.arguments?.getLong("beanId") ?: 0L
                    BeanDetailScreen(
                        beanId = beanId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate("edit/$beanId") }
                    )
                }
                composable(
                    "edit/{beanId}",
                    arguments = listOf(navArgument("beanId") { type = NavType.LongType })
                ) { entry ->
                    BeanEditScreen(
                        beanId = entry.arguments?.getLong("beanId") ?: 0L,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onSaved = { id ->
                            if (navController.previousBackStackEntry != null) {
                                navController.popBackStack()
                            } else {
                                navController.navigate("detail/$id") {
                                    popUpTo("beans")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeanListScreen(
    viewModel: CoffeeViewModel,
    onAdd: () -> Unit,
    onOpen: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    onReport: () -> Unit,
    onExport: () -> Unit
) {
    val beans by viewModel.beans.collectAsState()
    val logs by viewModel.logs.collectAsState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("豆仓", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = onReport) {
                        Icon(Icons.Rounded.BarChart, contentDescription = "报告")
                    }
                    IconButton(onClick = onExport) {
                        Icon(Icons.Rounded.FileDownload, contentDescription = "导出 CSV")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Rounded.Add, contentDescription = "添加咖啡豆")
            }
        },
        bottomBar = { BottomBar() }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .premiumBackground()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { CellarHeader(beans, logs, onReport) }
            if (beans.isNotEmpty()) {
                item { ForecastStrip(beans) }
                item { InsightCharts(beans) }
            }
            if (beans.isEmpty()) {
                item { EmptyState(onAdd) }
            } else {
                items(beans, key = { it.id }) { bean ->
                    BeanCard(
                        bean = bean,
                        onClick = { onOpen(bean.id) },
                        onEdit = { onEdit(bean.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CellarHeader(beans: List<CoffeeBean>, logs: List<ConsumptionLog>, onReport: () -> Unit) {
    val ready = beans.count { it.drinkWindow().startsWith("现在适饮") }
    val monthStart = LocalDate.now().minusDays(30)
    val monthGrams = logs.filter { runCatching { LocalDate.parse(it.date) }.getOrNull()?.isBefore(monthStart) == false }.sumOf { it.grams }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Rounded.LocalCafe, contentDescription = null, tint = Color.White)
            Text("记录你的手冲咖啡豆", color = Color.White.copy(alpha = 0.82f))
            Text(
                "${beans.size} 款咖啡 · $ready 款适饮",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text("近 30 天消耗 ${monthGrams.clean()}g", color = Color.White.copy(alpha = 0.86f))
            FilledTonalButton(onClick = onReport, colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.White.copy(alpha = 0.18f), contentColor = Color.White)) {
                Text("查看周/月报告")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BeanCard(bean: CoffeeBean, onClick: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onEdit
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            PackageImage(bean.imageUri, Modifier.size(78.dp).clip(RoundedCornerShape(18.dp)))
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(bean.roaster.ifBlank { "未命名烘焙商" }, fontWeight = FontWeight.Bold)
                Text(
                    bean.variety.ifBlank { bean.process.ifBlank { "咖啡豆" } },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    RatingStars(bean.rating)
                    Text(bean.inventoryStatus, style = MaterialTheme.typography.labelMedium)
                }
                if (bean.remainingGrams > 0.0 || bean.packageWeight > 0.0) {
                    Text("库存 ${bean.remainingGrams.clean()}g / ${bean.packageWeight.clean()}g", style = MaterialTheme.typography.labelMedium)
                }
                Text(bean.drinkWindow(), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Rounded.Edit, contentDescription = "编辑咖啡豆")
            }
        }
    }
}

@Composable
private fun ForecastStrip(beans: List<CoffeeBean>) {
    val soonReady = beans.filter { it.daysUntilReady() in 1..3 }
    val nearEnd = beans.filter { it.daysUntilPeakEnd() in 0..5 }
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("赏味期预告", fontWeight = FontWeight.Bold)
            if (soonReady.isEmpty() && nearEnd.isEmpty()) {
                Text("目前没有临近提醒，豆仓状态很从容。", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            soonReady.take(3).forEach { bean ->
                Text("${bean.displayName()} 还有 ${bean.daysUntilReady()} 天养好", color = MaterialTheme.colorScheme.secondary)
            }
            nearEnd.take(3).forEach { bean ->
                Text("${bean.displayName()} 建议 ${bean.daysUntilPeakEnd()} 天内喝完", color = MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}

@Composable
private fun InsightCharts(beans: List<CoffeeBean>) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("豆仓图表", fontWeight = FontWeight.Bold)
            BeanThumbnailRail(beans)
            MiniDistributionChart(
                title = "产地分布",
                data = beans.groupingBy { it.originCategory() }.eachCount()
            )
            MiniDistributionChart(
                title = "处理法分布",
                data = beans.groupingBy { it.process.ifBlank { "未记录" } }.eachCount()
            )
            MiniDistributionChart(
                title = "单克价格区间",
                data = beans.groupingBy { it.priceBand() }.eachCount()
            )
        }
    }
}

@Composable
private fun BeanThumbnailRail(beans: List<CoffeeBean>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("咖啡缩略图", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(beans.take(10), key = { it.id }) { bean ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp)) {
                    PackageImage(bean.imageUri, Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)))
                    Text(
                        bean.displayName(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniDistributionChart(title: String, data: Map<String, Int>) {
    val max = data.values.maxOrNull()?.coerceAtLeast(1) ?: 1
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        data.entries.sortedByDescending { it.value }.take(5).forEach { (label, count) ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(label, modifier = Modifier.width(78.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(count.toFloat() / max.toFloat())
                            .height(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
                Text(count.toString(), style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeanDetailScreen(
    beanId: Long,
    viewModel: CoffeeViewModel,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val data by viewModel.beanWithRecipes(beanId).collectAsState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(data?.bean?.roaster ?: "咖啡") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) { Icon(Icons.Rounded.Edit, contentDescription = "编辑") }
                    IconButton(onClick = { viewModel.deleteBean(beanId, onBack) }) {
                        Icon(Icons.Rounded.Delete, contentDescription = "删除")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        val bean = data?.bean
        if (bean == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("没有找到这款咖啡")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .premiumBackground()
                    .padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { DetailHero(bean) }
                item {
                    Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Rounded.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("编辑咖啡信息")
                    }
                }
                item { BeanFacts(bean) }
                item { DrinkLogger(bean = bean, onLog = viewModel::logDrink) }
                item {
                    RecipeComposer(beanId = bean.id, onSave = viewModel::saveRecipe)
                }
                items(data?.recipes.orEmpty(), key = { it.id }) { recipe ->
                    RecipeCard(recipe = recipe, onDelete = { viewModel.deleteRecipe(recipe.id) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(viewModel: CoffeeViewModel, onBack: () -> Unit) {
    val beans by viewModel.beans.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val week = remember(beans, logs) { buildReport("本周", beans, logs, LocalDate.now().minusDays(7)) }
    val month = remember(beans, logs) { buildReport("本月", beans, logs, LocalDate.now().minusDays(30)) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("饮用报告") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .premiumBackground()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { ReportCard(week) }
            item { ReportCard(month) }
            item { ForecastStrip(beans) }
            item {
                Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("最近记录", fontWeight = FontWeight.Bold)
                        if (logs.isEmpty()) Text("还没有每日饮用记录。")
                        logs.take(10).forEach { log ->
                            val bean = beans.firstOrNull { it.id == log.beanId }
                            Text("${log.date} · ${bean?.displayName() ?: "咖啡豆"} · ${log.grams.clean()}g")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportCard(stats: ReportStats) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stats.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                MetricBlock("总消耗", "${stats.totalGrams.clean()}g", Modifier.weight(1f))
                MetricBlock("总花费", "¥${stats.totalCost.clean()}", Modifier.weight(1f))
            }
            Text("喝得最多：${stats.topBean}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun MetricBlock(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun DetailHero(bean: CoffeeBean) {
    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            PackageImage(bean.imageUri, Modifier.fillMaxWidth().aspectRatio(1.55f).clip(RoundedCornerShape(20.dp)))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(bean.roaster, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(bean.variety, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                RatingStars(bean.rating)
            }
            if (bean.flavorDescription.isNotBlank()) {
                Text("风味描述：${bean.flavorDescription}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
            Text(bean.flavorNotes.ifBlank { "还没有记录风味笔记。" })
        }
    }
}

@Composable
private fun BeanFacts(bean: CoffeeBean) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            FactRow(Icons.Rounded.CalendarMonth, "烘焙日期", bean.roastDate)
            FactRow(Icons.Rounded.Coffee, "烘焙度", bean.roastLevel.ifBlank { "未设置" })
            FactRow(Icons.Rounded.Coffee, "处理法", bean.process.ifBlank { "未设置" })
            FactRow(Icons.Rounded.Inventory2, "库存状态", bean.inventoryStatus)
            FactRow(Icons.Rounded.Inventory2, "剩余库存", "${bean.remainingGrams.clean()}g / ${bean.packageWeight.clean()}g")
            FactRow(Icons.Rounded.Star, "最佳饮用期", bean.drinkWindow())
            FactRow(Icons.Rounded.LocalCafe, "整包售价", "¥${bean.totalPrice.clean()}")
            FactRow(Icons.Rounded.LocalCafe, "单克价格", "¥${bean.effectivePricePerGram().clean()} / 克")
            val origin = listOf(bean.country, bean.province, bean.village, bean.estate, bean.washingStation, bean.batch)
                .filter { it.isNotBlank() }
                .joinToString(" · ")
            if (origin.isNotBlank()) {
                FactRow(Icons.Rounded.LocalCafe, "产区信息", origin)
            }
        }
    }
}

@Composable
private fun DrinkLogger(bean: CoffeeBean, onLog: (CoffeeBean, Double, String) -> Unit) {
    var grams by remember { mutableStateOf("15") }
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("今天喝了这款", fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CoffeeTextField("用豆克数", grams, KeyboardType.Number) { grams = it }
            }
            Button(
                onClick = { onLog(bean, grams.toDoubleOrNull() ?: 0.0, "每日记录") },
                enabled = (grams.toDoubleOrNull() ?: 0.0) > 0.0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("记录并扣减库存")
            }
        }
    }
}

@Composable
private fun FactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeanEditScreen(
    beanId: Long,
    viewModel: CoffeeViewModel,
    onBack: () -> Unit,
    onSaved: (Long) -> Unit
) {
    val beanData by if (beanId == 0L) {
        remember { mutableStateOf<BeanWithRecipes?>(null) }
    } else {
        remember(beanId) { viewModel.beanWithRecipes(beanId) }.collectAsState()
    }
    var form by remember(beanId) { mutableStateOf(CoffeeBean()) }
    var loadedBeanId by remember(beanId) { mutableStateOf(0L) }
    LaunchedEffect(beanData?.bean) {
        val bean = beanData?.bean
        if (bean != null && loadedBeanId != bean.id) {
            form = bean
            loadedBeanId = bean.id
        }
    }
    val context = LocalContext.current
    val roasters by viewModel.roasterSuggestions.collectAsState()
    val processes by viewModel.processSuggestions.collectAsState()
    val roastLevels by viewModel.roastLevelSuggestions.collectAsState()
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            form = form.copy(imageUri = uri.toString())
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val uri = cameraUri
        if (success && uri != null) {
            form = form.copy(imageUri = uri.toString())
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (beanId == 0L) "添加咖啡豆" else "编辑咖啡豆") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        if (beanId != 0L && beanData == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .premiumBackground()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("正在载入原咖啡档案…")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .premiumBackground()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                PackageImage(
                    uri = form.imageUri,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.7f)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { imagePicker.launch(arrayOf("image/*")) }
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    FilledTonalButton(onClick = { imagePicker.launch(arrayOf("image/*")) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Rounded.Image, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("相册")
                    }
                    FilledTonalButton(
                        onClick = {
                            val uri = createCameraImageUri(context)
                            cameraUri = uri
                            cameraLauncher.launch(uri)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.CameraAlt, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("拍照")
                    }
                }
            }
            item {
                CoffeeTextField("烘焙商", form.roaster) { form = form.copy(roaster = it) }
                SuggestionChips(roasters) { form = form.copy(roaster = it) }
            }
            item { DateWheelField("烘焙日期", form.roastDate) { form = form.copy(roastDate = it) } }
            item { CoffeeTextField("豆种 / 品种", form.variety) { form = form.copy(variety = it) } }
            item {
                CoffeeTextField("处理法", form.process) { form = form.copy(process = it) }
                SuggestionChips(processes) { form = form.copy(process = it) }
            }
            item {
                CoffeeTextField("烘焙度", form.roastLevel) { form = form.copy(roastLevel = it) }
                SuggestionChips(roastLevels) { form = form.copy(roastLevel = it) }
            }
            item { SectionTitle("产区与批次") }
            item { CoffeeTextField("国家", form.country) { form = form.copy(country = it) } }
            item { CoffeeTextField("省份 / 产区", form.province) { form = form.copy(province = it) } }
            item { CoffeeTextField("村庄 / 微产区", form.village) { form = form.copy(village = it) } }
            item { CoffeeTextField("庄园", form.estate) { form = form.copy(estate = it) } }
            item { CoffeeTextField("处理站", form.washingStation) { form = form.copy(washingStation = it) } }
            item { CoffeeTextField("批次", form.batch) { form = form.copy(batch = it) } }
            item { SectionTitle("价格与库存") }
            item {
                CoffeeTextField("包装重量 g", form.packageWeight.toInput(), KeyboardType.Decimal) {
                    val grams = it.toDoubleOrNull() ?: 0.0
                    form = form.copy(
                        packageWeight = grams,
                        remainingGrams = if (form.remainingGrams == 0.0) grams else form.remainingGrams,
                        pricePerGram = calculateUnitPrice(grams, form.totalPrice)
                    )
                }
            }
            item { CoffeeTextField("剩余库存 g", form.remainingGrams.toInput(), KeyboardType.Decimal) { form = form.copy(remainingGrams = it.toDoubleOrNull() ?: 0.0) } }
            item {
                CoffeeTextField("整包售价 ¥", form.totalPrice.toInput(), KeyboardType.Decimal) {
                    val total = it.toDoubleOrNull() ?: 0.0
                    form = form.copy(
                        totalPrice = total,
                        pricePerGram = calculateUnitPrice(form.packageWeight, total)
                    )
                }
            }
            item { UnitPricePreview(form) }
            item { RestingDaysField(form.restingDays) { form = form.copy(restingDays = it) } }
            item { CoffeeTextField("风味描述", form.flavorDescription) { form = form.copy(flavorDescription = it) } }
            item { CoffeeTextField("风味笔记", form.flavorNotes) { form = form.copy(flavorNotes = it) } }
            item { InventoryPicker(form.inventoryStatus) { form = form.copy(inventoryStatus = it) } }
            item { RatingPicker(form.rating) { form = form.copy(rating = it) } }
            item {
                Button(
                    onClick = { viewModel.saveBean(form, onSaved) },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("保存咖啡豆")
                }
            }
        }
    }
}

@Composable
private fun RecipeComposer(beanId: Long, onSave: (BrewingRecipe) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var recipe by remember { mutableStateOf(BrewingRecipe(beanId = beanId)) }
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("冲煮配方", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                FilledTonalButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "收起" else "添加")
                }
            }
            if (expanded) {
                CoffeeTextField("滤杯 / 冲煮器具", recipe.brewer) { recipe = recipe.copy(brewer = it) }
                CoffeeTextField("研磨度", recipe.grindSize) { recipe = recipe.copy(grindSize = it) }
                CoffeeTextField("水温 °C", recipe.waterTemperature.toInput(), KeyboardType.Decimal) { recipe = recipe.copy(waterTemperature = it.toDoubleOrNull() ?: 0.0) }
                CoffeeTextField("粉量 g", recipe.dose.toInput(), KeyboardType.Decimal) { recipe = recipe.copy(dose = it.toDoubleOrNull() ?: 0.0) }
                CoffeeTextField("注水量 g", recipe.waterAmount.toInput(), KeyboardType.Decimal) { recipe = recipe.copy(waterAmount = it.toDoubleOrNull() ?: 0.0) }
                CoffeeTextField("粉水比", recipe.ratio) { recipe = recipe.copy(ratio = it) }
                BrewTimeWheelField("萃取时间", recipe.time) { recipe = recipe.copy(time = it) }
                CoffeeTextField("品饮记录", recipe.tastingNotes) { recipe = recipe.copy(tastingNotes = it) }
                Button(onClick = {
                    onSave(recipe)
                    recipe = BrewingRecipe(beanId = beanId)
                    expanded = false
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("保存配方")
                }
            }
        }
    }
}

@Composable
private fun RecipeCard(recipe: BrewingRecipe, onDelete: () -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(recipe.brewer.ifBlank { "冲煮配方" }, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete) { Icon(Icons.Rounded.Delete, contentDescription = "删除配方") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(recipe.grindSize.ifBlank { "研磨度" }) })
                AssistChip(onClick = {}, label = { Text("${recipe.waterTemperature.clean()}°C") })
                AssistChip(onClick = {}, label = { Text(recipe.ratio) })
            }
            Text("${recipe.dose.clean()}g 咖啡粉 · ${recipe.waterAmount.clean()}g 水 · ${recipe.time}")
            if (recipe.tastingNotes.isNotBlank()) Text(recipe.tastingNotes, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CoffeeTextField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun UnitPricePreview(bean: CoffeeBean) {
    val unit = bean.effectivePricePerGram()
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("自动计算单克价格", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    if (unit > 0.0) "¥${unit.clean()} / 克" else "输入包装重量和售价后自动计算",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Icon(Icons.Rounded.LocalCafe, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun SuggestionChips(values: List<String>, onPick: (String) -> Unit) {
    if (values.isNotEmpty()) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
            items(values.take(8)) { value ->
                AssistChip(onClick = { onPick(value) }, label = { Text(value) })
            }
        }
    }
}

@Composable
private fun DateWheelField(label: String, value: String, onChange: (String) -> Unit) {
    var show by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { show = true },
        trailingIcon = {
            IconButton(onClick = { show = true }) {
                Icon(Icons.Rounded.CalendarMonth, contentDescription = "选择日期")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
    if (show) {
        val parsed = runCatching { LocalDate.parse(value) }.getOrDefault(LocalDate.now())
        DateWheelDialog(
            initial = parsed,
            onDismiss = { show = false },
            onConfirm = {
                onChange(it.format(DateTimeFormatter.ISO_DATE))
                show = false
            }
        )
    }
}

@Composable
private fun NumberWheelField(label: String, value: Int, min: Int, max: Int, onChange: (Int) -> Unit) {
    var show by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = "$value 天",
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { show = true },
        shape = RoundedCornerShape(16.dp)
    )
    if (show) {
        NumberWheelDialog(
            title = label,
            initial = value,
            min = min,
            max = max,
            suffix = "天",
            onDismiss = { show = false },
            onConfirm = {
                onChange(it)
                show = false
            }
        )
    }
}

@Composable
private fun RestingDaysField(value: Int, onChange: (Int) -> Unit) {
    var text by remember(value) { mutableStateOf(value.toString()) }
    var showWheel by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it.filter { char -> char.isDigit() }.take(2)
                onChange(text.toIntOrNull()?.coerceIn(0, 45) ?: 0)
            },
            label = { Text("养豆天数") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(16.dp),
            suffix = { Text("天") }
        )
        FilledTonalButton(onClick = { showWheel = true }) {
            Text("轮盘")
        }
    }
    if (showWheel) {
        NumberWheelDialog(
            title = "养豆天数",
            initial = value,
            min = 0,
            max = 45,
            suffix = "天",
            onDismiss = { showWheel = false },
            onConfirm = {
                text = it.toString()
                onChange(it)
                showWheel = false
            }
        )
    }
}

@Composable
private fun BrewTimeWheelField(label: String, value: String, onChange: (String) -> Unit) {
    var show by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { show = true },
        shape = RoundedCornerShape(16.dp)
    )
    if (show) {
        val parts = value.split(":")
        var minute = parts.getOrNull(0)?.toIntOrNull() ?: 2
        var second = parts.getOrNull(1)?.toIntOrNull() ?: 45
        Dialog(onDismissRequest = { show = false }) {
            Card(shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(label, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                        WheelPicker(0, 15, minute, Modifier.weight(1f)) { minute = it }
                        WheelPicker(0, 59, second, Modifier.weight(1f)) { second = it }
                    }
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { show = false }) { Text("取消") }
                        TextButton(onClick = {
                            onChange("%d:%02d".format(minute, second))
                            show = false
                        }) { Text("确定") }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateWheelDialog(initial: LocalDate, onDismiss: () -> Unit, onConfirm: (LocalDate) -> Unit) {
    var year = initial.year
    var month = initial.monthValue
    var day = initial.dayOfMonth
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("选择日期", fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    WheelPicker(2020, LocalDate.now().year + 1, year, Modifier.weight(1f)) { year = it }
                    WheelPicker(1, 12, month, Modifier.weight(1f)) { month = it }
                    WheelPicker(1, 31, day, Modifier.weight(1f)) { day = it }
                }
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("取消") }
                    TextButton(onClick = {
                        val safeDay = day.coerceAtMost(LocalDate.of(year, month, 1).lengthOfMonth())
                        onConfirm(LocalDate.of(year, month, safeDay))
                    }) { Text("确定") }
                }
            }
        }
    }
}

@Composable
private fun NumberWheelDialog(
    title: String,
    initial: Int,
    min: Int,
    max: Int,
    suffix: String,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selected = initial
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(title, fontWeight = FontWeight.Bold)
                WheelPicker(min, max, initial, Modifier.fillMaxWidth()) { selected = it }
                Text("$selected$suffix", modifier = Modifier.align(Alignment.CenterHorizontally), color = MaterialTheme.colorScheme.primary)
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("取消") }
                    TextButton(onClick = { onConfirm(selected) }) { Text("确定") }
                }
            }
        }
    }
}

@Composable
private fun WheelPicker(min: Int, max: Int, value: Int, modifier: Modifier = Modifier, onChange: (Int) -> Unit) {
    AndroidView(
        modifier = modifier.height(150.dp),
        factory = { context ->
            NumberPicker(context).apply {
                minValue = min
                maxValue = max
                this.value = value.coerceIn(min, max)
                wrapSelectorWheel = true
                setOnValueChangedListener { _, _, newValue -> onChange(newValue) }
            }
        },
        update = { picker ->
            picker.minValue = min
            picker.maxValue = max
            picker.value = value.coerceIn(min, max)
        }
    )
}

@Composable
private fun InventoryPicker(value: String, onChange: (String) -> Unit) {
    val options = listOf("有库存", "养豆中", "快喝完", "已喝完")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        options.forEach { option ->
            AssistChip(onClick = { onChange(option) }, label = { Text(option) })
        }
    }
}

@Composable
private fun RatingPicker(value: Int, onChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("评分", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
        repeat(5) { index ->
            IconButton(onClick = { onChange(index + 1) }) {
                Icon(
                    if (index < value) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    contentDescription = "${index + 1} 星",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
private fun RatingStars(value: Int) {
    Row {
        repeat(5) { index ->
            Icon(
                if (index < value) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
private fun PackageImage(uri: String, modifier: Modifier) {
    if (uri.isBlank()) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Coffee, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(34.dp))
        }
    } else {
        AsyncImage(
            model = uri,
            contentDescription = "咖啡包装照片",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun EmptyState(onAdd: () -> Unit) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Rounded.Coffee, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(44.dp))
            Text("你的豆仓还是空的", fontWeight = FontWeight.Bold)
            Button(onClick = onAdd) { Text("添加第一款咖啡") }
        }
    }
}

@Composable
private fun BottomBar() {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Rounded.Inventory2, contentDescription = null) },
            label = { Text("咖啡豆") }
        )
    }
}

@Composable
private fun Modifier.premiumBackground(): Modifier = background(
    Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            MaterialTheme.colorScheme.background
        )
    )
)

private fun CoffeeBean.drinkWindow(): String {
    val roast = runCatching { LocalDate.parse(roastDate) }.getOrNull() ?: return "需要有效烘焙日期"
    val ready = roast.plusDays(restingDays.toLong())
    val end = ready.plusDays(28)
    val today = LocalDate.now()
    return when {
        today.isBefore(ready) -> "还需 ${ChronoUnit.DAYS.between(today, ready)} 天进入适饮期"
        today.isAfter(end) -> "已过峰值期 · ${end.format(DateTimeFormatter.ISO_DATE)} 后"
        else -> "现在适饮 · 建议 ${end.format(DateTimeFormatter.ISO_DATE)} 前喝完"
    }
}

private fun CoffeeBean.displayName(): String = listOf(roaster, variety.ifBlank { estate }, process)
    .filter { it.isNotBlank() }
    .joinToString(" · ")
    .ifBlank { "未命名咖啡豆" }

private fun CoffeeBean.daysUntilReady(): Long {
    val roast = runCatching { LocalDate.parse(roastDate) }.getOrNull() ?: return Long.MAX_VALUE
    return ChronoUnit.DAYS.between(LocalDate.now(), roast.plusDays(restingDays.toLong()))
}

private fun CoffeeBean.daysUntilPeakEnd(): Long {
    val roast = runCatching { LocalDate.parse(roastDate) }.getOrNull() ?: return Long.MAX_VALUE
    return ChronoUnit.DAYS.between(LocalDate.now(), roast.plusDays(restingDays.toLong()).plusDays(28))
}

private fun CoffeeBean.priceBand(): String = when {
    effectivePricePerGram() <= 0.0 -> "未记录"
    effectivePricePerGram() < 0.5 -> "< ¥0.5/g"
    effectivePricePerGram() < 1.0 -> "¥0.5-1/g"
    effectivePricePerGram() < 2.0 -> "¥1-2/g"
    else -> "¥2+/g"
}

private fun CoffeeBean.effectivePricePerGram(): Double =
    if (packageWeight > 0.0 && totalPrice > 0.0) totalPrice / packageWeight else pricePerGram

private fun calculateUnitPrice(packageWeight: Double, totalPrice: Double): Double =
    if (packageWeight > 0.0 && totalPrice > 0.0) totalPrice / packageWeight else 0.0

private fun CoffeeBean.originCategory(): String {
    val text = listOf(country, province, village, estate, washingStation, batch, flavorNotes)
        .joinToString(" ")
        .lowercase(Locale.ROOT)
    return when {
        text.isBlank() -> "未记录"
        text.containsAny("sidamo", "sidama", "西达摩") -> "埃塞俄比亚 · Sidamo"
        text.containsAny("guji", "古吉") -> "埃塞俄比亚 · Guji"
        text.containsAny("yirgacheffe", "耶加", "耶加雪菲") -> "埃塞俄比亚 · Yirgacheffe"
        text.containsAny("harrar", "哈拉") -> "埃塞俄比亚 · Harrar"
        text.containsAny("ethiopia", "埃塞") -> "埃塞俄比亚"
        text.containsAny("panama", "巴拿马") -> "巴拿马"
        text.containsAny("colombia", "哥伦比亚") -> "哥伦比亚"
        text.containsAny("kenya", "肯尼亚") -> "肯尼亚"
        text.containsAny("costa rica", "哥斯达黎加") -> "哥斯达黎加"
        text.containsAny("guatemala", "危地马拉") -> "危地马拉"
        text.containsAny("brazil", "巴西") -> "巴西"
        text.containsAny("indonesia", "印尼", "苏门答腊") -> "印度尼西亚"
        country.isNotBlank() && province.isNotBlank() -> "${country} · ${province}"
        country.isNotBlank() -> country
        province.isNotBlank() -> province
        else -> "其他产区"
    }
}

private fun String.containsAny(vararg tokens: String): Boolean =
    tokens.any { contains(it.lowercase(Locale.ROOT)) }

private fun createCameraImageUri(context: Context): Uri {
    val dir = File(context.cacheDir, "camera").apply { mkdirs() }
    val file = File.createTempFile("coffee-package-", ".jpg", dir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

private fun buildReport(title: String, beans: List<CoffeeBean>, logs: List<ConsumptionLog>, start: LocalDate): ReportStats {
    val recent = logs.filter { log ->
        val date = runCatching { LocalDate.parse(log.date) }.getOrNull()
        date != null && !date.isBefore(start)
    }
    val beanById = beans.associateBy { it.id }
    val totalGrams = recent.sumOf { it.grams }
    val totalCost = recent.sumOf { log -> log.grams * (beanById[log.beanId]?.effectivePricePerGram() ?: 0.0) }
    val topId = recent.groupBy { it.beanId }.maxByOrNull { entry -> entry.value.sumOf { it.grams } }?.key
    return ReportStats(
        title = title,
        totalGrams = totalGrams,
        totalCost = totalCost,
        topBean = beanById[topId]?.displayName() ?: "暂无记录"
    )
}

private fun Double.clean(): String = if (this % 1.0 == 0.0) this.toInt().toString() else String.format(Locale.US, "%.2f", this)
private fun Double.toInput(): String = if (this == 0.0) "" else clean()

private fun buildCsv(beans: List<CoffeeBean>, recipes: List<BrewingRecipe>, logs: List<ConsumptionLog>): String {
    val rows = mutableListOf(
        listOf(
            "类型", "咖啡豆ID", "烘焙商", "烘焙日期", "豆种/品种", "处理法", "烘焙度",
            "国家", "省份/产区", "村庄", "庄园", "处理站", "批次", "包装重量", "剩余库存",
            "整包售价", "单克价格", "养豆天数", "风味描述", "风味笔记", "评分", "库存状态", "滤杯/冲煮器具", "研磨度",
            "水温", "粉量", "注水量", "粉水比", "萃取时间", "品饮记录", "日期", "消耗克数"
        )
    )
    beans.forEach { bean ->
        rows += listOf(
            "咖啡豆", bean.id.toString(), bean.roaster, bean.roastDate, bean.variety, bean.process,
            bean.roastLevel, bean.country, bean.province, bean.village, bean.estate, bean.washingStation,
            bean.batch, bean.packageWeight.toString(), bean.remainingGrams.toString(), bean.totalPrice.toString(),
            bean.effectivePricePerGram().toString(), bean.restingDays.toString(), bean.flavorDescription, bean.flavorNotes, bean.rating.toString(), bean.inventoryStatus,
            "", "", "", "", "", "", "", "", ""
        )
    }
    recipes.forEach { recipe ->
        rows += listOf(
            "配方", recipe.beanId.toString(), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", recipe.brewer,
            recipe.grindSize, recipe.waterTemperature.toString(), recipe.dose.toString(),
            recipe.waterAmount.toString(), recipe.ratio, recipe.time, recipe.tastingNotes, "", ""
        )
    }
    logs.forEach { log ->
        rows += listOf(
            "每日记录", log.beanId.toString(), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", log.note, log.date, log.grams.toString()
        )
    }
    return rows.joinToString("\n") { row -> row.joinToString(",") { csvEscape(it) } }
}

private fun csvEscape(value: String): String {
    val escaped = value.replace("\"", "\"\"")
    return if (escaped.any { it == ',' || it == '"' || it == '\n' }) "\"$escaped\"" else escaped
}

