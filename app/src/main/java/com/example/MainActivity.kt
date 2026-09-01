package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.local.AppDatabase
import com.example.data.local.UserPreferencesRepository
import com.example.data.repository.CraftingRepositoryImpl
import com.example.ui.navigation.MainAppNavigation
import com.example.ui.theme.AlbionCraftTheme
import com.example.ui.theme.ObsidianBg
import com.example.ui.viewmodel.CraftingViewModel
import com.example.ui.viewmodel.CraftingViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: CraftingViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val preferencesRepository = UserPreferencesRepository(applicationContext)
        val craftingRepository = CraftingRepositoryImpl(database, preferencesRepository)
        CraftingViewModelFactory(craftingRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlbionCraftTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ObsidianBg
                ) {
                    MainAppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
