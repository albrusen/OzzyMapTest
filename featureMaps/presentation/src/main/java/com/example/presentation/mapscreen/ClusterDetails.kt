package com.example.presentation.mapscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.presentation.mapscreen.utils.CellData
import com.maps.presentation.R


@Composable
fun ClusterDetails(
    cellsInSelectedCluster: List<CellData>?,
    stationsCount: Int,
    viewModel: MapScreenModel = hiltViewModel()
) {
    Box {
        AnimatedVisibility(
            visible = !cellsInSelectedCluster.isNullOrEmpty() || stationsCount > 0,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(top = 32.dp)
                .fillMaxHeight()
                .width(165.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                    RoundedCornerShape(8.dp)
                )
        ) {
            Column {
                Button(onClick = {
                    viewModel.clearSelection()
                    viewModel.dismissCell()
                } ) {
                    Text("Закрыть")
                }
                if (stationsCount > viewModel.MAX_ALLOWED_STATIONS_IN_MEMORY) {
                    Text(stringResource(R.string.too_many_station_warning))
                }
                cellsInSelectedCluster?.let {
                    ClusterDetailsDialog(
                        cells = it,
                        onDismiss = { viewModel.clearSelection() },
                        onCellClick = { cellData ->
                            viewModel.onSingleCellMarkerClick(cellData)
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
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            items(cells.size) { i ->
                val cell = cells[i]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onCellClick(cell) },
                ) {
                    Column(modifier = Modifier.padding(8.dp)
                        .background(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
                            RoundedCornerShape(8.dp))) {
                        Text(modifier = Modifier.padding(8.dp), text = "Cell ID: ${cell.CELLID}")
                        Text(modifier = Modifier.padding(8.dp), text = "LAC: ${cell.LAC}, MNC: ${cell.MNC}")
                    }
                }
            }
        }
    }
}