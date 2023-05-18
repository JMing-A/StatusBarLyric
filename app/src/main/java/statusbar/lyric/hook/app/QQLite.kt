package statusbar.lyric.hook.app

import android.content.Context
import de.robv.android.xposed.XposedHelpers
import statusbar.lyric.utils.LogUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.ktx.findClass
import statusbar.lyric.utils.ktx.hookAfterConstructor
import statusbar.lyric.utils.ktx.hookBeforeMethod
import statusbar.lyric.utils.ktx.isNull
import statusbar.lyric.utils.ktx.setReturnConstant

class QQLite {
    fun hook(name: String): Boolean {
        return try {
            val songInfo = "com.tencent.qqmusic.core.song.SongInfo".findClass()
            var context: Context? = null
            "com.tencent.qqmusiccommon.util.music.RemoteLyricController".setReturnConstant("BluetoothA2DPConnected", result = true)
            "com.tencent.qqmusiccommon.util.music.RemoteControlManager".hookAfterConstructor(Context::class.java) {
                context = it.args[0] as Context
            }
            "com.tencent.qqmusiccommon.util.music.RemoteControlManager".hookBeforeMethod("updataMetaData", songInfo, String::class.java) {
                val lyric = if (it.args[1].isNull()) return@hookBeforeMethod else it.args[1].toString()
                it.args[1] = null // 去除妙播显示歌词
                LogUtils.e("$name: $lyric")
                Utils.sendLyric(context!!, lyric, name)
            }
            true
        } catch (_: XposedHelpers.ClassNotFoundError) {
            false
        }
    }
}