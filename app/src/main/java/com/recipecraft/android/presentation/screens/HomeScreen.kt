package com.recipecraft.android.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.recipecraft.android.domain.model.Recipe
import com.recipecraft.android.presentation.viewmodels.HomeViewModel

/**
 * HomeScreen with LazyColumn stable keys (P1 Blocker Fix)
 * 
 * Fixes:
 * - Added key = { recipe.id } to suggested recipes
 * - Added contentType for header and recipe items
 * - Prevents UI glitches when recipes update
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onRecipeClick: (Recipe) -> Unit = {},
    onCreateClick: () -> Unit = {}
) {
    val suggestedRecipes by viewModel.suggestedRecipes.collectAsStateWithLifecycle(emptyList())
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle(null)
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Icon(Icons.Default.Add, contentDescription = "Create Recipe")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item(
                key = "header",
                contentType = "header"
            ) {
                HomeHeader(
                    userName = userPreferences?.userName ?: "Chef",
                    recipesCount = suggestedRecipes.size
                )
            }
            
            // Suggested recipes section
            if (suggestedRecipes.isNotEmpty()) {
                item(
                    key = "suggested_header",
                    contentType = "section_header"
                ) {
                    Text(
                        text = "Suggested for You",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                items(
                    items = suggestedRecipes,
                    key = { recipe -> recipe.id },  // ✅ P1 FIX: Stable key
                    contentType = { "recipe_suggestion" } // ✅ P1 FIX: Performance
                ) { recipe ->
                    SuggestedRecipeItem(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe) }
                    )
                }
            } else {
                item(
                    key = "empty_state",
                    contentType = "empty"
                ) {
                    EmptyRecipesState(onCreateClick)
                }
            }
        }
    }
}

@Composable
fun HomeHeader(
    userName: String,
    recipesCount: Int
) {
    Column {
        Text(
            text = "Welcome, $userName!",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$recipesCount recipes ready to cook",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SuggestedRecipeItem(
    recipe: Recipe,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${recipe.ingredients.size} ingredients • ${recipe.cookTime} mins",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (recipe.difficulty.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AssistChip(
                        onClick = {},
                        label = { Text(recipe.difficulty) }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyRecipesState(onCreateClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = "No recipes yet",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start by creating your first recipe",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCreateClick) {
            Text("Create Recipe")
        }
    }
}