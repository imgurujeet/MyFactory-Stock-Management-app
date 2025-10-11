package com.factory.myfactory.presentation.screens.coil

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.factory.myfactory.data.models.CoilStockItem
import com.factory.myfactory.presentation.components.DropdownField
import com.factory.myfactory.presentation.screens.coil.viewmodel.CoilViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoilEntryScreen(navHost: NavHostController,onBack : () -> Unit,viewModel: CoilViewModel = hiltViewModel()) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = screenWidth > screenHeight
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val uploadResult by viewModel.uploadResult.collectAsState()







    //inputs
    var size by rememberSaveable { mutableStateOf("") }
    val sizeOptions = listOf(
        "1/2\"", "5/8\"", "3/4\"", "1\"",
        "1.5\"", "2\"", "2.5\"", "3\""
    )
    var guages by rememberSaveable { mutableStateOf("") }
    val gaugesOptions = listOf(
        "16g", "18g", "20g", "22g", "24g","26g","28g"

    )
    var grades by rememberSaveable { mutableStateOf("") }
    val gradesOptions = listOf(
        "202", "304"
    )
    var weight by rememberSaveable { mutableStateOf("") }


    // success Message
    LaunchedEffect(uploadResult) {
        if (uploadResult == "success") {
            Toast.makeText(context, "Wohoo! Item added to Stock", Toast.LENGTH_SHORT).show()
            // Reset the input fields
            size = ""
            guages = ""
            grades = ""
            weight = ""
            // Optional: pop back if you still want
            navHost.popBackStack()
        } else if (uploadResult != null) {
            Toast.makeText(context, "Something Went Wrong: $uploadResult", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold (
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            ){
                Text(
                    text = "Add Coil",
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
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding).fillMaxWidth() .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ){

                DropdownField(
                    label ="Size",
                    options = sizeOptions,
                    selectedOption = size,
                    onOptionSelected = {size=it},
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)

                )
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

                OutlinedTextField(
                    value = weight,
                    onValueChange =  { input ->
                        // allow only digits
                        if (input.all { it.isDigit() }) {
                            weight = input
                        }
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
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val uid = currentUser?.uid
                        if (!uid.isNullOrEmpty()) {  // ✅ check non-empty
                            val db = FirebaseFirestore.getInstance()
                            db.collection("users").document(uid)
                                .get()
                                .addOnSuccessListener { document ->
                                    val name = document?.getString("name") ?: "Unknown"
                                    val stock = CoilStockItem(
                                        size = size,
                                        gauge = guages,
                                        grade = grades,
                                        weight = weight.toDouble(),
                                        entryUserId = uid,
                                        entryUsername = name,
                                        timestamp = System.currentTimeMillis()
                                    )
                                    viewModel.addStock(stock)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Failed to fetch user info", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(context, "User UID invalid", Toast.LENGTH_SHORT).show()
                        }

                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isLoading && (weight.toIntOrNull()?:0) > 0 && grades.isNotEmpty() && guages.isNotEmpty() && size.isNotEmpty()

                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Stocking items…")
                    } else {
                        Text("Add to Stock")
                    }
                }
            }

        }
    )
}



