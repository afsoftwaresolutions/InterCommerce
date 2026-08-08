package com.afsoftwaresolutions.intercommerce.presentation.detail

internal fun prepareProductImageUrls(
    thumbnail: String,
    images: List<String>
): List<String> {
    val normalizedImages = images
        .asSequence()
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    val normalizedThumbnail = thumbnail.trim()

    return buildList {
        if (normalizedThumbnail.isNotEmpty()) {
            add(normalizedThumbnail)
        }
        addAll(normalizedImages)
    }.distinct()
}