package com.afsoftwaresolutions.intercommerce.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.afsoftwaresolutions.intercommerce.presentation.cart.CartRoute as CartRouteContent
import com.afsoftwaresolutions.intercommerce.presentation.catalog.CatalogRoute as CatalogRouteContent
import com.afsoftwaresolutions.intercommerce.presentation.detail.ProductDetailRoute as ProductDetailRouteContent
import com.afsoftwaresolutions.intercommerce.presentation.navigation.CatalogRoute as CatalogDestination
import com.afsoftwaresolutions.intercommerce.presentation.navigation.CartRoute as CartDestination
import com.afsoftwaresolutions.intercommerce.presentation.navigation.ProductDetailRoute as ProductDetailDestination

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val cartBadgeViewModel: CartBadgeViewModel = hiltViewModel()
    val cartItemCount by cartBadgeViewModel.cartItemCount.collectAsStateWithLifecycle()

    InterCommerceNavGraph(
        navController = navController,
        catalogDestination = { onProductClick, onCartClick ->
            CatalogRouteContent(
                onProductClick = onProductClick,
                onCartClick = onCartClick,
                cartItemCount = cartItemCount
            )
        },
        detailDestination = { productId, onBackClick, onCartClick ->
            ProductDetailRouteContent(
                onBackClick = onBackClick,
                onCartClick = onCartClick,
                cartItemCount = cartItemCount
            )
        },
        cartDestination = { onBackClick, onContinueShopping, onProductClick ->
            CartRouteContent(
                onBackClick = onBackClick,
                onContinueShopping = onContinueShopping,
                onProductClick = onProductClick
            )
        }
    )
}

@Composable
internal fun InterCommerceNavGraph(
    navController: NavHostController,
    catalogDestination: @Composable (
        onProductClick: (Int) -> Unit,
        onCartClick: () -> Unit
    ) -> Unit,
    detailDestination: @Composable (
        productId: Int,
        onBackClick: () -> Unit,
        onCartClick: () -> Unit
    ) -> Unit,
    cartDestination: @Composable (
        onBackClick: () -> Unit,
        onContinueShopping: () -> Unit,
        onProductClick: (Int) -> Unit
    ) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = CatalogDestination
    ) {
        composable<CatalogDestination> {
            catalogDestination(
                { productId ->
                    navController.navigate(ProductDetailDestination(productId = productId))
                },
                {
                    navController.navigate(CartDestination) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<ProductDetailDestination> { backStackEntry ->
            val args = backStackEntry.toRoute<ProductDetailDestination>()
            detailDestination(
                args.productId,
                { navController.popBackStack() },
                {
                    navController.navigate(CartDestination) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<CartDestination> {
            cartDestination(
                { navController.popBackStack() },
                {
                    if (!navController.popBackStack(route = CatalogDestination, inclusive = false)) {
                        navController.navigate(CatalogDestination) {
                            launchSingleTop = true
                        }
                    }
                },
                { productId ->
                    navController.navigate(ProductDetailDestination(productId = productId))
                }
            )
        }
    }
}