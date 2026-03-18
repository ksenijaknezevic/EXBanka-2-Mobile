package rs.raf.exbanka.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import rs.raf.exbanka.mobile.ui.navigation.AppNavGraph
import rs.raf.exbanka.mobile.ui.theme.EXBankaVerificationTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EXBankaVerificationTheme {
                AppNavGraph()
            }
        }
    }
}
