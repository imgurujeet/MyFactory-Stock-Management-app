package com.factory.myfactory.presentation.screens.scrap

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
fun ScrapCutPieceOutFlowScreen(navHost: NavHostController,onBack : () -> Unit,viewModel: PipeViewModel = hiltViewModel()){
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = screenWidth > screenHeight
    val context = LocalContext.current
    //val isLoading by viewModel.isLoading.collectAsState()

    val entries by viewModel.pipeOutflowEntry.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.successMessage.collectAsState()


//    var size by rememberSaveable { mutableStateOf("") }
//    val sizeOptions = listOf(
//        "1/2\"", "5/8\"", "3/4\"", "1\"",
//        "1.5\"", "2\"", "2.5\"", "3\""
//    )
    var guages by rememberSaveable { mutableStateOf("") }
    val gaugesOptions = listOf(
        "16g", "18g", "20g", "22g", "24g","26g","28g"

    )
    var grades by rememberSaveable { mutableStateOf("") }
    val gradesOptions = listOf(
        "202", "304"
    )
    var scrapWeight by rememberSaveable { mutableStateOf("") }
    var cutPieceWeight by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(success) {
        success?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages() // clear message after showing

            // Reset all input fields
            //size = ""
            guages = ""
            grades = ""
            scrapWeight = ""

        }
    }


    Scaffold (
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
            ){
                Text(
                    text = "Scrap CutPiece Outflow",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = if(isLandscape) (screenHeight.value * 0.06f).sp else (screenWidth.value * 0.06f).sp, // dynamic font size
                    fontWeight = FontWeight.Medium,
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
    ) { innerPadding ->

        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).fillMaxWidth() .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ){

//            DropdownField(
//                label ="Size",
//                options = sizeOptions,
//                selectedOption = size,
//                onOptionSelected = {size=it},
//                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
//
//            )
            DropdownField(
                label ="Gauge",
                options = gaugesOptions,
                selectedOption = guages,
                onOptionSelected = {guages=it},
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)

            )
            DropdownField(
                label ="Grade",
                options = gradesOptions,
                selectedOption = grades,
                onOptionSelected = {grades=it},
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
            ){
                OutlinedTextField(
                    value = scrapWeight,
                    onValueChange =  { input ->
                        // allow only digits
                        if (input.all { it.isDigit() }) {
                            scrapWeight = input
                        }
                    },
                    label = { Text("Scrap(Kgs)") },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    enabled = !loading
                )
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = cutPieceWeight,
                    onValueChange =  { input ->
                        // allow only digits
                        if (input.all { it.isDigit() }) {
                            cutPieceWeight = input
                        }
                    },
                    label = { Text("CutPcs.(Kgs)") },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    enabled = !loading
                )

            }


            Text(
                text = "1 Tonne = 1000 KGs",
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
                    if (guages.isBlank() || grades.isBlank() || scrapWeight.isBlank() || cutPieceWeight.isBlank()) {
                        viewModel.clearMessages()
                        viewModel.addPipeEntryError("Please fill all required fields.")
                        return@Button
                    }
                    if (uid != null) {
                        val db = FirebaseFirestore.getInstance()
                        db.collection("users").document(uid)
                            .get()
                            .addOnSuccessListener { document ->
                                val name = document?.getString("name") ?: "Unknown"

                                val newScrapEntry = ScrapStock(
                                    gauge = guages,
                                    grade = grades,
                                    weight = scrapWeight.toDouble(),
                                    entryUserId = uid,
                                    entryUserName = name
                                )

                                val newCutPieceEntry = CutPieceStock(
                                    gauge = guages,
                                    grade = grades,
                                    weight = cutPieceWeight.toDouble(),
                                    entryUserId = uid,
                                    entryUserName = name
                                )
                                viewModel.addScrapCutPieceOutflow(newScrapEntry,newCutPieceEntry)

                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to fetch user info", Toast.LENGTH_SHORT).show()
                            }


                    }


                },
                modifier=Modifier.fillMaxWidth(),
                shape=RoundedCornerShape(10.dp)

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