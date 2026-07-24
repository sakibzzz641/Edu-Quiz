package com.example.edu_quiz.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edu_quiz.data.DataRepository
import com.example.edu_quiz.theme.AccentPurple
import com.example.edu_quiz.theme.ColorCorrect
import com.example.edu_quiz.theme.ColorIncorrect
import com.example.edu_quiz.theme.ColorWarning
import com.example.edu_quiz.theme.NotoSansBengaliFontFamily
import com.example.edu_quiz.theme.OutfitFontFamily
import com.example.edu_quiz.theme.TextLight
import com.example.edu_quiz.theme.TextMuted
import com.example.edu_quiz.ui.components.AmbientMeshBackground
import com.example.edu_quiz.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
  categoryIds: List<Long>,
  isPracticeMistakes: Boolean,
  sessionId: Long = 0L,
  onCloseClick: () -> Unit,
  onQuizComplete: (score: Int, total: Int) -> Unit,
  repository: DataRepository,
  modifier: Modifier = Modifier,
  viewModel: QuizViewModel = viewModel(
    key = "QuizViewModel_${sessionId}_${categoryIds.joinToString("_")}_$isPracticeMistakes"
  ) {
    QuizViewModel(repository, categoryIds, isPracticeMistakes, sessionId)
  }
) {
  val uiState by viewModel.uiState.collectAsState()
  val currentIndex by viewModel.currentQuestionIndex.collectAsState()
  val selectedAnswer by viewModel.selectedAnswerIndex.collectAsState()
  val timerRatio by viewModel.timerRatio.collectAsState()
  val score by viewModel.score.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (isPracticeMistakes) "Mistakes Practice / ভুল সংশোধন" else "Edu-Quiz Play",
            fontFamily = NotoSansBengaliFontFamily,
            fontSize = 18.sp,
            color = TextLight
          )
        },
        actions = {
          IconButton(onClick = onCloseClick) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextLight)
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
      when (uiState) {
        QuizUiState.Loading -> {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading questions...", fontFamily = OutfitFontFamily, color = TextMuted)
          }
        }
        QuizUiState.Empty -> {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "No questions found!",
              fontFamily = OutfitFontFamily,
              fontSize = 18.sp,
              color = TextMuted
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = onCloseClick,
              colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
            ) {
              Text("Back", fontFamily = OutfitFontFamily)
            }
          }
        }
        is QuizUiState.Success -> {
          val questions = (uiState as QuizUiState.Success).questions
          val currentQuestion = questions.getOrNull(currentIndex)

          if (currentQuestion != null) {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              // Quiz progress and Timer row
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Question: ${currentIndex + 1}/${questions.size}",
                  fontFamily = OutfitFontFamily,
                  fontSize = 15.sp,
                  color = TextMuted
                )

                // Smooth animated timer ring
                val timerColor = if (timerRatio > 0.5f) {
                  val fraction = (timerRatio - 0.5f) * 2f
                  lerp(ColorWarning, AccentPurple, fraction)
                } else {
                  val fraction = timerRatio * 2f
                  lerp(ColorIncorrect, ColorWarning, fraction)
                }

                Box(contentAlignment = Alignment.Center) {
                  Canvas(modifier = Modifier.size(54.dp)) {
                    // Background ring outline
                    drawCircle(
                      color = Color(0x1AFFFFFF),
                      style = Stroke(width = 4.dp.toPx())
                    )
                    // Draining active timer ring arc
                    drawArc(
                      color = timerColor,
                      startAngle = -90f,
                      sweepAngle = 360f * timerRatio,
                      useCenter = false,
                      style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                  }
                  Text(
                    text = "${(timerRatio * 10).toInt() + 1}",
                    fontFamily = OutfitFontFamily,
                    fontSize = 14.sp,
                    color = timerColor
                  )
                }
              }

              Spacer(modifier = Modifier.height(20.dp))

              // Question GlassCard
              GlassCard(
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = currentQuestion.question.questionText,
                  fontFamily = NotoSansBengaliFontFamily,
                  fontSize = 18.sp,
                  lineHeight = 26.sp, // Taller line spacing for Bangla script
                  color = TextLight,
                  modifier = Modifier.padding(20.dp)
                )
              }

              Spacer(modifier = Modifier.height(24.dp))

              // Option cards
              Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                currentQuestion.shuffledOptions.forEachIndexed { index, optionText ->
                  val isCorrect = index == currentQuestion.shuffledCorrectIndex
                  val isSelected = selectedAnswer == index

                  // Determine border color and overlay color based on states
                  val (borderColor, overlayColor) = when {
                    selectedAnswer == null -> Pair(Color.Transparent, Color.Transparent)
                    isCorrect -> Pair(ColorCorrect, ColorCorrect.copy(alpha = 0.15f))
                    isSelected -> Pair(ColorIncorrect, ColorIncorrect.copy(alpha = 0.15f))
                    else -> Pair(Color.Transparent, Color.Transparent)
                  }

                  val isClickable = selectedAnswer == null

                  GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    onClick = if (isClickable) { { viewModel.selectOption(index) } } else null
                  ) {
                    Box(
                      modifier = Modifier
                        .fillMaxWidth()
                        .cardHighlight(borderColor = borderColor, overlayColor = overlayColor)
                    ) {
                      Text(
                        text = optionText,
                        fontFamily = NotoSansBengaliFontFamily,
                        fontSize = 15.sp,
                        lineHeight = 22.sp, // Line height for options
                        color = TextLight,
                        modifier = Modifier.padding(16.dp)
                      )
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(24.dp))

              // Explanation & Next Action (Reveals when answered or timeout occurs)
              AnimatedVisibility(
                visible = selectedAnswer != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
              ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                  Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x14FFFFFF)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                      val resultMsg = when {
                        selectedAnswer == currentQuestion.shuffledCorrectIndex -> "Correct! / সঠিক উত্তর হয়েছে! 🎉"
                        selectedAnswer == -1 -> "Time Out! / সময় শেষ হয়ে গেছে! ⏰"
                        else -> "Incorrect / ভুল উত্তর হয়েছে! ❌"
                      }

                      val msgColor = when {
                        selectedAnswer == currentQuestion.shuffledCorrectIndex -> ColorCorrect
                        selectedAnswer == -1 -> ColorWarning
                        else -> ColorIncorrect
                      }

                      Text(
                        text = resultMsg,
                        fontFamily = NotoSansBengaliFontFamily,
                        fontSize = 15.sp,
                        color = msgColor
                      )

                      if (currentQuestion.question.explanation.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                          text = "ব্যাখ্যা: ${currentQuestion.question.explanation}",
                          fontFamily = NotoSansBengaliFontFamily,
                          fontSize = 14.sp,
                          lineHeight = 20.sp, // Explanation line height
                          color = TextMuted
                        )
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(16.dp))

                  Button(
                    onClick = {
                      viewModel.nextQuestion { finalScore, total ->
                        onQuizComplete(finalScore, total)
                      }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text(
                      text = if (currentIndex + 1 < questions.size) "Next Question / পরবর্তী প্রশ্ন" else "Finish / শেষ করুন",
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
  }
}

// Ext helper modifier to draw states on top of the GlassCard
private fun Modifier.cardHighlight(borderColor: Color, overlayColor: Color): Modifier {
  return this.drawBehind {
    if (overlayColor != Color.Transparent) {
      drawRect(color = overlayColor, size = size)
    }
    if (borderColor != Color.Transparent) {
      drawRect(
        color = borderColor,
        size = size,
        style = Stroke(width = 2.dp.toPx())
      )
    }
  }
}
