package com.calendar

import androidx.compose.ui.window.ComposeUIViewController
import com.calendar.ui.CalendarRoot
import com.calendar.ui.IosPlatform
import com.calendar.ui.UserDefaultsStore
import platform.UIKit.UIViewController

/** [setIcon] recibe el nombre del icono alternativo (`alt_<id>`) y lo aplica desde Swift. */
fun MainViewController(setIcon: (String) -> Unit): UIViewController = ComposeUIViewController {
    CalendarRoot(UserDefaultsStore(), IosPlatform(setIcon))
}
