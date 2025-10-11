package com.factory.myfactory.presentation.screens.auth

import android.content.Context
import android.graphics.drawable.Icon
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.factory.myfactory.R
import com.factory.myfactory.data.repositories.AuthRepository
import com.factory.myfactory.presentation.screens.navigations.UserRole
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.factory.myfactory.presentation.screens.auth.viewmodel.AuthState
import com.factory.myfactory.presentation.screens.auth.viewmodel.AuthViewModel
import com.factory.myfactory.presentation.screens.navigations.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navHost: NavHostController,
    onLoginSuccess: (UserRole) -> Unit,
    //authViewModel: AuthViewModel, context: Context,

) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = hiltViewModel()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var loading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var showSecondAuthDialog by remember { mutableStateOf(false) }
    var secondAuthKey by remember { mutableStateOf<String?>(null) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false)}

    // Fetch roles from ViewModel
    val roles by authViewModel.roles.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val scope = rememberCoroutineScope()

    //  Observe role changes if user is logged in
    uid?.let {
        LaunchedEffect(it) {
            authViewModel.startObservingRoles(it)
        }
    }

    //  React to logout or null user
    LaunchedEffect(uid, authState) {
        if (uid == null || (authState is AuthState.Success && (authState as AuthState.Success).message.contains("Logged out", ignoreCase = true))) {
            authViewModel.clearCache(context) // Clear any cached login/roles
            navHost.navigate(Screen.AppEntryScreen.route) {
                popUpTo(Screen.AppEntryScreen.route) { inclusive = true }
            }
        }
    }


    if (roles.isEmpty()) {
        CircularProgressIndicator()
        return
    }

//    if (loading) {
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            CircularProgressIndicator()
//        }
//        return
//    }

    if (!errorMsg.isNullOrEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = errorMsg!!, color = MaterialTheme.colorScheme.error)
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Login as",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            roles.forEach { role ->
                RoleCard(
                    role = role,
                    icon = {
                        Icon(
                            painter = painterResource(id = getRoleIcon(role)),
                            contentDescription = role,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    onClick = {
                        selectedRole = role
                        // Always fetch the stored second auth key

                        uid?.let {safeUid ->
                            authViewModel.getSecondAuthKey(safeUid,role) { key ->
                                secondAuthKey = key
                                showSecondAuthDialog = true
                            }

                        }

                    }
                )
            }


        }

        Box(
            Modifier.align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 16.dp)
                .clickable(
                    onClick = {
                        showLogoutConfirmDialog = true

                    }
                ),
            contentAlignment = Alignment.Center

        ){
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    painter = painterResource(R.drawable.ic_logout),
                    contentDescription = "Logout Button",
                    tint = Color.Red.copy(alpha = 0.6f)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text="Logout"
                )

            }
        }

    }

    // Show second auth dialog if needed
    if (showSecondAuthDialog && selectedRole != null) {
        SecondAuthDialog(
            onSubmit = { input ->

                uid?.let { safeUid->
                    // Verify the key using ViewModel
                    authViewModel.verifyRoleAccess(safeUid, selectedRole!!, input) { success, msg ->
                        if (success) {
                            showSecondAuthDialog = false
                            onLoginSuccess(UserRole.valueOf(selectedRole!!))
                        } else {
                            Toast.makeText(context, msg ?: "Incorrect key", Toast.LENGTH_SHORT).show()
                            // Keep the dialog open if incorrect
                            showSecondAuthDialog = true
                        }
                    }

                }

            },
            onCancel = {
                showSecondAuthDialog = false
            }
        )
    }

    // logout Confirm Dialog

    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                showLogoutConfirmDialog = false
            },
            title = { Text(text = "Confirm Logout") },
            text = { Text("Are you sure you want to Logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirmDialog = false
                        authViewModel.logout(context)
                        Toast.makeText(context, "You have successfully logged out", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirmDialog = false
                    }
                ) {
                    Text("No")
                }
            }
        )
    }
}

// Map role name to icon resource
fun getRoleIcon(role: String): Int {
    return when (role.lowercase()) {
        "admin" -> R.drawable.ic_admin
        "coil" -> R.drawable.ic_coil
        "pipe" -> R.drawable.ic_pipe
        "scrap" -> R.drawable.ic_scrap
        "cut piece" -> R.drawable.ic_cut_piece
        else -> R.drawable.add_stock
    }
}

@Composable
fun SecondAuthDialog(onSubmit: (String) -> Unit,onCancel:(String)->Unit) {
    var input by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            onCancel
        },
        title = { Text("Enter Second Auth Key") },
        text = {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Second Key") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
            )
        },
        confirmButton = {
            Button(onClick = { onSubmit(input) }) { Text("Submit") }
        },
        dismissButton = {
            Button(onClick = { onCancel(String())  }) { Text("Cancel") }
        }
    )
}



@Composable
fun RoleCard(role: String,icon: @Composable () -> Unit = {},onClick: () -> Unit){

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // thin card
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                    )
                )
            ).clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically, // Align text and icon vertically
            horizontalArrangement = Arrangement.Center // Center the entire row horizontally
        ) {
            icon() // Your icon composable
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = role,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.align(Alignment.CenterVertically) // Ensure text aligns with icon center
            )
            Spacer(modifier = Modifier.weight(1f)) // Pushes the arrow to the end


            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
//                modifier = Modifier
//                    .clickable { onClick() }
            )
        }
    }

}
