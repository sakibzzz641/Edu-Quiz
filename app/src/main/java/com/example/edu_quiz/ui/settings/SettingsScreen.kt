package com.example.edu_quiz.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edu_quiz.data.DataRepository
import com.example.edu_quiz.theme.AccentPurple
import com.example.edu_quiz.theme.NotoSansBengaliFontFamily
import com.example.edu_quiz.theme.OutfitFontFamily
import com.example.edu_quiz.theme.TextLight
import com.example.edu_quiz.theme.TextMuted
import com.example.edu_quiz.ui.components.AmbientMeshBackground
import com.example.edu_quiz.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  onBackClick: () -> Unit,
  repository: DataRepository,
  modifier: Modifier = Modifier,
  viewModel: SettingsViewModel = viewModel { SettingsViewModel(repository) }
) {
  val currentUrl by viewModel.baseUrl.collectAsState()
  var urlInput by remember { mutableStateOf("") }

  LaunchedEffect(currentUrl) {
    urlInput = currentUrl
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Settings / সেটিংস",
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
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp)
      ) {
        Text(
          text = "Configure Sync Server Base URL",
          fontFamily = OutfitFontFamily,
          fontSize = 18.sp,
          color = TextLight
        )
        Text(
          text = "নিচে ডেটা সিঙ্ক করার জন্য আপনার গিটহাব বা সার্ভারের লিংকটি পরিবর্তন করতে পারেন। ফোল্ডারের শেষে '/' দিতে ভুলবেন না।",
          fontFamily = NotoSansBengaliFontFamily,
          fontSize = 13.sp,
          lineHeight = 20.sp,
          color = TextMuted,
          modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        GlassCard(
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp)
          ) {
            OutlinedTextField(
              value = urlInput,
              onValueChange = { urlInput = it },
              label = {
                Text(
                  "JSON Base URL",
                  fontFamily = OutfitFontFamily,
                  fontSize = 14.sp
                )
              },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentPurple,
                unfocusedBorderColor = TextMuted,
                focusedTextColor = TextLight,
                unfocusedTextColor = TextLight
              ),
              modifier = Modifier.fillMaxWidth(),
              textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = OutfitFontFamily)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
              onClick = {
                viewModel.saveBaseUrl(urlInput)
                onBackClick()
              },
              colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "Save URL / সংরক্ষণ করুন",
                fontFamily = NotoSansBengaliFontFamily,
                fontSize = 15.sp,
                color = Color.White
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
              onClick = {
                urlInput = "https://raw.githubusercontent.com/sakibzzz641/Edu-Quiz/refs/heads/main/asset/"
              },
              colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentPurple),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "Reset to Default / ডিফল্ট করুন",
                fontFamily = NotoSansBengaliFontFamily,
                fontSize = 14.sp
              )
            }
          }
        }
      }
    }
  }
}
