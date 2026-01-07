package com.recipecraft.android.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.recipecraft.android.domain.model.CookingHistory
import com.recipecraft.android.presentation.viewmodels.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * HistoryScreen with LazyColumn stable keys (P1 Blocker Fix)
 * 
 * Fixes:
 * - Added key = { item.id } to prevent recomposition bugs
 * - Added contentType = "history_item" for performance
 * - Stable keys ensure correct history item identity
 */
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel
) {
    val cookingHistory by viewModel.cookingHistory.collectAsStateWithLifecycle(emptyList())
    
    if (cookingHistory.isEmpty()) {
        EmptyStateView(
            message = "No cooking history yet",
            actionText = "Start Cooking",
            onAction = { /* navigate to home */ }
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = cookingHistory,
                key = { item -> item.id },  // ✅ P1 FIX: Stable key
                contentType = { "history_item" } // ✅ P1 FIX: Performance
            ) { item ->
                HistoryCard(item)
            }
        }
    }
}

@Composable
fun HistoryCard(item: CookingHistory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.recipeName,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatDate(item.cookedAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (item.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.notes,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            // Show cooking duration if available
            if (item.cookingDuration > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Cooked in ${item.cookingDuration} minutes",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}