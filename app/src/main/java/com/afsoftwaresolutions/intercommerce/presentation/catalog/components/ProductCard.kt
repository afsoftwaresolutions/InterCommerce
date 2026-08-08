package com.afsoftwaresolutions.intercommerce.presentation.catalog.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.afsoftwaresolutions.intercommerce.R
import com.afsoftwaresolutions.intercommerce.domain.model.Money
import com.afsoftwaresolutions.intercommerce.domain.model.Percentage
import com.afsoftwaresolutions.intercommerce.presentation.common.formatter.MoneyFormatter
import com.afsoftwaresolutions.intercommerce.presentation.common.formatter.PercentageFormatter
import com.afsoftwaresolutions.intercommerce.presentation.components.shimmer
import com.afsoftwaresolutions.intercommerce.ui.theme.InterCommerceTheme
import java.util.Locale

@Composable
fun ProductCard(
    productId: Int,
    title: String,
    thumbnail: String,
    price: Money,
    discount: Percentage,
    rating: Double,
    stock: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageDescription = stringResource(R.string.product_image_description, title)
    val ratingText = String.format(Locale.US, "%.1f", rating)
    val priceText = MoneyFormatter.format(price)
    val discountText = PercentageFormatter.format(discount)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("product_card_$productId")
            .clickable { onClick(productId) }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SubcomposeAsyncImage(
                model = thumbnail,
                contentDescription = imageDescription,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .semantics { contentDescription = imageDescription },
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .shimmer()
                    )
                },
                error = {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = priceText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            if (discount.basisPoints > 0) {
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = {
                        Text(
                            text = stringResource(R.string.product_discount_description, discountText)
                        )
                    }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = stringResource(R.string.product_rating_description, ratingText),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = ratingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val stockText = if (stock == 0) {
                stringResource(R.string.product_out_of_stock)
            } else {
                stringResource(R.string.product_units_available, stock)
            }

            Text(
                text = stockText,
                style = MaterialTheme.typography.bodySmall,
                color = if (stock == 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(name = "ProductCard Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun ProductCardInStockPreview() {
    InterCommerceTheme {
        ProductCard(
            productId = 1,
            title = "Producto 1",
            thumbnail = "",
            price = Money.ofCents(999),
            discount = Percentage.ofBasisPoints(1_048),
            rating = 4.3,
            stock = 12,
            onClick = {}
        )
    }
}

@Preview(name = "ProductCard Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductCardOutOfStockPreview() {
    InterCommerceTheme {
        ProductCard(
            productId = 2,
            title = "Producto 2",
            thumbnail = "",
            price = Money.ofCents(1999),
            discount = Percentage.ZERO,
            rating = 4.0,
            stock = 0,
            onClick = {}
        )
    }
}