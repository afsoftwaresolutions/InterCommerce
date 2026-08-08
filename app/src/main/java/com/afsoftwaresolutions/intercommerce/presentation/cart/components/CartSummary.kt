package com.afsoftwaresolutions.intercommerce.presentation.cart.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.afsoftwaresolutions.intercommerce.R
import com.afsoftwaresolutions.intercommerce.domain.model.CartTotals
import com.afsoftwaresolutions.intercommerce.presentation.common.formatter.MoneyFormatter

@Composable
fun CartSummary(
    totals: CartTotals,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cart_summary")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryRow(
                label = stringResource(R.string.subtotal_label),
                value = MoneyFormatter.format(totals.subtotal),
                tag = "cart_subtotal"
            )
            SummaryRow(
                label = stringResource(R.string.discount_label),
                value = MoneyFormatter.format(totals.discount),
                tag = "cart_discount"
            )
            SummaryRow(
                label = stringResource(R.string.taxes_label),
                value = MoneyFormatter.format(totals.tax),
                tag = "cart_taxes"
            )
            SummaryRow(
                label = stringResource(R.string.total_label),
                value = MoneyFormatter.format(totals.total),
                tag = "cart_total",
                emphasize = true
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    tag: String,
    emphasize: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (emphasize) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = if (emphasize) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}