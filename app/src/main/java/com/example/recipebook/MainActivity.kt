package com.example.recipebook

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private val recipeList = mutableListOf<Recipe>()
    private lateinit var recipeAdapter: RecipeAdapter

    private lateinit var recipeTitleInput: EditText
    private lateinit var recipeDescriptionInput: EditText
    private lateinit var layoutTitle: TextInputLayout
    private lateinit var layoutDescription: TextInputLayout
    private lateinit var addSavoryButton: Button
    private lateinit var addSweetButton: Button
    private lateinit var recipesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = if (imeInsets.bottom > 0) imeInsets.bottom else systemBars.bottom
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding)
            insets
        }

        initViews()
        setupSampleData()
        setupRecyclerView()
        setupButtons()
    }

    private fun initViews() {
        recipesRecyclerView = findViewById(R.id.main_recipes_list)
        recipeTitleInput = findViewById(R.id.main_recipe_title)
        recipeDescriptionInput = findViewById(R.id.main_recipe_description)
        layoutTitle = findViewById(R.id.layout_recipe_title)
        layoutDescription = findViewById(R.id.layout_recipe_description)
        addSavoryButton = findViewById(R.id.main_add_savory_button)
        addSweetButton = findViewById(R.id.main_add_sweet_button)
    }

    private fun setupSampleData() {
        recipeList.add(
            Recipe(
                title = "Spaghetti Carbonara",
                description = "Rich and creamy classic Italian pasta dish made with eggs, pancetta, Pecorino Romano cheese, and black pepper.",
                flavor = Flavor.SAVORY
            )
        )
        recipeList.add(
            Recipe(
                title = "Garlic Herb Steak Bites",
                description = "Tender beef sirloin cubes seared to perfection in garlic herb butter with fresh parsley.",
                flavor = Flavor.SAVORY
            )
        )
        recipeList.add(
            Recipe(
                title = "Chocolate Lava Cake",
                description = "Decadent warm chocolate cake with a rich molten lava center, topped with powdered sugar.",
                flavor = Flavor.SWEET
            )
        )
        recipeList.add(
            Recipe(
                title = "Classic Apple Pie",
                description = "Flaky double-crust pie filled with fresh spiced apples, cinnamon, nutmeg, and warm caramel sauce.",
                flavor = Flavor.SWEET
            )
        )
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter { recipe ->
            showRecipeDialog(recipe)
        }

        recipesRecyclerView.layoutManager = LinearLayoutManager(this)
        recipesRecyclerView.adapter = recipeAdapter
        recipeAdapter.setRecipes(recipeList)

        setupSwipeToDelete()
    }

    private fun setupSwipeToDelete() {
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return

                val item = recipeAdapter.getItem(position)
                if (item is RecipeListItem.RecipeItem) {
                    val removedRecipe = item.recipe
                    recipeList.remove(removedRecipe)
                    recipeAdapter.setRecipes(recipeList)
                    Toast.makeText(
                        this@MainActivity,
                        "${removedRecipe.title} deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Do not allow deleting headers
                    recipeAdapter.notifyItemChanged(position)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recipesRecyclerView)
    }

    private fun setupButtons() {
        addSavoryButton.setOnClickListener {
            addNewRecipe(Flavor.SAVORY)
        }

        addSweetButton.setOnClickListener {
            addNewRecipe(Flavor.SWEET)
        }
    }

    private fun addNewRecipe(flavor: Flavor) {
        val title = recipeTitleInput.text?.toString()?.trim().orEmpty()
        val description = recipeDescriptionInput.text?.toString()?.trim().orEmpty()

        layoutTitle.error = null
        layoutDescription.error = null

        var isValid = true

        if (title.isEmpty()) {
            layoutTitle.error = getString(R.string.error_empty_title)
            isValid = false
        }

        if (description.isEmpty()) {
            layoutDescription.error = getString(R.string.error_empty_description)
            isValid = false
        }

        if (!isValid) return

        val newRecipe = Recipe(
            title = title,
            description = description,
            flavor = flavor
        )

        recipeList.add(newRecipe)
        recipeAdapter.setRecipes(recipeList)

        // Clear input fields
        recipeTitleInput.text?.clear()
        recipeDescriptionInput.text?.clear()
        recipeTitleInput.clearFocus()
        recipeDescriptionInput.clearFocus()

        Toast.makeText(
            this,
            "Added ${if (flavor == Flavor.SAVORY) "Savory" else "Sweet"} recipe!",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showRecipeDialog(recipe: Recipe) {
        MaterialAlertDialogBuilder(this)
            .setTitle(recipe.title)
            .setMessage(recipe.description)
            .setPositiveButton(R.string.dialog_close, null)
            .show()
    }
}