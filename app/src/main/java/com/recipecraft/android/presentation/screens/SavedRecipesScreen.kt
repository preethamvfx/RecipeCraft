package com.recipecraft.android.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.recipecraft.android.domain.model.Recipe
import com.recipecraft.android.presentation.viewmodels.SavedRecipesViewModel

/**
 * SavedRecipesScreen with LazyColumn stable keys (P1 Blocker Fix)
 * 
 * Fixes:
 * - Added key = { recipe.id } to prevent recomposition bugs
 * - Added contentType = "recipe_card" for performance
 * - Prevents scroll position reset on state updates
 * - Reduces memory waste with large lists
 */
@Composable
fun SavedRecipesScreen(
    viewModel: SavedRecipesViewModel,
    onRecipeClick: (Recipe) -> Unit = {},
    onDeleteClick: (Recipe) -> Unit = {}
) {
    val recipes by viewModel.savedRecipes.collectAsStateWithLifecycle(emptyList())
    
    if (recipes.isEmpty()) {
        EmptyStateView(
            message = "No saved recipes yet",
            actionText = "Create Recipe",
            onAction = { /* navigate to create */ }
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = recipes,
                key = { recipe -> recipe.id },  // ✅ P1 FIX: Stable key prevents recomposition bugs
                contentType = { "recipe_card" }  // ✅ P1 FIX: Performance optimization
            ) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    onClick = { onRecipeClick(recipe) },
                    onDelete = { onDeleteClick(recipe) }
                )
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${recipe.cookTime} mins • ${recipe.difficulty}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${recipe.ingredients.size} ingredients",
                    style = MaterialTheme.typography.labelSmall
                )
                TextButton(onClick = onDelete) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    message: String,
    actionText: String,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAction) {
            Text(actionText)
        }
    }
}