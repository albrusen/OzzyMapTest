package com.example.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.feature_home.presentation.R

@Composable
fun HomeScreen(
    onItemClick: (
        lat: Double,
        lon: Double,
        Float?
    ) -> Unit
) {
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                ScenarioCard(
                    title = stringResource(R.string.australia_title),
                    description = stringResource(R.string.australia_desc),
                    onClick = {
                        val centerLat = -28.1666790536353
                        val centerLon = 123.473749471784
                        onItemClick(centerLat, centerLon, 22f)
                    }
                )
            }

        }
    }
}

@Composable
fun ScenarioCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.colorScheme.onSurfaceVariant.let {
                    MaterialTheme.typography.titleLarge.copy(
                        color = it
                    )
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.colorScheme.onSurfaceVariant.let {
                    MaterialTheme.typography.bodyMedium.copy(
                        color = it
                    )
                }
            )
        }
    }
}
