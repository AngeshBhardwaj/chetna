package `in`.chetna.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import `in`.chetna.mobile.onboarding.contact.ContactScreen
import `in`.chetna.mobile.onboarding.otp.OtpScreen
import `in`.chetna.mobile.onboarding.signedin.SignedInScreen
import `in`.chetna.mobile.onboarding.welcome.WelcomeScreen

@Composable
fun ChetnaNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Route.Welcome) {
        composable<Route.Welcome> {
            WelcomeScreen(
                onGetStarted = { navController.navigate(Route.Contact()) }
            )
        }
        composable<Route.Contact> {
            ContactScreen(
                onBack = { navController.popBackStack() },
                onContinue = { email -> navController.navigate(Route.Otp(email)) }
            )
        }
        composable<Route.Otp> {
            OtpScreen(
                onBack = { navController.popBackStack() },
                onVerified = {
                    navController.navigate(Route.SignedIn) {
                        popUpTo(Route.Welcome) { inclusive = false }
                    }
                }
            )
        }
        composable<Route.SignedIn> {
            SignedInScreen(
                onLogout = {
                    navController.navigate(Route.Welcome) {
                        popUpTo(Route.Welcome) { inclusive = true }
                    }
                }
            )
        }
    }
}
