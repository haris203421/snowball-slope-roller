package com.flurry.snowballslope.domain.usecase

import com.flurry.snowballslope.domain.model.DecorationItem
import com.flurry.snowballslope.domain.model.Snowman

class BuildSnowmanUseCase {
    operator fun invoke(
        hillId: Int,
        runSizes: List<Float>,
        decorations: List<DecorationItem>
    ): Snowman? {
        if (runSizes.size < 3) return null
        val sorted = runSizes.sortedDescending()
        return Snowman(
            hillId = hillId,
            baseDp = sorted[0],
            middleDp = sorted[1],
            headDp = sorted[2],
            decorations = decorations
        )
    }
}
