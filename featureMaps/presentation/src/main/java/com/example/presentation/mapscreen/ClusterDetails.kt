package com.example.presentation.mapscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.presentation.mapscreen.utils.CellData


@Composable
fun ClusterDetails(
    cellsInSelectedCluster: List<CellData>?,
    stationsCount: Int,
    viewModel: MapScreenModel = hiltViewModel()
) {
    Box {
        AnimatedVisibility(
            visible = cellsInSelectedCluster != null || stationsCount > 0,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column {
                Text("Stations Count = $stationsCount")
                cellsInSelectedCluster?.let {
                    ClusterDetailsDialog(
                        cells = it,
                        onDismiss = { viewModel.clearSelection() },
                        onCellClick = { cellId ->
//                        viewModel.onSingleCellMarkerClick(cellId)
                            viewModel.clearSelection()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ClusterDetailsDialog(
    cells: List<CellData>,
    onDismiss: () -> Unit,
    onCellClick: (CellData) -> Unit
) {
    if (cells.isEmpty()) {
        Text("Loading cell details...")
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(cells.size) { i ->
                var cell = cells[i]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onCellClick(cell) },
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Cell ID: ${cell.CELLID}")
                        Text("LAC: ${cell.LAC}, MNC: ${cell.MNC}")
                        Text("Lat: ${cell.LAT}, Lon: ${cell.LON}")
                    }
                }
            }
        }
    }
}