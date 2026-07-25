package com.example.edu_quiz.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.edu_quiz.data.DataRepository
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.example.edu_quiz.Leaderboard
import com.example.edu_quiz.PracticeMistakes
import com.example.edu_quiz.QuizPlay
import com.example.edu_quiz.Settings

import com.example.edu_quiz.data.local.CategoryEntity
import com.example.edu_quiz.theme.AccentPink
import com.example.edu_quiz.theme.AccentPurple
import com.example.edu_quiz.theme.ColorCorrect
import com.example.edu_quiz.theme.ColorIncorrect
import com.example.edu_quiz.theme.NotoSansBengaliFontFamily
import com.example.edu_quiz.theme.OutfitFontFamily
import com.example.edu_quiz.theme.TextLight
import com.example.edu_quiz.theme.TextMuted
import com.example.edu_quiz.ui.components.AmbientMeshBackground
import com.example.edu_quiz.ui.components.GlassCard
import com.example.edu_quiz.ui.components.springClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
  onItemClick: (NavKey) -> Unit,
  repository: DataRepository,
  modifier: Modifier = Modifier,
  viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(repository) }
) {
  val categories by viewModel.categories.collectAsState()
  val attempts by viewModel.attempts.collectAsState()
  val wrongAnswersCount by viewModel.wrongAnswersCount.collectAsState()
  val syncState by viewModel.syncState.collectAsState()
  val selectedIds = viewModel.selectedCategoryIds

  // Map to store expanded states of category IDs
  val expandedStates = remember { mutableStateMapOf<Long, Boolean>() }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Edu-Quiz",
              fontFamily = OutfitFontFamily,
              fontSize = 22.sp,
              color = TextLight
            )
            Text(
              text = "এডু-কুইজ — জ্ঞান অর্জনের সহজ মাধ্যম",
              fontFamily = NotoSansBengaliFontFamily,
              fontSize = 11.sp,
              color = TextMuted
            )
          }
        },
        actions = {
          IconButton(onClick = { viewModel.syncData() }) {
            Icon(Icons.Default.Refresh, contentDescription = "Sync", tint = TextLight)
          }
          IconButton(onClick = { onItemClick(Settings) }) {
            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextLight)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Color.Transparent,
          titleContentColor = TextLight
        )
      )
    },
    containerColor = Color.Transparent,
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    AmbientMeshBackground(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 20.dp)
      ) {
        // Sync Status banner if sync is loading/active
        AnimatedVisibility(visible = syncState != SyncUiState.Idle) {
          GlassCard(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              when (val state = syncState) {
                is SyncUiState.Loading -> {
                  CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = AccentPurple)
                  Text(
                    text = state.message,
                    fontFamily = NotoSansBengaliFontFamily,
                    fontSize = 14.sp,
                    color = TextLight
                  )
                }
                SyncUiState.Success -> {
                  Icon(Icons.Default.Check, contentDescription = "Done", tint = ColorCorrect)
                  Text(
                    text = "Sync Complete! / ডেটা আপডেট হয়েছে!",
                    fontFamily = NotoSansBengaliFontFamily,
                    fontSize = 14.sp,
                    color = ColorCorrect
                  )
                  // Dismiss automatically after delay
                  viewModel.resetSyncState()
                }
                is SyncUiState.Error -> {
                  Icon(Icons.Default.Warning, contentDescription = "Error", tint = ColorIncorrect)
                  Text(
                    text = "Sync Failed: ${state.message}",
                    fontFamily = NotoSansBengaliFontFamily,
                    fontSize = 13.sp,
                    color = ColorIncorrect,
                    modifier = Modifier.weight(1f)
                  )
                  IconButton(onClick = { viewModel.resetSyncState() }) {
                    Icon(Icons.Default.Check, contentDescription = "Dismiss", tint = TextMuted)
                  }
                }
                else -> {}
              }
            }
          }
        }

        // Stats Dashboard Quick Links Card
        GlassCard(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // History Quick Link
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .springClickable { onItemClick(Leaderboard) }
                .padding(8.dp)
            ) {
              Icon(
                Icons.Default.History,
                contentDescription = "History",
                tint = AccentPurple,
                modifier = Modifier.size(28.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "History / ইতিহাস",
                fontFamily = NotoSansBengaliFontFamily,
                fontSize = 12.sp,
                color = TextLight
              )
              Text(
                text = "${attempts.size} Played",
                fontFamily = OutfitFontFamily,
                fontSize = 11.sp,
                color = TextMuted
              )
            }

            // Practice Mistakes Quick Link
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .springClickable { onItemClick(PracticeMistakes) }
                .padding(8.dp)
            ) {
              Icon(
                Icons.Default.Warning,
                contentDescription = "Practice Mistakes",
                tint = if (wrongAnswersCount > 0) ColorIncorrect else TextMuted,
                modifier = Modifier.size(28.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Mistakes / ভুল সংশোধন",
                fontFamily = NotoSansBengaliFontFamily,
                fontSize = 12.sp,
                color = TextLight
              )
              Text(
                text = "$wrongAnswersCount Errors",
                fontFamily = OutfitFontFamily,
                fontSize = 11.sp,
                color = if (wrongAnswersCount > 0) ColorIncorrect else TextMuted
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Select Categories / বিষয় নির্বাচন করুন:",
          fontFamily = NotoSansBengaliFontFamily,
          fontSize = 16.sp,
          color = TextLight,
          modifier = Modifier.padding(bottom = 8.dp)
        )

        if (categories.isEmpty()) {
          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "No categories loaded. Please sync data.",
                fontFamily = OutfitFontFamily,
                color = TextMuted
              )
              Spacer(modifier = Modifier.height(12.dp))
              Button(
                onClick = { viewModel.syncData() },
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
              ) {
                Text(
                  text = "Sync Now / আপডেট করুন",
                  fontFamily = NotoSansBengaliFontFamily,
                  fontSize = 14.sp
                )
              }
            }
          }
        } else {
          // Render Category Tree as expandable lists
          val flatTree = buildFlatTree(categories, expandedStates)

          Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              items(flatTree) { node ->
                CategoryRow(
                  node = node,
                  isSelected = selectedIds.contains(node.category.id),
                  onCheckedChange = {
                    toggleCategoryAndDescendants(node.category.id, categories, viewModel)
                  },
                  isExpanded = expandedStates[node.category.id] ?: false,
                  onToggleExpand = {
                    expandedStates[node.category.id] = !(expandedStates[node.category.id] ?: false)
                  },
                  repository = repository
                )
              }
              item {
                Spacer(modifier = Modifier.height(80.dp)) // Avoid truncation under play button
              }
            }

            // Start Quiz Floating Button
            androidx.compose.animation.AnimatedVisibility(
              visible = selectedIds.isNotEmpty(),
              modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
            ) {
              Button(
                onClick = {
                  onItemClick(QuizPlay(categoryIds = selectedIds.toList(), isPracticeMistakes = false, sessionId = System.currentTimeMillis()))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .background(
                    brush = Brush.linearGradient(listOf(AccentPurple, AccentPink)),
                    shape = RoundedCornerShape(24.dp)
                  )
              ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Start Quiz (${selectedIds.size} Cat) / কুইজ শুরু করুন",
                  fontFamily = NotoSansBengaliFontFamily,
                  fontSize = 15.sp,
                  color = Color.White
                )
              }
            }
          }
        }
      }
    }
  }
}

