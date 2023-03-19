package com.aidventory.feature.settings

import androidx.annotation.StringRes
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.core.common.designsystem.icon.Icon


enum class SettingCategory(
    @StringRes val textStringRes: Int,
    @StringRes val descriptionStringRes: Int,
    val icon: Icon
) {
    DATA(
        textStringRes = R.string.setting_item_data_text,
        descriptionStringRes = R.string.setting_item_data_description,
        icon = AidventoryIcons.Storage
    ),
    SUPPLY_USES(
        textStringRes = R.string.setting_item_supply_uses_text,
        descriptionStringRes = R.string.setting_item_supply_uses_description,
        icon = AidventoryIcons.Category
    ),
    THEME(
        textStringRes = R.string.setting_item_theme_text,
        descriptionStringRes = R.string.setting_item_theme_description,
        icon = AidventoryIcons.Palette
    ),
    ABOUT(
        textStringRes = R.string.setting_item_about_text,
        descriptionStringRes = R.string.setting_item_about_description,
        icon = AidventoryIcons.Info
    )
}