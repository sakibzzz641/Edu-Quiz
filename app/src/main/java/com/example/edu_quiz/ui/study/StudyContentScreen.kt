package com.example.edu_quiz.ui.study

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import androidx.compose.material3.MaterialTheme
import com.example.edu_quiz.data.DataRepository
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import kotlinx.coroutines.launch

@Composable
fun StudyContentScreen(categoryId: Long, categoryName: String, repository: DataRepository, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val contentState = remember { mutableStateOf<String?>(null) }
    val loading = remember { mutableStateOf(true) }

    LaunchedEffect(categoryId) {
        scope.launch {
            val entity = repository.getStudyContent(categoryId)
            contentState.value = entity?.markdownContent
            loading.value = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (loading.value) {
            CircularProgressIndicator()
        } else {
            val content = contentState.value ?: ""
            // Render markdown using the markdown renderer library
            Markdown(
    content = content,
    colors = markdownColor(text = MaterialTheme.colorScheme.onSurface),
    typography = markdownTypography()
)
        }
    }
}
