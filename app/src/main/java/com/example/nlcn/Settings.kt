package com.example.nlcn

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(){
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        TopAppBar(
            title = {
                Row {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings Icon",
                        tint = Color.White,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Settings",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black
            )
        )

        // Spacer between the top bar and the row
        Spacer(modifier = Modifier.height(16.dp))

        // Row for Theme settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Theme",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )

            var expanded by remember { mutableStateOf(false) }
            var selectedOption by remember { mutableStateOf("Dark Mode") }

            Box(
                modifier = Modifier
                    .background(Color.DarkGray, shape = RoundedCornerShape(4.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = selectedOption,
                    color = Color.White
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.DarkGray)
                ) {
                    DropdownMenuItem(
                        text = { Text("Dark Mode", color = Color.White) },
                        onClick = {
                            selectedOption = "Dark Mode"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Light Mode", color = Color.White) },
                        onClick = {
                            selectedOption = "Light Mode"
                            expanded = false
                        }
                    )
                }
            }
        }

        // Row for Language settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Language",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
            )

            var expanded by remember { mutableStateOf(false) }
            var selectedOption by remember { mutableStateOf("English") }

            Box(
                modifier = Modifier
                    .background(Color.DarkGray, shape = RoundedCornerShape(4.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = selectedOption,
                    color = Color.White
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.DarkGray)
                ) {
                    DropdownMenuItem(
                        text = { Text("English", color = Color.White) },
                        onClick = {
                            selectedOption = "English"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Tiếng Việt", color = Color.White) },
                        onClick = {
                            selectedOption = "Tiếng Việt"
                            expanded = false
                        }
                    )
                }
            }
        }

        // Row for About Page
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable{
                    val intent = Intent(context, AboutActivity::class.java)
                   context.startActivity(intent)
                },
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "About",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(start = 32.dp)
                    .padding(vertical = 18.dp)
            )

            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "About",
                tint = Color.White
            )
        }
    }
}
