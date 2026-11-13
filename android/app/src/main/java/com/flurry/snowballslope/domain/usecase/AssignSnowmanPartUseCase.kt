package com.flurry.snowballslope.domain.usecase

import com.flurry.snowballslope.core.config.GameConfig
import com.flurry.snowballslope.domain.model.SnowmanPart

class AssignSnowmanPartUseCase {
    operator fun invoke(sizeDp: Float): SnowmanPart = when {
        sizeDp >= GameConfig.BASE_THRESHOLD_DP -> SnowmanPart.Base
        sizeDp >= GameConfig.MIDDLE_THRESHOLD_DP -> SnowmanPart.Middle
        else -> SnowmanPart.Head
    }
}
