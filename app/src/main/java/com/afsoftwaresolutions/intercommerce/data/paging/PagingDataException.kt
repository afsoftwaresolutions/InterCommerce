package com.afsoftwaresolutions.intercommerce.data.paging

import com.afsoftwaresolutions.intercommerce.domain.error.DataError
import java.io.IOException

class PagingDataException(
    val error: DataError
) : IOException()