package com.afsoftwaresolutions.intercommerce.presentation.catalog.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.afsoftwaresolutions.intercommerce.presentation.components.shimmer
import com.afsoftwaresolutions.intercommerce.ui.theme.AppShapes

@Composable
fun ProductCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("product_skeleton")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(AppShapes.small)
                    .shimmer()
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(16.dp)
                    .clip(AppShapes.extraSmall)
                    .shimmer()
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(14.dp)
                    .clip(AppShapes.extraSmall)
                    .shimmer()
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
                    .clip(AppShapes.extraSmall)
                    .shimmer()
            )
        }
    }
}