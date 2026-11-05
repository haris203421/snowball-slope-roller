package com.flurry.snowballslope.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L

    const val TOTAL_HILLS = 15
    const val RUNS_PER_SNOWMAN = 3

    const val START_SIZE_DP = 28f
    const val MAX_SIZE_DP = 96f
    const val MIN_SIZE_DP = 24f

    const val PATCH_GROWTH_DP = 6f
    const val TREE_SHRINK_DP = 4f
    const val SIZE_STEP_MS = 200

    const val BASE_THRESHOLD_DP = 72f
    const val MIDDLE_THRESHOLD_DP = 48f

    const val RUN_DISTANCE_DP = 2200f
    const val FLATTEN_DISTANCE_DP = 420f
    const val START_SPEED_DP = 220f
    const val END_SPEED_DP = 340f

    const val TRACK_MARGIN_DP = 20f
    const val BALL_SCREEN_RATIO = 0.62f

    const val TILT_ALPHA = 0.15f
    const val TILT_MAX_G = 4.0f
    const val TILT_DEAD_ZONE = 0.25f
    const val LATERAL_SPEED_DP = 260f

    const val FRAME_MS = 16L
    const val COUNTDOWN_STEP_MS = 520L
    const val COUNTDOWN_FROM = 3
    const val BUMP_INVULN_DP = 140f
    const val EFFECT_SPEED = 4.2f

    const val PATCH_GAP_MIN_DP = 210f
    const val PATCH_GAP_MAX_DP = 360f
    const val PINE_GAP_MIN_DP = 340f
    const val PINE_GAP_MAX_DP = 520f
    const val SCARF_GAP_DP = 1150f
    const val HAT_GAP_DP = 1700f

    const val PATCH_SIZE_DP = 56f
    const val PINE_SIZE_DP = 72f
    const val PICKUP_SIZE_DP = 52f

    const val DECORATION_TILE_DP = 72
    const val SNAP_RADIUS = 0.16f
    const val PARENT_GATE_ANSWER = 12
    const val PARENT_GATE_TRIES = 3
}
