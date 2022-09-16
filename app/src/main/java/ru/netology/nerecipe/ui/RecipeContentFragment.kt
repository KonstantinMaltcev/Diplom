package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.RecipeContentFragmentBinding
import ru.netology.nerecipe.recipe.Recipe
import ru.netology.nerecipe.utils.focusAndShowKeyboard
import ru.netology.nerecipe.viewModel.RecipeViewModel

class RecipeContentFragment : Fragment() {

    private val viewModel: RecipeViewModel by activityViewModels()

    private val args by navArgs<RecipeContentFragmentArgs>()

    private lateinit var currentRecipe: Recipe

    private lateinit var item: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RecipeContentFragmentBinding.inflate(
        layoutInflater, container, false
    ).also { binding ->

        val spinner: Spinner = binding.editCategory
        context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.category_names,
                android.R.layout.simple_spinner_item
            )
        }.also { adapter ->
            adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                item = parent?.getItemAtPosition(pos) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //пустое переопределение
            }
        }

        if (args.fromFragment == REQUEST_CURRENT_RECIPE_KEY) {
            currentRecipe = viewModel.data.value?.let { listRecipe ->
                listRecipe.firstOrNull {
                    it.id == args.initialContent
                }
            } as Recipe

            binding.editTitle.setText(currentRecipe.title)
            binding.editAuthor.setText(currentRecipe.author)

            val categoriesList = resources.getStringArray(R.array.category_names)
            val numberCategory = categoriesList.indexOf(currentRecipe.category)
            binding.editCategory.setSelection(numberCategory)
        }
        binding.editTitle.focusAndShowKeyboard()
        binding.editAuthor.focusAndShowKeyboard()
        binding.ok.setOnClickListener { onOkButtonClicked(binding) }
    }.root

    private fun onOkButtonClicked(binding: RecipeContentFragmentBinding) {
        val textTitle = binding.editTitle.text
        val textAuthor = binding.editAuthor.text
        val textCategory = item

        if (!textAuthor.isNullOrBlank() || !textTitle.isNullOrBlank()) {
            if (args.fromFragment == REQUEST_FEED_KEY ||
                args.fromFragment == REQUEST_FEED_FAVORITE_KEY
            ) {
                viewModel.onSaveButtonClicked(
                    author = textAuthor.toString(),
                    title = textTitle.toString(),
                    category = textCategory,
                    content = null
                )
            } else if (args.fromFragment == REQUEST_CURRENT_RECIPE_KEY) {
                viewModel.onSaveButtonClicked(
                    author = textAuthor.toString(),
                    title = textTitle.toString(),
                    content = currentRecipe.content,
                    category = textCategory
                )
            }
            findNavController().popBackStack()
        }
    }

    companion object {
        const val REQUEST_FEED_KEY = "feed"
        const val REQUEST_CURRENT_RECIPE_KEY = "current"
        const val REQUEST_FEED_FAVORITE_KEY = "favorite"
    }
}