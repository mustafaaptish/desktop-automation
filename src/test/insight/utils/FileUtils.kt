package insight.utils

import insight.utilities.log.logInfo
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

object FileUtils {

    fun verifyExportedConfigFile(
        downloadDir: String,
        externalIdentifier: String,
        expectedDeviceName: String
    ): Boolean {

        val fileNamePrefix = "clink_config_export_$externalIdentifier"

        val file = File(downloadDir)
            .listFiles { _, name -> name.startsWith(fileNamePrefix) && name.endsWith(".xml") }
            ?.firstOrNull()
            ?: throw AssertionError("Exported config file starting with $fileNamePrefix not found in $downloadDir")

        logInfo("Found export file: ${file.absolutePath}")

        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = docBuilder.parse(file)
        doc.documentElement.normalize()

        val rootElement = doc.documentElement
        if (rootElement.nodeName != "Configuration") {
            throw AssertionError("Root element is not <Configuration>, but <${rootElement.nodeName}>")
        }

        val deviceAttr = rootElement.getAttribute("Device")
        if (deviceAttr != expectedDeviceName) {
            throw AssertionError("Device attribute mismatch. Expected: $expectedDeviceName, Found: $deviceAttr")
        }

        logInfo("XML file is valid and Device attribute is '$deviceAttr'")
        return true
    }
}
