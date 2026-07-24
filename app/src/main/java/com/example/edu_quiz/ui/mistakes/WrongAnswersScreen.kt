package com.example.edu_quiz.ui.mistakes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edu_quiz.data.DataRepository
import com.example.edu_quiz.data.local.QuestionEntity
import com.example.edu_quiz.theme.ColorCorrect
import com.example.edu_quiz.theme.ColorIncorrect
import com.example.edu_quiz.theme.NotoSansBengaliFontFamily
import com.example.edu_quiz.theme.OutfitFontFamily
import com.example.edu_quiz.theme.TextLight
import com.example.edu_quiz.theme.TextMuted
import com.example.edu_quiz.ui.components.AmbientMeshBackground
import com.example.edu_quiz.ui.components.GlassCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WrongAnswersScreen(
  onBackClick: () -> Unit,
  onStartPracticeClick: () -> Unit,
  repository: DataRepository,
  modifier: Modifier = Modifier
) {
  val coroutineScope = rememberCoroutineScope()
  val wrongQuestions = remember { mutableStateListOf<QuestionEntity>() }

  fun loadWrongQuestions() {
    coroutineScope.launch {
      wrongQuestions.clear()
      wrongQuestions.addAll(repository.getWrongAnswersList())
    }
  }

  LaunchedEffect(Unit) {
    loadWrongQuestions()
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Practice Mistakes / ভুল সংশোধন",
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
      if (wrongQuestions.isEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "No mistakes registered!",
            fontFamily = OutfitFontFamily,
            fontSize = 18.sp,
            color = TextMuted
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "খুব সুন্দর! আপনার কোনো ভুল উত্তর জমা নেই।",
            fontFamily = NotoSansBengaliFontFamily,
            fontSize = 14.sp,
            color = TextMuted
          )
        }
      } else {
        Column(modifier = Modifier.fillMaxSize()) {
          // Practice action bar
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Total: ${wrongQuestions.size} Mistakes",
              fontFamily = OutfitFontFamily,
              fontSize = 15.sp,
              color = TextLight
            )
            
            Button(
              onClick = onStartPracticeClick,
              colors = ButtonDefaults.buttonColors(containerColor = ColorIncorrect),
              elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = "Practice")
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Practice / অনুশীলন করুন",
                fontFamily = NotoSansBengaliFontFamily,
                fontSize = 14.sp,
                color = Color.White
              )
            }
          }

          LazyColumn(
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            items(wrongQuestions) { q ->
              val correctAns = when (q.correctIndex) {
                0 -> q.option1
                1 -> q.option2
                2 -> q.option3
                else -> q.option4
              }

              GlassCard(
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                  ) {
                    Text(
                      text = q.questionText,
                      fontFamily = NotoSansBengaliFontFamily,
                      fontSize = 15.sp,
                      lineHeight = 22.sp, // Line height for Bangla
                      color = TextLight,
                      modifier = Modifier.weight(1f)
                    )

                    IconButton(
                      onClick = {
                        coroutineScope.launch {
                          repository.removeWrongAnswer(q.id)
                          loadWrongQuestions()
                        }
                      }
                    ) {
                      Icon(Icons.Default.Delete, contentDescription = "Remove", tint = ColorIncorrect)
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  Card(
                    colors = CardDefaults.cardColors(containerColor = ColorCorrect.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                      Text(
                        text = "Correct Answer / সঠিক উত্তর:",
                        fontFamily = NotoSansBengaliFontFamily,
                        fontSize = 12.sp,
                        color = ColorCorrect
                      )
                      Text(
                        text = correctAns,
                        fontFamily = NotoSansBengaliFontFamily,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = TextLight
                      )
                    }
                  }

                  if (q.explanation.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                      text = "Explanation: ${q.explanation}",
                      fontFamily = NotoSansBengaliFontFamily,
                      fontSize = 13.sp,
                      lineHeight = 18.sp,
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
}
