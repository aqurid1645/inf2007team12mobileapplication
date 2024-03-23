//package com.inf2007team12mobileapplication.presentation.profile
//
//import android.widget.Toast
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowDropDown
//import androidx.compose.material3.Button
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.DropdownMenu
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.google.firebase.auth.FirebaseAuth
//
//@Composable
//fun CreateProfileScreen(navController: NavController, viewModel: ProfileScreenViewModel = hiltViewModel()) {
//    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
//    val userProfile by viewModel.getUserProfile(userId).observeAsState(UserProfile())
//    val context = LocalContext.current
//
//    var name by remember { mutableStateOf(userProfile.name) }
//    var bio by remember { mutableStateOf(userProfile.bio) }
//    var contactNumber by remember { mutableStateOf(userProfile.contactNumber) }
//    var role by remember { mutableStateOf(userProfile.role) }
//    var expanded by remember { mutableStateOf(false) }
//    val roleOptions = listOf("Lecturer", "Student")
//    var isEditingAnyField by remember { mutableStateOf(false) }
//
//
//
//    LaunchedEffect(userProfile) {
//        name = userProfile.name
//        bio = userProfile.bio
//        contactNumber = userProfile.contactNumber
//        role = userProfile.role
//    }
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        ProfileInfoItem(
//            label = "Name",
//            value = name,
//            onValueChange = { name = it },
//            onEditingChanged = { isEditingAnyField = it }
//        )
//
//        ProfileInfoItem(
//            label = "Bio",
//            value = bio,
//            onValueChange = { bio = it },
//            onEditingChanged = { isEditingAnyField = it }
//        )
//
//        ProfileInfoItem(
//            label = "Contact Number",
//            value = contactNumber,
//            onValueChange = { contactNumber = it },
//            validate = ::isValidSingaporeContact,
//            onEditingChanged = { isEditingAnyField = it }
//        )
//        RoleDropdown(
//            role = role,
//            onRoleSelected = { role = it },
//            expanded = expanded,
//            onExpandedChange = { expanded = it },
//            roleOptions = roleOptions
//        )
//
//        Spacer(modifier = Modifier.padding(vertical = 8.dp))
//
//        Button(
//            onClick = {
//                if (!isEditingAnyField && isValidSingaporeContact(contactNumber)) {
//                    viewModel.isContactNumberTaken(userId, contactNumber) { isTaken ->
//                        if (!isTaken) {
//                            viewModel.updateUserProfile(
//                                userId,
//                                UserProfile(name, bio, contactNumber, role)
//                            ) {
//                                Toast.makeText(
//                                    context,
//                                    "Profile details created successfully",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                navController.navigate("camera") {
//                                    // Clear back stack
//                                    popUpTo("createProfileScreen") { inclusive = true }
//                                }
//
//                            }
//                        } else {
//                            Toast.makeText(
//                                context,
//                                "Contact number is already taken.",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                } else {
//                    Toast.makeText(
//                        context,
//                        "Please finish editing before saving",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            },
//            modifier = Modifier.fillMaxWidth(),
//            enabled = !isEditingAnyField
//        ) {
//            Text("Create Profile")
//        }
//        Spacer(modifier = Modifier.padding(vertical = 8.dp))
//    }
//}
//
//
//@Composable
//fun RoleDropdown(
//    role: String,
//    onRoleSelected: (String) -> Unit,
//    expanded: Boolean,
//    onExpandedChange: (Boolean) -> Unit,
//    roleOptions: List<String>
//) {
//    Column(modifier = Modifier.fillMaxWidth()) {
//        OutlinedTextField(
//            value = role.ifEmpty { "Select Role" },
//            onValueChange = { },
//            readOnly = true,
//            label = { Text("Role") },
//            trailingIcon = {
//                Icon(
//                    imageVector = Icons.Filled.ArrowDropDown,
//                    contentDescription = "Dropdown Icon",
//                    Modifier.clickable { onExpandedChange(true) } // Only open the dropdown, don't toggle
//                )
//            },
//            modifier = Modifier.fillMaxWidth()
//        )
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { onExpandedChange(false) },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            roleOptions.forEach { selection ->
//                DropdownMenuItem(
//                    onClick = {
//                        onRoleSelected(selection)
//                        onExpandedChange(false)
//                    },
//                    text = { Text(selection) }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun ProfileInfoItem(
//    label: String,
//    value: String,
//    onValueChange: (String) -> Unit,
//    validate: (String) -> Boolean = { true },
//    onEditingChanged: (Boolean) -> Unit // New parameter to report back editing state
//) {
//    var editing by remember { mutableStateOf(false) }
//    var text by remember { mutableStateOf(value) }
//
//    LaunchedEffect(value) {
//        text = value
//    }
//
//    LaunchedEffect(editing) {
//        onEditingChanged(editing) // Report editing state back to the parent
//    }
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp)
//            .clickable { editing = true }, // Apply clickable here to make the whole card clickable
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(text = label, style = MaterialTheme.typography.labelLarge)
//            if (editing) {
//                OutlinedTextField(
//                    value = text,
//                    onValueChange = {
//                        text = it // Update text regardless of validation
//                    },
//                    label = { Text(label) },
//                    singleLine = true,
//                    isError = !validate(text) && text.isNotEmpty(), // Check validation but allow empty input for correction
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Button(onClick = {
//                    if (validate(text) || text.isEmpty()) { // Allow saving if valid or empty (assuming you want to allow clearing the field)
//                        onValueChange(text)
//                        editing = false
//                    }
//                }) {
//                    Text("Done")
//                }
//            } else {
//                Text(text = value, style = MaterialTheme.typography.bodyLarge)
//            }
//        }
//    }
//}
//
//fun isValidSingaporeContact(contact: String): Boolean {
//    return contact.matches(Regex("^\\+65\\d{8}$"))
//}
//
//
//fun isValidInternationalContact(contact: String): Boolean {
//    return contact.matches(Regex("^\\+?[1-9]\\d{1,14}$"))
//}
