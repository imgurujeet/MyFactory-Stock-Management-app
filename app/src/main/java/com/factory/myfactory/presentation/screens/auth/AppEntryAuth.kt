package com.factory.myfactory.presentation.screens.auth


import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.factory.myfactory.core.Constants.OTP_LENGTH
import com.factory.myfactory.core.Constants.OTP_TIMEOUT
import com.factory.myfactory.presentation.screens.auth.viewmodel.AuthState
import com.factory.myfactory.presentation.screens.auth.viewmodel.AuthViewModel
import com.factory.myfactory.presentation.screens.navigations.Screen
import kotlinx.coroutines.delay


@Composable
fun AppEntryAuthScreen(navHost : NavHostController,viewModel: AuthViewModel = hiltViewModel()){

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val isLandscape = screenWidth > screenHeight

    var mobileNumber by rememberSaveable { mutableStateOf("")}
    var otp by rememberSaveable { mutableStateOf("") }
    var timer by rememberSaveable { mutableStateOf(OTP_TIMEOUT) } // 60 seconds
    var canResend by rememberSaveable { mutableStateOf(true) }

    val state by viewModel.authState.collectAsState()

    // React to auth state changes
    LaunchedEffect(state) {
        // Check in-memory cached login first
        if (viewModel.isLoggedIn(context)) {
           // Toast.makeText(context, "Already logged in! Redirecting...", Toast.LENGTH_SHORT).show()
            navHost.navigate(Screen.Login.route) {
                popUpTo(Screen.AppEntryScreen.route) { inclusive = true }
            }
            return@LaunchedEffect
        }

        when (state) {
            is AuthState.Loading -> {
                Toast.makeText(context, "sending otp", Toast.LENGTH_SHORT).show()
            }
            is AuthState.CodeSent -> {
                Toast.makeText(context, "OTP sent successfully!", Toast.LENGTH_SHORT).show()
                timer = OTP_TIMEOUT
                canResend = false
            }
            is AuthState.Success -> {
                Toast.makeText(context, "Verified successfully!", Toast.LENGTH_SHORT).show()
                navHost.navigate(Screen.Login.route) {
                    popUpTo(Screen.AppEntryScreen.route) { inclusive = true }
                }
            }
            is AuthState.Error -> {
                Toast.makeText(context, "Error: ${(state as AuthState.Error).message}", Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }


    // Countdown logic
    LaunchedEffect(key1 = timer) {
        if (timer > 0) {
            delay(1000L)
            timer--
        } else {
            canResend = true
        }
    }

    Scaffold(
        //containerColor = MaterialTheme.colorScheme.background.copy(0.8f),
        content = { innerPadding ->

            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)
                    .windowInsetsPadding(WindowInsets.systemBars),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Box(
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(14.dp))
                        .windowInsetsPadding(
                        WindowInsets.ime.union(WindowInsets.systemBars) //  reacts to keyboard
                    ),

                ){
                    Column (
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){


                    Text(
                        text = "Welcome Back",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                        //.align(Alignment.Center)

                    )
                    Text(
                        text = "Enter your mobile number to verify yourself",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Thin,
                        modifier = Modifier.padding(bottom = 13.dp)

                    )
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { if (it.length <=10 ) mobileNumber =it},
                        label = { Text("Mobile Number")},
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Phone
                        ),
                        leadingIcon = {
                            Text("+91", color =  MaterialTheme.colorScheme.onSurface)
                        },
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                        OutlinedTextField(
                            value = otp,
                            onValueChange = { if (it.length <= 6) otp = it },
                            label = { Text("verification code") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Phone
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        TextButton(
                            onClick = {
                              //  Log.d("AuthDebug", "Sending OTP to $mobileNumber")
                                //Toast.makeText(context, "Sending OTP...", Toast.LENGTH_SHORT).show()
                                viewModel.sendOtp(mobileNumber, context as Activity,context)
//                                timer = OTP_TIMEOUT
//                                canResend = false
                                // Call your resend OTP API here
                            },
                            enabled = mobileNumber.length == 10,
                            modifier = Modifier.padding(vertical = 10.dp).align(Alignment.Start),
                        ) {
                            Text(
                                text = if (canResend) "Send OTP" else "Resend OTP in $timer s",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (canResend && mobileNumber.length == 10) MaterialTheme.colorScheme.primary else Color.Gray,

                            )
                        }
//
                    Button(onClick = {
                       // Log.d("AuthDebug", "Verifying OTP: $otp")
                        Toast.makeText(context, "Verifying OTP...", Toast.LENGTH_SHORT).show()
                        viewModel.verifyOtp(otp,context)
                       // navHost.navigate(Screen.Login.route)

                    },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        enabled = mobileNumber.length == 10 && otp.length == OTP_LENGTH,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (mobileNumber.length == 10) MaterialTheme.colorScheme.primary else Color.Gray,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )


                    ) {
                        Text(
                            "Access Dashboard"
                        )
                    }
                        Spacer(modifier = Modifier.size(16.dp))
                        if (state is AuthState.Error) {
                            Text(
                                text = (state as AuthState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        if (state is AuthState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(top = 16.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "Authorize Personnel only",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }



                }


            }

        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun AppEntryScreenPreview(){
    val navHost = rememberNavController()
    AppEntryAuthScreen(navHost)
}