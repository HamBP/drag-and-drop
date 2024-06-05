package com.example.draganddrop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draganddrop.ui.theme.DragAndDropTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DragAndDropTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        DragAndDropList()
                    }
                }
            }
        }
    }
}

@Composable
fun DragAndDropList() {
    var items by remember { mutableStateOf((1..20).map { "Item $it" }) }
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var offsetY by remember { mutableStateOf(0f) }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(items, { it }) { item ->
            val index = items.indexOf(item)
            val modifier = if (draggedItemIndex == index) {
                Modifier
                    .offset(y = offsetY.roundToInt().dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            } else {
                Modifier
            }
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                draggedItemIndex = index
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                offsetY += dragAmount.y
                                val newIndex =
                                    (index + (offsetY / 100).toInt()).coerceIn(0, items.size - 1)
                                if (newIndex != index) {
                                    items = items
                                        .toMutableList()
                                        .apply {
                                            add(newIndex, removeAt(index))
                                        }
                                    draggedItemIndex = newIndex
                                    offsetY = 0f
                                }
                            },
                            onDragEnd = {
                                draggedItemIndex = null
                                offsetY = 0f
                            }
                        )
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    androidx.compose.foundation.text.BasicText(text = item)
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewDragAndDropList() {
    Surface {
        DragAndDropList()
    }
}
