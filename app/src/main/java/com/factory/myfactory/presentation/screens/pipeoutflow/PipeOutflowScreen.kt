package com.factory.myfactory.presentation.screens.pipeoutflow

import android.widget.Button
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import com.factory.myfactory.data.models.CutPieceStock
import com.factory.myfactory.data.models.PipeStock
import com.factory.myfactory.data.models.ScrapStock
import com.factory.myfactory.helper.getApproxPipeWeight
import com.factory.myfactory.helper.round
import com.factory.myfactory.presentation.components.DropdownField
import com.factory.myfactory.presentation.screens.pipe.viemodel.PipeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PipeOutflowScreen(navHost: NavHostController,onBack: () -> Unit,viewModel: PipeViewModel = hiltViewModel()){

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = screenWidth > screenHeight
    val context = LocalContext.current

    val entries by viewModel.pipeOutflowEntry.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.successMessage.collectAsState()


    //inputs
    var pipeType by rememberSaveable { mutableStateOf("") }
    val pipeTypeOptions = listOf(
        "round","rect"
    )
    var pipeSize by rememberSaveable { mutableStateOf("") }
    // Map of coil sizes to their corresponding pipe options
    val pipeSizeAsPerType = mapOf(
        "round" to listOf("1/2\"","5/8\"","3/4\"","1\"","1.1/4\"","1.5\"","2\"","2.5\"","3\""),
        "rect" to listOf("1/2\" × 1/2\"","5/8\" × 5/8\"","3/4\" × 3/4\"", "1/2\" × 1\"", "1\" × 1\"", "3/4\" × 1.5\"", "1.5\" × 1.5\"","2\" × 1\"", "3 × 1\""),
    )
    fun getPipeSizeOptions(type: String): List<String> {
        // Return the list for the selected type ("round" or "rect"), or empty if not available
        return pipeSizeAsPerType[type] ?: emptyList()
    }

    var coilSize by rememberSaveable { mutableStateOf("") }
    val coilSizeOptions = mapOf(
        "round" to listOf("1/2\"", "5/8\"", "3/4\"", "1\"", "1.1/4\"", "1.5\"", "2\"", "2.5\"", "3\""),
        "rect" to mapOf(
            "5/8\"" to listOf("1/2\" × 1/2\""),
            "3/4\"" to listOf("5/8\" × 5/8\""),
            "1\"" to listOf("1/2\" × 1\"", "3/4\" × 3/4\""),
            "1.1/4\"" to listOf("1\" × 1\""),
            "1.5\"" to listOf("3/4\" × 1.5\""),
            "2\"" to listOf("2\" × 1\"", "1.5\" × 1.5\""),
            "2.5\"" to listOf("3 × 1\""),
        )
    )

    fun getCoilSizeOptions(pipeType: String, pipeSize: String): String {
        return if (pipeType == "round") {
            // For round pipes, coil is same as size
            pipeSize
        } else {
            // For rect pipes, check the mapping
            val rectMap = coilSizeOptions["rect"] as? Map<String, List<String>> ?: return ""
            // Find the first coil size that contains this rect pipe
            rectMap.entries.firstOrNull { it.value.contains(pipeSize) }?.key ?: ""
        }
    }


    var gauges by rememberSaveable { mutableStateOf("") }
    val gaugesOptions = listOf(
        "16g", "18g", "20g", "22g", "24g","26g","28g"

    )
    var grades by rememberSaveable { mutableStateOf("") }
    val gradesOptions = listOf(
        "202", "304"
    )
    var numberOfPipes by rememberSaveable { mutableStateOf("") }

    val approxWeight by remember(pipeSize, gauges, pipeType, numberOfPipes) {
        derivedStateOf {
            if (pipeSize.isNotEmpty() && gauges.isNotEmpty() && pipeType.isNotEmpty()) {
                val singleWeight = getApproxPipeWeight(pipeSize, gauges,pipeType)
                val totalWeight = if (numberOfPipes.isNotEmpty()) {
                    singleWeight * numberOfPipes.toInt()
                } else singleWeight
                String.format("%.2f", totalWeight)
            } else ""
        }
    }

    LaunchedEffect(success) {
        success?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages() // clear message after showing

            // Reset all input fields
            pipeType = ""
            pipeSize = ""
            coilSize = ""
            gauges = ""
            grades = ""
            numberOfPipes = ""
        }
    }



    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(horizontal = 16.dp, vertical = 8.dp)
                    .padding(bottom = 8.dp)
            ){
                Text(
                    text = "Pipe Outflow",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = if(isLandscape) (screenHeight.value * 0.06f).sp else (screenWidth.value * 0.06f).sp, // dynamic font size
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)

                )
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    "Back arrow",
                    Modifier.align(Alignment.CenterStart)
                        .clickable(onClick = onBack)
                )
            }
        }

    ){innerPadding->

        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).fillMaxWidth() .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()).windowInsetsPadding(
                    WindowInsets.ime.union(WindowInsets.systemBars) //  reacts to keyboard
                ),
        ) {

            DropdownField(
                label = "Type of Pipe",
                options = pipeTypeOptions,
                selectedOption = pipeType,
                onOptionSelected = {
                    pipeType = it
                    pipeSize = ""
                    coilSize = ""               // Reset pipe size
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)

            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                val pipeOptions = getPipeSizeOptions(pipeType)
                DropdownField(
                    label = "Pipe Size",
                    options = pipeOptions,
                    selectedOption = pipeSize,
                    onOptionSelected = { pipeSize = it },
                    modifier = Modifier.weight(1f).padding(bottom = 10.dp, end = 10.dp)

                )
                val coilOptions = getCoilSizeOptions(pipeType, pipeSize)
                coilSize = coilOptions
                DropdownField(
                    label = "Coil Size",
                    selectedOption = coilSize, // show the single value
                    options = listOf(coilSize), // wrap it in a list just to satisfy DropdownField
                    onOptionSelected = { /* optional if you want user to click */ },
                    modifier = Modifier.weight(1f).padding(bottom = 10.dp),

                    )

            }



            DropdownField(
                label = "Gauge",
                options = gaugesOptions,
                selectedOption = gauges,
                onOptionSelected = { gauges = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)

            )
            DropdownField(
                label = "Grade",
                options = gradesOptions,
                selectedOption = grades,
                onOptionSelected = { grades = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
            )

            OutlinedTextField(
                value = numberOfPipes,
                onValueChange = { input ->
                    // allow only digits
                    if (input.all { it.isDigit() }) {
                        numberOfPipes = input
                    }
                },
                label = { Text("Number of pipes") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone
                ),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                enabled = true
            )

            Text(
                text = "Approx Weight: $approxWeight kg",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {

                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val uid = currentUser?.uid

                    // Validation before submitting
                    if (pipeType.isBlank() || pipeSize.isBlank() || gauges.isBlank() || grades.isBlank()) {
                        viewModel.clearMessages()
                        viewModel.addPipeEntryError("Please fill all required fields.")
                        return@Button
                    }
                    val numPipes = numberOfPipes.toIntOrNull() ?: 0
                    val approx = getApproxPipeWeight(pipeSize, gauges, pipeType) * numPipes
                    if (uid != null) {
                        val db = FirebaseFirestore.getInstance()
                        db.collection("users").document(uid)
                            .get()
                            .addOnSuccessListener { document ->
                                val name = document?.getString("name") ?: "Unknown"

                                val newPipe = PipeStock(
                                    pipeType = pipeType,
                                    pipeSize = pipeSize,
                                    gauge = gauges,
                                    grade = grades,
                                    quantity = numPipes,
                                    approxWeight = approx.round(2),
                                    entryUserId = uid,
                                    entryUserName = name
                                )
                                viewModel.addPipeOutflow(newPipe)

                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to fetch user info", Toast.LENGTH_SHORT).show()
                            }


                    }


                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                enabled = (numberOfPipes.toIntOrNull()?:0) > 0 && coilSize.isNotEmpty() && gauges.isNotEmpty() && grades.isNotEmpty()

            ){
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Submitting items…")
                } else {
                    Text("Submit")
                }
            }

        }


    }
}