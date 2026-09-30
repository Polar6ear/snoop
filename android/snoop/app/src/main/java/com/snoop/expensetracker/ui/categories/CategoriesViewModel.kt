package com.snoop.expensetracker.ui.categories

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snoop.expensetracker.data.db.dao.CategoryDao
import com.snoop.expensetracker.data.db.entities.CategoryEntity
import com.snoop.expensetracker.ui.theme.SnoopColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryDao: CategoryDao
) : ViewModel() {

    val categories: StateFlow<List<CategoryEntity>> = categoryDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Spec §11: "full user-created custom categories — no age-segmented presets." icon is a
     * Material icon name the UI picker offers; color is auto-assigned from the rotating palette
     * so the user doesn't have to pick one, but nothing stops adding a color picker later.
     */
    fun deleteCustomCategory(id: Long) = viewModelScope.launch { categoryDao.deleteCustom(id) }

    fun createCustomCategory(name: String, icon: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            val existingCount = categoryDao.count()
            val color = SnoopColors.categoryPalette[existingCount % SnoopColors.categoryPalette.size]
            categoryDao.insert(
                CategoryEntity(
                    name = trimmed,
                    icon = icon,
                    color = String.format("#%06X", 0xFFFFFF and color.toArgb()),
                    isCustom = true
                )
            )
        }
    }
}
