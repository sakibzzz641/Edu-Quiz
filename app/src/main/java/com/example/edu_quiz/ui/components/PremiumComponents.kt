package com.example.edu_quiz.ui.components

import android.content.Context
import android.provider.Settings
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.edu_quiz.theme.DarkBg
import com.example.edu_quiz.theme.GlassBg
import com.example.edu_quiz.theme.GlassBorderEnd
import com.example.edu_quiz.theme.GlassBorderStart

fun isReducedMotionEnabled(context: Context): Boolean {
  return try {
    Settings.Global.getFloat(
      context.contentResolver,
      Settings.Global.ANIMATOR_DURATION_SCALE,
      1.0f
    ) == 0.0f
  } catch (e: Exception) {
    false
  }
}

fun Modifier.springClickable(
  onClick: () -> Unit
): Modifier = composed {
  val context = LocalContext.current
  val reducedMotion = remember { isReducedMotionEnabled(context) }
  
  if (reducedMotion) {
    this.clickable(onClick = onClick)
  } else {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
      targetValue = if (pressed) 0.96f else 1.0f,
      animationSpec = spring(
        dampingRatio = 0.6f,
        stiffness = 300f
      ),
      label = "press_scale"
    )
    
    this
      .graphicsLayer {
        scaleX = scale
        scaleY = scale
      }
      .pointerInput(Unit) {
        detectTapGestures(
          onPress = {
            pressed = true
            tryAwaitRelease()
            pressed = false
          },
          onTap = { onClick() }
        )
      }
  }
}

@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(16.dp),
  borderWidth: Dp = 1.dp,
  onClick: (() -> Unit)? = null,
  content: @Composable BoxScope.() -> Unit
) {
  val cardModifier = modifier
    .clip(shape)
    .background(GlassBg)
    .border(
      width = borderWidth,
      brush = Brush.linearGradient(
        colors = listOf(GlassBorderStart, GlassBorderEnd)
      ),
      shape = shape
    )
    .drawBehind {
      // Soft, low-opacity large shadow for depth
      drawRect(
        color = Color(0x05000000),
        size = size
      )
    }

  val finalModifier = if (onClick != null) {
    cardModifier.springClickable(onClick)
  } else {
    cardModifier
  }

  Box(
    modifier = finalModifier,
    contentAlignment = androidx.compose.ui.Alignment.CenterStart,
    content = content
  )
}

@Composable
fun AmbientMeshBackground(
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit
) {
  Box(
    modifier = modifier
      .background(DarkBg)
      .drawBehind {
        // Large ambient glowing mesh circles
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color(0x228B5CF6), Color.Transparent),
            radius = size.width * 0.8f
          ),
          radius = size.width * 0.8f,
          center = androidx.compose.ui.geometry.Offset(0f, 0f)
        )
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color(0x19EC4899), Color.Transparent),
            radius = size.width * 0.8f
          ),
          radius = size.width * 0.8f,
          center = androidx.compose.ui.geometry.Offset(size.width, size.height)
        )
      }
  ) {
    content()
  }
}
