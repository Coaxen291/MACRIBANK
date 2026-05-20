package com.utp.macribank.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.utp.macribank.presentation.login.LoginView
import com.utp.macribank.presentation.login.RegisterView
import com.utp.macribank.presentation.feed.FeedView
import com.utp.macribank.presentation.feed.TransferView
import com.utp.macribank.presentation.feed.ReceiveView
import com.utp.macribank.presentation.feed.BillsView
import com.utp.macribank.presentation.feed.MoreView
import com.utp.macribank.presentation.feed.DepositView
import com.utp.macribank.presentation.feed.TransactionDetailView
import com.utp.macribank.presentation.feed.HomeViewModel
import com.utp.macribank.presentation.profile.ProfileView
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginView(navController = navController)
        }

        composable("register") {
            RegisterView(navController = navController)
        }

        composable("feed") {
            FeedView(navController = navController, viewModel = homeViewModel)
        }

        composable("transfer") {
            TransferView(navController = navController)
        }

        composable("receive") {
            ReceiveView(navController = navController, viewModel = homeViewModel)
        }

        composable("bills") {
            BillsView(navController = navController)
        }

        composable("more") {
            MoreView(navController = navController)
        }

        composable("deposit") {
            DepositView(navController = navController)
        }

        composable("profile") {
            ProfileView(navController = navController, viewModel = homeViewModel)
        }

        composable("transactionDetail") {
            homeViewModel.selectedTransaction?.let { transaction ->
                TransactionDetailView(navController = navController, transaction = transaction)
            }
        }
    }
}
