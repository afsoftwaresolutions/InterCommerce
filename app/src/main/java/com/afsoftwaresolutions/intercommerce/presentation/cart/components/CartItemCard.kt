package com.afsoftwaresolutions.intercommerce.presentation.cart.components

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.afsoftwaresolutions.intercommerce.R
import com.afsoftwaresolutions.intercommerce.domain.model.Money
import com.afsoftwaresolutions.intercommerce.presentation.common.formatter.MoneyFormatter
import com.afsoftwaresolutions.intercommerce.presentation.components.shimmer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartItemCard(
    productId: Int,
    title: String,
    thumbnail: String,
    unitPrice: Money,
    quantity: Int,
    stock: Int,
    itemSubtotal: Money?,
    isUpdating: Boolean,
    onProductClick: (Int) -> Unit,
    onIncreaseClick: (Int) -> Unit,
    onDecreaseClick: (Int) -> Unit,
    onRemoveClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val canDecrease = quantity > 1 && !isUpdating
    val canIncrease = quantity < stock && !isUpdating
    val canRemove = !isUpdating
    val quantityStateDescription = stringResource(R.string.quantity_label) + ": $quantity"
    val decreaseDescription = stringResource(R.string.decrease_quantity_description, title)
    val increaseDescription = stringResource(R.string.increase_quantity_description, title)
    val removeDescription = stringResource(R.string.remove_cart_item_description, title)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cart_item_$productId")
            .clickable { onProductClick(productId) }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = thumbnail,
                contentDescription = stringResource(R.string.cart_item_image_description, title),
                modifier = Modifier
                    .height(84.dp)
                    .fillMaxWidth(0.28f)
                    .testTag("cart_item_image_$productId"),
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
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.image_load_error),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = MoneyFormatter.format(unitPrice),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = { onDecreaseClick(productId) },
                        enabled = canDecrease,
                        modifier = Modifier
                            .testTag("decrease_quantity_$productId")
                    ) {
                        Text(
                            text = "-",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.semantics {
                                contentDescription = decreaseDescription
                            }
                        )
                    }

                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .testTag("cart_item_quantity_$productId")
                            .semantics {
                                stateDescription = quantityStateDescription
                            }
                    )

                    IconButton(
                        onClick = { onIncreaseClick(productId) },
                        enabled = canIncrease,
                        modifier = Modifier
                            .testTag("increase_quantity_$productId")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = increaseDescription
                        )
                    }
                }

                if (itemSubtotal != null) {
                    Text(
                        text = MoneyFormatter.format(itemSubtotal),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedButton(
                onClick = { onRemoveClick(productId) },
                enabled = canRemove,
                modifier = Modifier
                    .testTag("remove_cart_item_$productId")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = removeDescription
                )
            }
        }
    }
}