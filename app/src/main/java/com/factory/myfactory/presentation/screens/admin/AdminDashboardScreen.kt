package com.factory.myfactory.presentation.screens.admin

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.factory.myfactory.presentation.components.TableViewCard
import com.factory.myfactory.presentation.components.UserCard
import com.factory.myfactory.presentation.screens.admin.viewmodel.AdminViewModel
import com.factory.myfactory.presentation.screens.auth.getRoleIcon
import com.imgurujeet.stockease.data.models.dummyUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import com.factory.myfactory.R
import com.factory.myfactory.data.models.RegisteredUser
import com.factory.myfactory.presentation.screens.admin.viewmodel.RegisteredUserViewmodel
import com.factory.myfactory.presentation.screens.navigations.Screen
import com.imgurujeet.stockease.data.models.User


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminDashboardScreen(navHost: NavHostController,onBack : () -> Unit,adminViewModel: AdminViewModel = hiltViewModel(),registeredUserViewmodel: RegisteredUserViewmodel = hiltViewModel()){

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = screenWidth > screenHeight
    val user by adminViewModel.users.collectAsState()
    val userRegistered by registeredUserViewmodel.registeredUser.collectAsState()
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showConfirmUpdate by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf<String?>(null) } // null = no filter


    Scaffold (
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp),

            ){
                Row(

                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,

                ){
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        "Back arrow",
                        Modifier.clickable(onClick = onBack)
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = "Factory Admin",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = if(isLandscape) (screenHeight.value * 0.06f).sp else (screenWidth.value * 0.06f).sp, // dynamic font size
                        fontWeight = FontWeight.Medium,
                       // modifier = Modifier.align(Alignment.Center)

                    )

                }

                OutlinedButton(
                    onClick = {
                        navHost.navigate(Screen.RegisteredUsersScreen.route)
                       // showAddUserDialog = true

                    },
                    Modifier.align(Alignment.CenterEnd).heightIn(min = 45.dp, max = 55.dp), // adjusts height for small/big screens
                    shape = RoundedCornerShape(10.dp),


                    contentPadding = PaddingValues(horizontal = 8.dp)

                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "register",
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        //tint = Color.White
                    )
                    Text("Register User",
                       // color = Color.White
                        )
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(innerPadding)
            //.verticalScroll(rememberScrollState())
        ){

            // total user and active user card
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(if(isLandscape) screenWidth*0.1f else screenHeight * 0.1f),
                    shape = RoundedCornerShape(10.dp),
                ){
                    Row(
                        Modifier.fillMaxSize().padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier.background(
                                Color(0xA868FF6E).copy(alpha = 0.1f) , shape = RoundedCornerShape(10.dp)
                            ) .size(if (isLandscape) screenWidth * 0.05f else screenHeight * 0.05f),
                            contentAlignment = Alignment.Center,

                            ){
                            Icon(
                                painter = painterResource(R.drawable.ic_users),
                                contentDescription = "users Icon",

                                tint = Color(0xEB008D05),
                                modifier = Modifier.padding(6.dp).size(30.dp)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f),
                            verticalArrangement = Arrangement.Center, // center vertically
                        ) {
                            Text(
                                text = "${user.size}",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Total Users",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Thin
                            )
                        }

                    }

                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(if(isLandscape) screenWidth*0.1f else screenHeight * 0.1f),
                    shape = RoundedCornerShape(10.dp),

                    ){ Row(
                    Modifier.fillMaxSize().padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){

                    Box(
                        modifier = Modifier.background(
                            Color(0xAB00A5FF).copy(alpha = 0.1f) , shape = RoundedCornerShape(10.dp)
                        ).size(if (isLandscape) screenWidth * 0.05f else screenHeight * 0.05f),
                        contentAlignment = Alignment.Center,


                        ){
                        Icon(
                            painter = painterResource(R.drawable.ic_active),
                            contentDescription = "active Icon",

                            tint = Color(0xFF00A5FF),
                            modifier = Modifier.padding(6.dp).size(30.dp).size(if (isLandscape) screenWidth * 0.03f else screenHeight * 0.03f)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        verticalArrangement = Arrangement.Center, // center vertically
                       // horizontalAlignment = Alignment.CenterHorizontally // center horizontally
                    ){
                        Text(
                            text = "${user.count { it.active == true }}",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Active users" ,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Thin
                        )
                    }



                }


                }


            }

            // total user and active user  section end

            Spacer(modifier=Modifier.height(10.dp))

            // Search and Filter

            //  Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().height(if (isLandscape) screenWidth * 0.06f else screenHeight * 0.06f),
                placeholder = { Text("Search by name or phone") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null,
                    modifier = Modifier.size(if (isLandscape) 20.dp else 24.dp),
                    ) },
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
            val filteredUsers = user.filter { u ->
                val matchesSearch = searchQuery.isBlank() ||
                        (u.name?.contains(searchQuery, ignoreCase = true) == true) ||
                        (u.phone.contains(searchQuery, ignoreCase = true))
                val matchesRole = selectedRole == null || u.roles.any { it.equals(selectedRole, ignoreCase = true) }
                matchesSearch && matchesRole
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 0.dp),
                   // .padding(innerPadding),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredUsers) { user ->
                    val regUser: RegisteredUser? = userRegistered.firstOrNull { it.phone == user.phone }

                    UserCard(
                        user = user,
                        userRegistered = regUser,
                        onEditClick = {
                            selectedUser = it
                            showEditDialog = true
                        },
                        onDeleteClick = {
                            selectedUser = it
                            showDeleteDialog = true
                        }

                    )

                }
            }






            // Edit Dialog
            if (showEditDialog && selectedUser != null) {
                var name by remember { mutableStateOf(selectedUser!!.name ?: "") }
                var phone by remember { mutableStateOf(selectedUser!!.phone) }
                var secondAuthKey by remember { mutableStateOf(selectedUser!!.secondAuthKey) }
                val roles = remember { mutableStateListOf(*selectedUser!!.roles.toTypedArray()) } // FIX
                val allRoles = listOf("Admin", "Coil", "ScrapCutPieceOutFlow", "Pipe","PipeOutflow")

                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    confirmButton = {
                        Button(onClick = {
                            selectedUser = selectedUser!!.copy(
                                name = name,
                                phone = phone,
                                roles = roles.toList(), // convert back to normal list
                                secondAuthKey = secondAuthKey,
                            )
                            showEditDialog = false
                            showConfirmUpdate = true
                        }) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showEditDialog = false }) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("Edit User") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                            OutlinedTextField(
                                value = phone,
                                onValueChange = {  },
                                label = { Text("Phone") },
                                enabled = false,
                                colors = TextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface, // text color
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), // label color
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                ),
                            )

                            Text("Roles:")

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
                                value = secondAuthKey.toString(),
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


            // Confirm Update Dialog
            if (showConfirmUpdate && selectedUser != null) {
                AlertDialog(
                    onDismissRequest = { showConfirmUpdate = false },
                    confirmButton = {
                        Button(onClick = {
                            adminViewModel.updateUser(selectedUser!!)
                            showConfirmUpdate = false
                        }) { Text("Confirm") }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showConfirmUpdate = false }) { Text("Cancel") }
                    },
                    title = { Text("Confirm Update") },
                    text = { Text("Are you sure you want to update this user?") }
                )
            }

            // Delete Dialog
            if (showDeleteDialog && selectedUser != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    confirmButton = {
                        Button(onClick = {
                            adminViewModel.deleteUser(selectedUser!!.uid)
                            showDeleteDialog = false
                        }) { Text("Delete") }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                    },
                    title = { Text("Confirm Delete") },
                    text = { Text("Are you sure you want to delete ${selectedUser!!.name}?") }
                )
            }




        }

    }
}


@Preview(showSystemUi = true)
@Composable
fun AdminDashboardPreview(){
    val navHost = rememberNavController()
    AdminDashboardScreen(navHost, onBack = {})
}


