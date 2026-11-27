package com.example.recipebook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter(
    private val onRecipeClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_RECIPE = 1
    }

    private val displayItems = mutableListOf<RecipeListItem>()

    fun setRecipes(recipes: List<Recipe>) {
        displayItems.clear()

        // Group Savory recipes
        displayItems.add(RecipeListItem.FlavorHeader(Flavor.SAVORY))
        val savoryRecipes = recipes.filter { it.flavor == Flavor.SAVORY }
        savoryRecipes.forEach { displayItems.add(RecipeListItem.RecipeItem(it)) }

        // Group Sweet recipes
        displayItems.add(RecipeListItem.FlavorHeader(Flavor.SWEET))
        val sweetRecipes = recipes.filter { it.flavor == Flavor.SWEET }
        sweetRecipes.forEach { displayItems.add(RecipeListItem.RecipeItem(it)) }

        notifyDataSetChanged()
    }

    fun getItem(position: Int): RecipeListItem {
        return displayItems[position]
    }

    override fun getItemViewType(position: Int): Int {
        return when (displayItems[position]) {
            is RecipeListItem.FlavorHeader -> TYPE_HEADER
            is RecipeListItem.RecipeItem -> TYPE_RECIPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            val view = inflater.inflate(R.layout.item_flavor_header, parent, false)
            FlavorHeaderViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_recipe, parent, false)
            RecipeViewHolder(view, onRecipeClick)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = displayItems[position]) {
            is RecipeListItem.FlavorHeader -> (holder as FlavorHeaderViewHolder).bind(item.flavor)
            is RecipeListItem.RecipeItem -> (holder as RecipeViewHolder).bind(item.recipe)
        }
    }

    override fun getItemCount(): Int = displayItems.size

    class FlavorHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerTitle: TextView = itemView.findViewById(R.id.header_title)
        private val accentBar: View = itemView.findViewById(R.id.header_accent_bar)

        fun bind(flavor: Flavor) {
            val context = itemView.context
            if (flavor == Flavor.SAVORY) {
                headerTitle.text = context.getString(R.string.savory)
                headerTitle.setTextColor(ContextCompat.getColor(context, R.color.savory_color))
                accentBar.setBackgroundColor(ContextCompat.getColor(context, R.color.savory_color))
            } else {
                headerTitle.text = context.getString(R.string.sweet)
                headerTitle.setTextColor(ContextCompat.getColor(context, R.color.sweet_color))
                accentBar.setBackgroundColor(ContextCompat.getColor(context, R.color.sweet_color))
            }
        }
    }

    class RecipeViewHolder(
        itemView: View,
        private val onRecipeClick: (Recipe) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val recipeTitle: TextView = itemView.findViewById(R.id.recipe_title)
        private val recipeDescription: TextView = itemView.findViewById(R.id.recipe_description)
        private val recipeFlavorTag: TextView = itemView.findViewById(R.id.recipe_flavor_tag)

        fun bind(recipe: Recipe) {
            val context = itemView.context
            recipeTitle.text = recipe.title
            recipeDescription.text = recipe.description

            if (recipe.flavor == Flavor.SAVORY) {
                recipeFlavorTag.text = context.getString(R.string.savory).uppercase()
                recipeFlavorTag.setBackgroundResource(R.drawable.bg_badge_savory)
                recipeFlavorTag.setTextColor(ContextCompat.getColor(context, R.color.savory_color))
            } else {
                recipeFlavorTag.text = context.getString(R.string.sweet).uppercase()
                recipeFlavorTag.setBackgroundResource(R.drawable.bg_badge_sweet)
                recipeFlavorTag.setTextColor(ContextCompat.getColor(context, R.color.sweet_color))
            }

            itemView.setOnClickListener {
                onRecipeClick(recipe)
            }
        }
    }
}