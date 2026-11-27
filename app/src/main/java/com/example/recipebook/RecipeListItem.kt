package com.example.recipebook

sealed interface RecipeListItem {
    data class FlavorHeader(val flavor: Flavor) : RecipeListItem
    data class RecipeItem(val recipe: Recipe) : RecipeListItem
}