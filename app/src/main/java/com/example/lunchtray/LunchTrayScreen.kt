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

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.ui.AccompanimentMenuScreen
import com.example.lunchtray.ui.CheckoutScreen
import com.example.lunchtray.ui.EntreeMenuScreen
import com.example.lunchtray.ui.OrderViewModel
import com.example.lunchtray.ui.SideDishMenuScreen
import com.example.lunchtray.ui.StartOrderScreen

enum class LunchTrayScreen(@StringRes val screenName: Int) {
    StartOrder(screenName = R.string.start_order),
    EntreeMenu(screenName = R.string.choose_entree),
    SideDish(screenName = R.string.choose_side_dish),
    AccompanistMenu(screenName = R.string.choose_accompaniment),
    Checkout(screenName = R.string.order_checkout),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayAppBar(
    currentScreen: LunchTrayScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (canNavigateBack) {
                    IconButton(
                        onClick = navigateUp,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                }
                Text(
                    text = stringResource(id = currentScreen.screenName),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = LunchTrayScreen.valueOf(backStackEntry?.destination?.route ?: LunchTrayScreen.StartOrder.name)

    Scaffold(
        topBar = {
            LunchTrayAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = {
                    navController.navigateUp()
                }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

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
                        onCancelButtonClicked = { cancelOrderAndReturnToStart(
                            navController,
                            viewModel = viewModel
                        ) },
                        onNextButtonClicked = {
                            navController.navigate(LunchTrayScreen.SideDish.name)
                        },
                        onSelectionChanged = {
                            viewModel.updateEntree(it)
                        },
                    )
                }
                composable(route = LunchTrayScreen.SideDish.name) {
                    SideDishMenuScreen(
                        options = DataSource.sideDishMenuItems,
                        onCancelButtonClicked = { cancelOrderAndReturnToStart(
                            navController,
                            viewModel = viewModel
                        ) },
                        onNextButtonClicked = {
                            navController.navigate(LunchTrayScreen.AccompanistMenu.name)
                        },
                        onSelectionChanged = {
                            viewModel.updateSideDish(it)
                        },
                    )
                }
                composable(route = LunchTrayScreen.AccompanistMenu.name) {
                    AccompanimentMenuScreen(
                        options = DataSource.accompanimentMenuItems,
                        onCancelButtonClicked = { cancelOrderAndReturnToStart(
                            navController,
                            viewModel = viewModel
                        ) },
                        onNextButtonClicked = { navController.navigate(LunchTrayScreen.Checkout.name) },
                        onSelectionChanged = {
                            viewModel.updateAccompaniment(it)
                        },
                    )
                }
                composable(route = LunchTrayScreen.Checkout.name) {
                    CheckoutScreen(
                        orderUiState = uiState,
                        onNextButtonClicked = {  cancelOrderAndReturnToStart(
                            navController,
                            viewModel = viewModel
                        ) },
                        onCancelButtonClicked = { cancelOrderAndReturnToStart(
                            navController,
                            viewModel = viewModel
                        ) },
                    )
                }
            }
        }
    }
}

private fun cancelOrderAndReturnToStart(
    navController: NavController,
    viewModel: OrderViewModel
) {
    viewModel.resetOrder()
    navController.popBackStack(LunchTrayScreen.StartOrder.name, inclusive = false)
}
