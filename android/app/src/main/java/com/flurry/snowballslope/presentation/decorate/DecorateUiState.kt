package com.flurry.snowballslope.presentation.decorate

import com.flurry.snowballslope.domain.model.DecorationKind
import com.flurry.snowballslope.domain.model.Inventory
import com.flurry.snowballslope.domain.model.SnowAnchor
import com.flurry.snowballslope.domain.model.Snowman

data class TrayEntry(val kind: DecorationKind, val count: Int)

data class DecorateUiState(
    val hillId: Int = 1,
    val hillName: String = "",
    val snowman: Snowman? = null,
    val anchors: List<SnowAnchor> = emptyList(),
    val inventory: Inventory = Inventory(0, 0, 0),
    val placedCount: Int = 0
) {
    val tray: List<TrayEntry>
        get() = DecorationKind.entries
            .map { TrayEntry(it, inventory.countOf(it)) }
            .filter { it.count > 0 }

    val canSave: Boolean get() = snowman != null
    val canClear: Boolean get() = placedCount > 0
}
