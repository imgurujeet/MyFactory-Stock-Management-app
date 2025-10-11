package com.factory.myfactory.presentation.components

import android.webkit.WebSettings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import com.factory.myfactory.presentation.screens.admin.viewmodel.AdminViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.factory.myfactory.R
import com.factory.myfactory.data.models.RegisteredUser
import com.imgurujeet.stockease.data.models.User
import com.imgurujeet.stockease.data.models.dummyUsers

@Composable
fun UserCard(
    user : User,
    userRegistered: RegisteredUser?,
    onEditClick : (User) -> Unit = {},
    onDeleteClick : (User) -> Unit = {}

){

    Card (
        modifier = Modifier
            .fillMaxWidth()
//            .pointerInput(Unit) {
//                detectTapGestures(
//                    onLongPress = {
//                        showDialog = true
//                    }
//                )
//            }
            .padding( top = 4.dp, bottom = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column (modifier = Modifier.padding(horizontal = 10.dp)){
            Row (Modifier.fillMaxWidth().padding(top=16.dp),
                horizontalArrangement = Arrangement.SpaceBetween

            ){
                Text(
                    text = "${user.name}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold

                )
                Text(
                    text = if(userRegistered?.active.toString() == "true") "Active" else " Inactive",

                )


            }
            Text(
                text = "${user.phone}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Active Roles: ${user.roles}"
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp,),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween


            ){
                Text(
                    text = "Last Login: ${user.lastLogin.toDateString()}"
                )

                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ){

                    Box(
                        modifier = Modifier.background(
                            Color(0xAB00A5FF).copy(alpha = 0.1f) , shape = RoundedCornerShape(10.dp)
                        ).clickable(
                            onClick = {
                                onEditClick(user)
                            }
                        )
                        ,
                        contentAlignment = Alignment.Center,

                        ){
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = "Edit Icon",

                            tint = Color(0xFF00A5FF),
                            modifier = Modifier.padding(6.dp).size(24.dp)
                        )
                    }
                    //Spacer(modifier = Modifier.padding(10.dp))
                    Box(
                        modifier = Modifier.background(
                            Color.Red.copy(alpha = 0.1f) , shape = RoundedCornerShape(10.dp)
                        ).clickable(
                            onClick = {
                                onDeleteClick(user)
                            }
                        )

                        ,
                        contentAlignment = Alignment.Center,

                        ){
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = "Edit Icon",
                            tint = Color.Red.copy(alpha = 0.4f),
                            modifier = Modifier.padding(6.dp)
                        )
                    }

                }

            }



        }

    }

}



@Composable
fun RegisterUserCard(
    user : RegisteredUser,
    onEditClick : (RegisteredUser) -> Unit = {},
    onUpdateClick : (RegisteredUser) -> Unit = {}

){

    Card (
        modifier = Modifier
            .fillMaxWidth()
//            .pointerInput(Unit) {
//                detectTapGestures(
//                    onLongPress = {
//                        showDialog = true
//                    }
//                )
//            }
            .padding( top = 4.dp, bottom = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column (modifier = Modifier.padding(horizontal = 10.dp)){
            Row (Modifier.fillMaxWidth().padding(top=16.dp),
                horizontalArrangement = Arrangement.SpaceBetween

            ){
                Text(
                    text = "${user.name}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold

                )
                Text(
                    text = if(user.active == true) "Active" else " Inactive",

                    )


            }
            Text(
                text = "${user.phone}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Initial Roles: ${user.roles}"
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp,),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween


            ){


                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ){

                    Box(
                        modifier = Modifier.background(
                            Color(0xAB00A5FF).copy(alpha = 0.1f) , shape = RoundedCornerShape(10.dp)
                        ).clickable(
                            onClick = {
                                onEditClick(user)
                            }
                        )
                        ,
                        contentAlignment = Alignment.Center,

                        ){
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = "Edit Icon",

                            tint = Color(0xFF00A5FF),
                            modifier = Modifier.padding(6.dp).size(24.dp)
                        )
                    }
                    //Spacer(modifier = Modifier.padding(10.dp))
                    Box(
                        modifier = Modifier.background(
                            if (user.active== true) Color.Red.copy(alpha = 0.1f) else Color.Green.copy(alpha = 0.1f) , shape = RoundedCornerShape(10.dp)
                        ).clickable(
                            onClick = {
                                onUpdateClick(user)
                            }
                        )

                        ,
                        contentAlignment = Alignment.Center,

                        ){
                        Row( modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                            ){
                            Text(
                                text = if (user.active== true) "Deactivate" else "Activate"
                            )

                            Icon(
                                painter = painterResource(R.drawable.ic_logout),
                                contentDescription = "Edit Icon",
                                tint = if (user.active== true) Color.Red.copy(alpha = 0.4f) else Color.Green.copy(alpha = 0.4f),
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }

                }

            }



        }

    }

}


