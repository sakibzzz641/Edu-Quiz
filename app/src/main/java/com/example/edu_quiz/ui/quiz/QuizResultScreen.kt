package com.example.edu_quiz.ui.quiz

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edu_quiz.theme.AccentPurple
import com.example.edu_quiz.theme.ColorCorrect
import com.example.edu_quiz.theme.ColorIncorrect
import com.example.edu_quiz.theme.NotoSansBengaliFontFamily
import com.example.edu_quiz.theme.OutfitFontFamily
import com.example.edu_quiz.theme.TextLight
import com.example.edu_quiz.theme.TextMuted
import com.example.edu_quiz.ui.components.AmbientMeshBackground
import com.example.edu_quiz.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(
  score: Int,
  totalQuestions: Int,
  categoryNames: String,
  onBackClick: () -> Unit,
  onPracticeMistakesClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val percent = if (totalQuestions > 0) (score * 100) / totalQuestions else 0
  val accuracyColor = if (percent >= 60) ColorCorrect else ColorIncorrect

  val (headline, subhead) = when {
    percent >= 80 -> Pair("Congratulations! / অভিনন্দন!", "অসাধারণ স্কোর! আপনি কুইজটি সফলভাবে আয়ত্ত করেছেন।")
    percent >= 50 -> Pair("Good effort! / চমৎকার চেষ্টা!", "ভালো হয়েছে! আরেকটু প্র্যাকটিস করলে আরও ভালো করবেন।")
    else -> Pair("Keep practicing! / অনুশীলন চালিয়ে যান!", "হতাশ হবেন না! ভুলগুলো চিহ্নিত করে আবার চেষ্টা করুন।")
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Quiz Results / কুইজ ফলাফল",
            fontFamily = NotoSansBengaliFontFamily,
            fontSize = 18.sp,
            color = TextLight
          )
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
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        // Large animated accuracy ring
        Box(contentAlignment = Alignment.Center) {
          Canvas(modifier = Modifier.size(160.dp)) {
            // Background trace
            drawCircle(
              color = Color(0x1AFFFFFF),
              style = Stroke(width = 12.dp.toPx())
            )
            // Percentage sweep
            drawArc(
              color = accuracyColor,
              startAngle = -90f,
              sweepAngle = 360f * (percent / 100f),
              useCenter = false,
              style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "$percent%",
              fontFamily = OutfitFontFamily,
              fontSize = 36.sp,
              color = accuracyColor
            )
            Text(
              text = "Accuracy",
              fontFamily = OutfitFontFamily,
              fontSize = 12.sp,
              color = TextMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Headline & subhead card
        GlassCard(
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = headline,
              fontFamily = NotoSansBengaliFontFamily,
              fontSize = 18.sp,
              color = TextLight
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = subhead,
              fontFamily = NotoSansBengaliFontFamily,
              fontSize = 14.sp,
              lineHeight = 20.sp,
              color = TextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
              text = "Final Score: $score / $totalQuestions",
              fontFamily = OutfitFontFamily,
              fontSize = 16.sp,
              color = accuracyColor
            )
          }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Action Buttons
        Button(
          onClick = onBackClick,
          colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Back to Menu / প্রধান মেনু",
            fontFamily = NotoSansBengaliFontFamily,
            fontSize = 15.sp,
            color = Color.White
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
          onClick = onPracticeMistakesClick,
          colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentPurple),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Practice Mistakes / ভুলগুলো প্র্যাকটিস করুন",
            fontFamily = NotoSansBengaliFontFamily,
            fontSize = 14.sp
          )
        }
      }
    }
  }
}
