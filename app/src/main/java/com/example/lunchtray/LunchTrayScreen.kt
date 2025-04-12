/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.ui.AccompanimentMenuScreen
import com.example.lunchtray.ui.CheckoutScreen
import com.example.lunchtray.ui.EntreeMenuScreen
import com.example.lunchtray.ui.OrderViewModel
import com.example.lunchtray.ui.SideDishMenuScreen
import com.example.lunchtray.ui.StartOrderScreen

// TODO: Screen enum
enum class LunchTrayScreen {
    StartOrder,
    EntreeMenu,
    SideDish,
    AccompanistMenu,
    Checkout,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayAppBar() {
    TopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                )
            }
            Spacer(
                modifier = Modifier.height(50.dp)
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        topBar = {
            LunchTrayAppBar()
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        // TODO: Make the button to actually go somewhere.
        Column(
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            NavHost(
                navController = navController,
                startDestination = LunchTrayScreen.StartOrder.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = LunchTrayScreen.StartOrder.name) {
                    StartOrderScreen(
                        onStartOrderButtonClicked = {
                            navController.navigate(LunchTrayScreen.EntreeMenu.name)
                        },
                    )
                }
                composable(route = LunchTrayScreen.EntreeMenu.name) {
                    EntreeMenuScreen(
                        options = DataSource.entreeMenuItems,
                        onCancelButtonClicked = { cancelOrderAndReturnToStart(navController) },
                        onNextButtonClicked = {
                            navController.navigate(LunchTrayScreen.SideDish.name)
                        },
                        onSelectionChanged = { },
                    )
                }
                composable(route = LunchTrayScreen.SideDish.name) {
                    SideDishMenuScreen(
                        options = DataSource.sideDishMenuItems,
                        onCancelButtonClicked = { cancelOrderAndReturnToStart(navController) },
                        onNextButtonClicked = {
                            navController.navigate(LunchTrayScreen.AccompanistMenu.name)
                        },
                        onSelectionChanged = { },
                    )
                }
                composable(route = LunchTrayScreen.AccompanistMenu.name) {
                    AccompanimentMenuScreen(
                        options = DataSource.accompanimentMenuItems,
                        onCancelButtonClicked = { cancelOrderAndReturnToStart(navController) },
                        onNextButtonClicked = { navController.navigate(LunchTrayScreen.Checkout.name) },
                        onSelectionChanged = { },
                    )
                }
                composable(route = LunchTrayScreen.Checkout.name) {
                    CheckoutScreen(
                        orderUiState = uiState,
                        onNextButtonClicked = { },
                        onCancelButtonClicked = { cancelOrderAndReturnToStart(navController) },
                    )
                }
            }
        }
    }
}

private fun cancelOrderAndReturnToStart(
    navController: NavController,
) {
    navController.popBackStack(LunchTrayScreen.StartOrder.name, inclusive = false)
}
