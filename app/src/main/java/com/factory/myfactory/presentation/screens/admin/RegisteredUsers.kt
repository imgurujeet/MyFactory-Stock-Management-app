package com.factory.myfactory.presentation.screens.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import com.factory.myfactory.data.models.RegisteredUser
import com.factory.myfactory.presentation.components.RegisterUserCard
import com.factory.myfactory.presentation.components.UserCard
import com.factory.myfactory.presentation.screens.admin.viewmodel.AdminViewModel
import com.factory.myfactory.presentation.screens.admin.viewmodel.RegisteredUserViewmodel
import com.factory.myfactory.presentation.screens.navigations.Screen
import com.google.firebase.auth.FirebaseAuth
import com.imgurujeet.stockease.data.models.User

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisteredUsersScreen(
    navHost: NavHostController,
    adminViewModel: AdminViewModel = hiltViewModel(),
    registeredUserViewmodel: RegisteredUserViewmodel = hiltViewModel()
){

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = screenWidth > screenHeight
    val registeredUser by  registeredUserViewmodel.registeredUser.collectAsState()
    var selectedUser by remember { mutableStateOf<RegisteredUser?>(null) }
    var showAddUserDialog by remember {mutableStateOf(false)}
    var searchQuery by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf<String?>(null) } // null = no filter
    val context = LocalContext.current

    Scaffold (
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ){
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable(onClick = {
                            navHost.navigate(Screen.AdminDashboard.route)

                        })
                )
                Text(
                    text = "Allowed Users",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = if(isLandscape) (screenHeight.value * 0.06f).sp else (screenWidth.value * 0.06f).sp, // dynamic font size
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center)
                )

            }
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(innerPadding)
        ){

            OutlinedButton(
                onClick = {
                   // navHost.navigate(Screen.RegisteredUsersScreen.route)
                    showAddUserDialog = true

                },
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E88E5).copy(0.8f)
                    ),

                    border = BorderStroke(2.dp,Color.White.copy(0.4f)),

                contentPadding = PaddingValues(horizontal = 8.dp),


            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "register",
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    tint = Color.White
                )
                Text("Register a New User",
                    color = Color.White
                )
            }



            Spacer(modifier=Modifier.height(10.dp))

            // Search and Filter

            //  Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by name or phone") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Role filter row
            // --- Role Filter Row (Responsive & Scrollable) ---
            val allRoles = listOf("All", "Coil", "Admin", "ScrapCutPieceOutFlow", "Pipe", "PipeOutflow")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()), // make scrollable
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allRoles.forEach { role ->
                    FilterChip(
                        selected = (selectedRole == role || (role == "All" && selectedRole == null)),
                        onClick = {
                            selectedRole = if (role == "All") null else role
                        },
                        label = {
                            Text(
                                role,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = if (isLandscape) 12.sp else 14.sp // dynamic font size
                            )
                        },
                        modifier = Modifier.defaultMinSize(
                            minWidth = 64.dp, // chip min width
                            minHeight = if (isLandscape) 28.dp else 32.dp
                        )
                    )
                }
            }


            // Apply search + role filter
            val filteredUsers = registeredUser.filter { u ->
                val matchesSearch = searchQuery.isBlank() ||
                        (u.name?.contains(searchQuery, ignoreCase = true) == true) ||
                        (u.phone.contains(searchQuery, ignoreCase = true))
                val matchesRole = selectedRole == null || u.roles.any { it.equals(selectedRole, ignoreCase = true) }
                matchesSearch && matchesRole
            }



            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 0.dp),
                // .padding(innerPadding),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(filteredUsers) { user ->

                    RegisterUserCard(

                        user = user,
                        onEditClick = {

                        },
                        onUpdateClick = { selected ->
                            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
                            if (selected.uid == currentUid) {
                                Toast.makeText(context, "You cannot deactivate yourself", Toast.LENGTH_SHORT).show()
                            } else {
                                registeredUserViewmodel.updateUser(selected) { success, error ->
                                    if (success) {
                                        Toast.makeText(context, "User updated", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }

                    )

                }
            }



            // Add User Dialog
            if (showAddUserDialog) {
                var name by remember { mutableStateOf("") }
                var phone by remember { mutableStateOf("") }
                var roles = remember { mutableStateListOf<String>() }
                var secondAuthKey by remember { mutableStateOf("") }
                val allRoles = listOf("Admin", "Coil", "ScrapCutPieceOutFlow", "Pipe", "PipeOutflow")


                AlertDialog(
                    onDismissRequest = { showAddUserDialog = false },
                    confirmButton = {
                        Button(onClick = {
                            val formattedPhone = if (phone.startsWith("+91")) {
                                phone
                            } else {
                                "+91$phone"
                            }
                            val user = RegisteredUser(
                                uid = "", // Firestore will generate UID or set it later
                                name = name.ifBlank { "Unknown" },
                                phone = formattedPhone,
                                roles = roles.toList(),
                                secondAuthKey = secondAuthKey.ifBlank { null }, //  safe convert
                                active = true,
                            )
                            showAddUserDialog = false
                            //showConfirmUpdate = true
                            registeredUserViewmodel.addUser(user)

                        }) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showAddUserDialog = false }) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("Add User") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") },leadingIcon = {
                                Text("+91", color = MaterialTheme.colorScheme.onSurface)
                            },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number
                                )
                                ,)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Assigned Roles:")

                                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    roles.forEach { role ->
                                        AssistChip(
                                            onClick = { roles.remove(role) }, // removes immediately
                                            label = { Text(role) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Remove role",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text("Add Role:")

                                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    allRoles.filter { it !in roles }.forEach { role ->
                                        AssistChip(
                                            onClick = { if (role !in roles) roles.add(role) }, // prevent duplicates
                                            label = { Text(role) }
                                        )
                                    }
                                }
                            }
                            OutlinedTextField(
                                value = secondAuthKey,
                                onValueChange = { secondAuthKey = it },
                                label = { Text("Access key") },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Phone
                                ),
                            )
                        }
                    }
                )
            }

        }

    }
}