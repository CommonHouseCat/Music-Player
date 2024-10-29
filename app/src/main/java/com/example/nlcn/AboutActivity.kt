package com.example.nlcn

import android.os.Bundle
import androidx.compose.material3.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


import com.example.nlcn.ui.theme.NLCNTheme
import java.util.Locale

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NLCNTheme {
                AboutScreen(this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(context: Context) {
    val dataStore = remember { PreferenceDataStore(context) }
    val currentLanguage = dataStore.getLanguage.collectAsState(initial = "en")

    // Update configuration when language changes
    val updatedContext = remember(currentLanguage.value) {
        val locale = Locale(currentLanguage.value)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)

        context.createConfigurationContext(configuration)
    }


   Surface (
       modifier = Modifier.fillMaxSize(),
       color = Color.Black
   ) {
       Column {
           TopAppBar(
               title = {
                   Row(verticalAlignment = Alignment.CenterVertically) {
                       Text(text = with(updatedContext) { getString(R.string.about) }, color = Color.White)
                       Spacer(modifier = Modifier.width(8.dp))
                       Icon(
                           imageVector = Icons.Outlined.Info,
                           contentDescription = "Info",
                           tint = Color.White
                       )
                   }
               },
               navigationIcon = {
                   IconButton(onClick = { (context as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed() }) {
                       Icon(
                           imageVector =  Icons.AutoMirrored.Filled.ArrowBack,
                           contentDescription = "Back",
                           tint = Color.White
                       )
                   }
               },
               colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
           )

           Column(
               modifier = Modifier
                   .fillMaxSize()
                   .padding(16.dp)
           ) {
               // General app description
               Text(text = with(updatedContext) { getString(R.string.about_app) }, color = Color.White, style = MaterialTheme.typography.headlineSmall)
               Text(
                   text = with(updatedContext) { getString(R.string.about_description) },
                   color = Color.White,
                   style = MaterialTheme.typography.bodyMedium,
                   textAlign = TextAlign.Justify
               )

               Spacer(modifier = Modifier.height(12.dp))

               // Project Scope
               Text(text = with(updatedContext) { getString(R.string.project_scope) }, color = Color.White, style = MaterialTheme.typography.headlineSmall)
               Text(
                   text = with(updatedContext) { getString(R.string.project_scope_text) },
                   color = Color.White,
                   style = MaterialTheme.typography.bodyMedium,
                   textAlign = TextAlign.Justify
               )
               
               Spacer(modifier = Modifier.height(12.dp))

               // Technology Used
               Text(text = with(updatedContext) { getString(R.string.technology) }, color = Color.White, style = MaterialTheme.typography.headlineSmall)
               Text(
                   text = with(updatedContext) { getString(R.string.technology_text) },
                   color = Color.White,
                   style = MaterialTheme.typography.bodyMedium,
                   textAlign = TextAlign.Justify
               )

               Spacer(modifier = Modifier.height(12.dp))

               // Technology Used
               Text(text = with(updatedContext) { getString(R.string.disclaimer) }, color = Color.White, style = MaterialTheme.typography.headlineSmall)
               Text(
                   text = with(updatedContext) { getString(R.string.disclaimer_text) },
                   color = Color.White,
                   style = MaterialTheme.typography.bodyMedium,
                   textAlign = TextAlign.Justify
               )

               Spacer(modifier = Modifier.height(12.dp))

               // Credit
               Text(text = with(updatedContext) { getString(R.string.credits) }, color = Color.White, style = MaterialTheme.typography.headlineSmall)
               Text(
                   text = with(updatedContext) { getString(R.string.credits_text) },
                   color = Color.White,
                   style = MaterialTheme.typography.bodyMedium,
                   textAlign = TextAlign.Justify
               )
           }
       }
   }
}