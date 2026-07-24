package com.example.edu_quiz.ui.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edu_quiz.data.DataRepository
import com.example.edu_quiz.theme.ColorCorrect
import com.example.edu_quiz.theme.ColorIncorrect
import com.example.edu_quiz.theme.NotoSansBengaliFontFamily
import com.example.edu_quiz.theme.OutfitFontFamily
import com.example.edu_quiz.theme.TextLight
import com.example.edu_quiz.theme.TextMuted
import com.example.edu_quiz.ui.components.AmbientMeshBackground
import com.example.edu_quiz.ui.components.GlassCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
  onBackClick: () -> Unit,
  repository: DataRepository,
  modifier: Modifier = Modifier
) {
  val attempts by repository.attempts.collectAsState(initial = emptyList())
  val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Quiz History / কুইজ ইতিহাস",
            fontFamily = NotoSansBengaliFontFamily,
            fontSize = 20.sp,
            color = TextLight
          )
        },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextLight)
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
      if (attempts.isEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "No history recorded yet!",
            fontFamily = OutfitFontFamily,
            fontSize = 18.sp,
            color = TextMuted
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "কুইজে অংশগ্রহণ করার পর আপনার সমস্ত স্কোর এখানে দেখা যাবে।",
            fontFamily = NotoSansBengaliFontFamily,
            fontSize = 14.sp,
            color = TextMuted
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(attempts) { attempt ->
            val dateStr = dateFormat.format(Date(attempt.date))
            val percent = if (attempt.totalQuestions > 0) {
              (attempt.score * 100) / attempt.totalQuestions
            } else 0

            val accuracyColor = if (percent >= 60) ColorCorrect else ColorIncorrect

            GlassCard(
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Score: ${attempt.score}/${attempt.totalQuestions}",
                    fontFamily = OutfitFontFamily,
                    fontSize = 18.sp,
                    color = accuracyColor
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = dateStr,
                    fontFamily = OutfitFontFamily,
                    fontSize = 12.sp,
                    color = TextMuted
                  )
                }

                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = "$percent%",
                    fontFamily = OutfitFontFamily,
                    fontSize = 24.sp,
                    color = accuracyColor
                  )
                  Text(
                    text = "Accuracy",
                    fontFamily = OutfitFontFamily,
                    fontSize = 10.sp,
                    color = TextMuted
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