// Tree builder algorithms
data class TreeRowNode(
  val category: CategoryEntity,
  val level: Int,
  val hasChildren: Boolean
)

private fun buildFlatTree(
  categories: List<CategoryEntity>,
  expandedStates: Map<Long, Boolean>
): List<TreeRowNode> {
  val result = mutableListOf<TreeRowNode>()
  
  fun recurse(parentId: Long?, level: Int) {
    val levelChildren = categories.filter { it.parentId == parentId }
    for (child in levelChildren) {
      val hasChildren = categories.any { it.parentId == child.id }
      result.add(TreeRowNode(child, level, hasChildren))
      
      val isExpanded = expandedStates[child.id] ?: false
      if (isExpanded && hasChildren) {
        recurse(child.id, level + 1)
      }
    }
  }

  recurse(null, 0)
  return result
}

private fun toggleCategoryAndDescendants(
  categoryId: Long,
  categories: List<CategoryEntity>,
  viewModel: MainScreenViewModel
) {
  val descendantIds = mutableListOf<Long>()
  fun collect(id: Long) {
    descendantIds.add(id)
    categories.filter { it.parentId == id }.forEach { collect(it.id) }
  }
  collect(categoryId)

  val anySelected = descendantIds.any { viewModel.selectedCategoryIds.contains(it) }
  if (anySelected) {
    // Deselect all
    viewModel.selectedCategoryIds.removeAll(descendantIds)
  } else {
    // Select all
    descendantIds.forEach { id ->
      if (!viewModel.selectedCategoryIds.contains(id)) {
        viewModel.selectedCategoryIds.add(id)
      }
    }
  }
}

@Composable
fun CategoryRow(
  node: TreeRowNode,
  isSelected: Boolean,
  onCheckedChange: () -> Unit,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  repository: com.example.edu_quiz.data.DataRepository
) {
  val paddingStart = (node.level * 20).dp
  val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "arrow_rot")

  GlassCard(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = paddingStart)
      .animateContentSize()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        // Expand/Collapse arrow
        if (node.hasChildren) {
          IconButton(onClick = onToggleExpand) {
            Icon(
              Icons.Default.KeyboardArrowDown,
              contentDescription = "Expand",
              tint = TextLight,
              modifier = Modifier.rotate(rotation)
            )
          }
        } else {
          Spacer(modifier = Modifier.width(48.dp))
        }

        Text(
          text = node.category.name,
          fontFamily = NotoSansBengaliFontFamily,
          fontSize = 15.sp,
          color = TextLight,
          modifier = Modifier.clickable { onCheckedChange() }
        )
      }

      // Study Content Button (book icon) conditional
      val hasStudyContent = remember(node.category.id) { mutableStateOf(false) }
      LaunchedEffect(node.category.id) {
        val content = repository.getStudyContent(node.category.id)
        hasStudyContent.value = content != null
      }
      if (hasStudyContent.value) {
        IconButton(onClick = { /* TODO: navigate to StudyContentScreen */ }) {
          Icon(Icons.Default.MenuBook, contentDescription = "Read", tint = AccentPurple)
        }
      }

      // Checkbox Indicator
      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(if (isSelected) AccentPurple else Color(0x33FFFFFF))
          .clickable { onCheckedChange() },
        contentAlignment = Alignment.Center
      ) {
        if (isSelected) {
          Icon(
            Icons.Default.Check,
            contentDescription = "Selected",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
