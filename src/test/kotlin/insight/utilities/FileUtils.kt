package insight.utilities

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

object FileUtils {

        val DOWNLOAD_LOCATION: String = System.getProperty("user.dir")


    fun verifyExportedConfigFile(
        downloadDir: String,
        externalIdentifier: String,
        expectedDeviceName: String
    ): Boolean {
        // Construct the expected filename pattern
        val fileNamePrefix = "clink_config_export_$externalIdentifier"

        // Find the matching file
        val file = File(downloadDir)
            .listFiles { dir, name -> name.startsWith(fileNamePrefix) && name.endsWith(".xml") }
            ?.firstOrNull()
            ?: throw AssertionError("Exported config file starting with $fileNamePrefix not found in $downloadDir")

        println("Found export file: ${file.absolutePath}")

        val content = file.readText(Charsets.UTF_8)
        println("XML file content:\n$content")

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

        println("XML file is valid and Device attribute is '$deviceAttr'")
        return true
    }
}