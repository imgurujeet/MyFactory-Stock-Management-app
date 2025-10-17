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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.factory.myfactory.R
import com.factory.myfactory.data.models.RegisteredUser
import com.imgurujeet.stockease.data.models.User
import com.imgurujeet.stockease.data.models.dummyUsers

@Composable
fun UserCard(
    user: User,
    userRegistered: RegisteredUser?,
    onEditClick: (User) -> Unit = {},
    onDeleteClick: (User) -> Unit = {}
) {


    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = screenWidth > screenHeight

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isLandscape) 2.dp else 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = if (isLandscape) 6.dp else 10.dp, vertical = 8.dp)) {

            // User Name & Status
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${user.name}",
                    fontSize = if (isLandscape) 14.sp else 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (userRegistered?.active.toString() == "true") "Active" else "Inactive",
                    fontSize = if (isLandscape) 12.sp else 14.sp,
                    color = if (userRegistered?.active.toString() == "true") Color(0xFF00A5FF) else Color.Gray
                )
            }

            // Phone
            Text(
                text = user.phone,
                fontSize = if (isLandscape) 12.sp else 15.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Roles
            Text(
                text = "Active Roles: ${user.roles}",
                fontSize = if (isLandscape) 12.sp else 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Last Login & Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = if (isLandscape) 4.dp else 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Last Login: ${user.lastLogin.toDateString()}",
                    fontSize = if (isLandscape) 10.sp else 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if (isLandscape) 8.dp else 12.dp)
                ) {
                    // Edit Button
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xAB00A5FF).copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onEditClick(user) }
                            .padding(if (isLandscape) 4.dp else 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = "Edit Icon",
                            tint = Color(0xFF00A5FF),
                            modifier = Modifier.size(if (isLandscape) 18.dp else 24.dp)
                        )
                    }

                    // Delete Button
                    Box(
                        modifier = Modifier
                            .background(
                                Color.Red.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onDeleteClick(user) }
                            .padding(if (isLandscape) 4.dp else 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = "Delete Icon",
                            tint = Color.Red.copy(alpha = 0.4f),
                            modifier = Modifier.size(if (isLandscape) 18.dp else 22.dp)
                        )
                    }
                }
            }
        }
    }
}




@Composable
fun RegisterUserCard(
    user: RegisteredUser,
    onEditClick: (RegisteredUser) -> Unit = {},
    onUpdateClick: (RegisteredUser) -> Unit = {}
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = screenWidth > screenHeight

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isLandscape) 2.dp else 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = if (isLandscape) 6.dp else 10.dp, vertical = 8.dp)) {

            // Name & Status
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${user.name}",
                    fontSize = if (isLandscape) 14.sp else 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (user.active == true) "Active" else "Inactive",
                    fontSize = if (isLandscape) 12.sp else 14.sp,
                    color = if (user.active == true) Color(0xFF00A5FF) else Color.Gray
                )
            }

            // Phone
            Text(
                text = user.phone,
                fontSize = if (isLandscape) 12.sp else 15.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Roles
            Text(
                text = "Initial Roles: ${user.roles}",
                fontSize = if (isLandscape) 12.sp else 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = if (isLandscape) 4.dp else 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(if (isLandscape) 8.dp else 12.dp)
            ) {
                // Edit Button
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xAB00A5FF).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onEditClick(user) }
                        .padding(if (isLandscape) 4.dp else 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit Icon",
                        tint = Color(0xFF00A5FF),
                        modifier = Modifier.size(if (isLandscape) 18.dp else 24.dp)
                    )
                }

                // Activate / Deactivate Button
                Box(
                    modifier = Modifier
                        .background(
                            if (user.active == true) Color.Red.copy(alpha = 0.1f) else Color.Green.copy(
                                alpha = 0.1f
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onUpdateClick(user) }
                        .padding(if (isLandscape) 4.dp else 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (user.active == true) "Deactivate" else "Activate",
                            fontSize = if (isLandscape) 10.sp else 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            painter = painterResource(R.drawable.ic_logout),
                            contentDescription = "Status Icon",
                            tint = if (user.active == true) Color.Red.copy(alpha = 0.4f) else Color.Green.copy(alpha = 0.4f),
                            modifier = Modifier
                                .padding(start = if (isLandscape) 2.dp else 6.dp)
                                .size(if (isLandscape) 16.dp else 20.dp)
                        )
                    }
                }
            }
        }
    }
}


