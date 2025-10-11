package com.factory.myfactory.presentation.screens.coil

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.factory.myfactory.data.models.CoilStockItem
import com.factory.myfactory.data.repositories.CoilRepository
import com.factory.myfactory.presentation.components.DropdownField
import com.factory.myfactory.presentation.screens.coil.viewmodel.CoilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateStockScreen(
    navHost: NavHostController,
    coilStockItemWithId: CoilRepository.CoilStockItemWithId,
    viewModel: CoilViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = screenWidth > screenHeight
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val uploadResult by viewModel.uploadResult.collectAsState()
    val operationResult by viewModel.operationResult.collectAsState()

    // success / error handling

    LaunchedEffect(operationResult) {
        when (operationResult) {
            "update_success" -> {
                Toast.makeText(context, "Stock Updated!", Toast.LENGTH_SHORT).show()
                navHost.popBackStack()
                viewModel.clearOperationResult()
            }
            is String -> {
                Toast.makeText(context, "Error: $operationResult", Toast.LENGTH_SHORT).show()
                viewModel.clearOperationResult()
            }
        }
    }

    // pre-filled inputs
    var size by rememberSaveable { mutableStateOf(coilStockItemWithId.coilStockItem.size) }
    val sizeOptions = listOf("1/2\"", "5/8\"", "3/4\"", "1\"", "1.5\"", "2\"", "2.5\"", "3\"")

    var gauges by rememberSaveable { mutableStateOf(coilStockItemWithId.coilStockItem.gauge) }
    val gaugesOptions = listOf("16g", "18g", "20g", "22g", "24g", "26g", "28g")

    var grades by rememberSaveable { mutableStateOf(coilStockItemWithId.coilStockItem.grade) }
    val gradesOptions = listOf("202", "304")

    var weight by rememberSaveable { mutableStateOf(coilStockItemWithId.coilStockItem.weight.toString()) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Update Coil",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = if (isLandscape) (screenHeight.value * 0.06f).sp else (screenWidth.value * 0.06f).sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
                IconButton(
                    onClick = { navHost.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                DropdownField(
                    label = "Size",
                    options = sizeOptions,
                    selectedOption = size,
                    onOptionSelected = { size = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
                DropdownField(
                    label = "Gauge",
                    options = gaugesOptions,
                    selectedOption = gauges,
                    onOptionSelected = { gauges = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
                DropdownField(
                    label = "Grade",
                    options = gradesOptions,
                    selectedOption = grades,
                    onOptionSelected = { grades = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) weight = input
                    },
                    label = { Text("Weight") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isLoading
                )
                Text(
                    text = "1 Tonne = 1000 KGs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        val stock = CoilStockItem(
                            size = size,
                            gauge = gauges,
                            grade = grades,
                            weight = weight.toDouble(),
                        )
                        viewModel.updateStock(coilStockItemWithId.id, stock)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isLoading &&
                            (weight.toIntOrNull() ?: 0) > 0 &&
                            grades.isNotEmpty() &&
                            gauges.isNotEmpty() &&
                            size.isNotEmpty()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Updating…")
                    } else {
                        Text("Update Stock")
                    }
                }
            }
        }
    )
}
