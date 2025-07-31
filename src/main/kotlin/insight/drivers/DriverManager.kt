//package insight.drivers
//
//import io.appium.java_client.windows.WindowsDriver
//import org.openqa.selenium.WebElement
//
//object DriverManager {
//    private var driver: WindowsDriver<WebElement>? = null
//
//    fun getDriver(): WindowsDriver<WebElement> {
//        if (driver == null) {
//            driver = DesktopDriver.startNotepad()
//        }
//        return driver!!
//    }
//
//    fun quitDriver() {
//        driver?.quit()
//        driver = null
//    }
//}