package com.aidventory.core.common.designsystem.icon

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.aidventory.core.R

object AidventoryIcons {
    val Home = Icon.DrawableResourceIcon(R.drawable.ic_home)
    val HomeBorder = Icon.DrawableResourceIcon(R.drawable.ic_home_border)
    val Scanner = Icon.DrawableResourceIcon(R.drawable.ic_qr_code_scanner)
    val Delete = Icon.DrawableResourceIcon(R.drawable.ic_delete)
    val DeleteBorder = Icon.DrawableResourceIcon(R.drawable.ic_delete_border)
    val Search = Icon.ImageVectorIcon(Icons.Default.Search)
    val SettingsBorder = Icon.DrawableResourceIcon(R.drawable.ic_settings_border)
    val Add = Icon.ImageVectorIcon(Icons.Default.Add)
    val List = Icon.ImageVectorIcon(Icons.Default.List)
    val ArrowBack = Icon.ImageVectorIcon(Icons.Default.ArrowBack)
    val Share = Icon.DrawableResourceIcon(R.drawable.ic_qr_code)
    val Close = Icon.ImageVectorIcon(Icons.Default.Close)
    val FlashOn = Icon.DrawableResourceIcon(R.drawable.ic_flash_on)
    val FlashOff = Icon.DrawableResourceIcon(R.drawable.ic_flash_off)
    val Sort = Icon.DrawableResourceIcon(R.drawable.ic_sort)
    val Filter = Icon.DrawableResourceIcon(R.drawable.ic_filter)
    val ArrowForward = Icon.ImageVectorIcon(Icons.Default.ArrowForward)
    val MoveUp = Icon.DrawableResourceIcon(R.drawable.ic_move_up)
    val Palette = Icon.DrawableResourceIcon(R.drawable.ic_palette)
    val Info = Icon.ImageVectorIcon(Icons.Outlined.Info)
    val Storage = Icon.DrawableResourceIcon(R.drawable.ic_database)
    val Category = Icon.DrawableResourceIcon(R.drawable.ic_outline_label)
    val Save = Icon.DrawableResourceIcon(R.drawable.ic_save)
    val Send = Icon.DrawableResourceIcon(R.drawable.ic_send)
}


sealed class Icon {

    @get:Composable
    abstract val imageVector: ImageVector

    data class ImageVectorIcon(override val imageVector: ImageVector) : Icon()

    data class DrawableResourceIcon(@DrawableRes val id: Int) : Icon() {
        override val imageVector: ImageVector
            @Composable get() = ImageVector.vectorResource(id)
    }
}