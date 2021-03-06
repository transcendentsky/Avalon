package avalon.group

import avalon.tool.pool.Constants.Basic.CURRENT_PATH
import avalon.tool.pool.Constants.Basic.CURRENT_SERVLET
import avalon.tool.pool.Constants.Basic.LANG
import avalon.util.GroupConfig
import avalon.util.GroupMessage
import java.util.regex.Pattern

object Reboot : GroupMessageResponder() {
	override fun doPost(message: GroupMessage, groupConfig: GroupConfig) {
		message.response(LANG.getString("group.reboot.reply"))

		CURRENT_SERVLET.reboot()

		val os = System.getProperty("os.name").toLowerCase()
		val cmd: String? =
				when {
					os.contains("linux") -> "$CURRENT_PATH/Avalon"
					os.contains("windows") -> "cmd /c start $CURRENT_PATH\\Avalon.bat"
					else -> null
				}

		if (cmd == null) {
			message.response(LANG.getString("group.reboot.unsupported_os"))
			return
		}

		val process = Runtime.getRuntime().exec(cmd)
		process.inputStream.close()
		process.errorStream.close()
		process.inputStream.close()

		Thread.sleep(5000)

		Runtime.getRuntime().halt(0)
	}

	override fun responderInfo(): ResponderInfo =
			ResponderInfo(
					Pair("reboot", LANG.getString("group.reboot.help")),
					Pattern.compile("reboot"),
					permission = ResponderPermission.OWNER,
					manageable = false
			)

	override fun instance(): GroupMessageResponder? = this
}