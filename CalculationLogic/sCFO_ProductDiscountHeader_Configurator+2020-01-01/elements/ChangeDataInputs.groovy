import net.pricefx.common.api.InputType
import net.pricefx.server.dto.calculation.ConfiguratorEntry

// Get value of the first input from the OtionInput.groovy, only if it exists
//String selectedType = out.OptionInput ? out.OptionInput.firstInput.valueOptions : null
String selectedType = out.OptionInput.getFirstInput()?.getValue()

// Use configuratorSwitch method. Pass selectedType as a parameter.
return configuratorSwitch(selectedType)

/**
 * This method is used to select and configure different types of ConfiguratorEntries.
 * It also loads the correct messages for each type.
 *
 * @param selectedType The type of ConfiguratorEntry to be used. This could be 'CHANGE_DATA', 'NEW_FAMILY', or 'NEW_GROUP'.
 * @return A ConfiguratorEntry instance configured based on the selectedType.
 * If the getterMethod or message for the selectedType do not exist, the method will return null.
 */
        ConfiguratorEntry configuratorSwitch(String selectedType) {
            Script CONFIG_UTILS = libs.sCFO_ProductDiscountLib.CfoConfiguratorUtils
            Script CONST_CONFIG = libs.sCFO_ProductDiscountLib.ConstConfig

            def getterMap = [
                    (CONST_CONFIG.CHANGE_DATA) : CONFIG_UTILS.&getChangeDataConfiguratorEntry,
                    (CONST_CONFIG.NEW_FAMILY)  : CONFIG_UTILS.&getAddFamilyConfiguratorEntry,
                    (CONST_CONFIG.NEW_GROUP)   : CONFIG_UTILS.&getAddGroupConfiguratorEntry,
            ]

            def messageMap = [
                    (CONST_CONFIG.CHANGE_DATA) : CONST_CONFIG.CFO_CONFIGURATOR_CONFIG.PARAGRAPH_MESSAGE_CHANGE,
                    (CONST_CONFIG.NEW_FAMILY)  : CONST_CONFIG.CFO_CONFIGURATOR_CONFIG.PARAGRAPH_MESSAGE_ADD_FAMILY,
                    (CONST_CONFIG.NEW_GROUP)   : CONST_CONFIG.CFO_CONFIGURATOR_CONFIG.PARAGRAPH_MESSAGE_ADD_GROUP,
            ]

            def getterMethod = getterMap[selectedType]
            def message = messageMap[selectedType]

            if (getterMethod && message) {
                return configureEntries(getterMethod, message)
            }
        }

//api.logInfo("configuratorSwitch", configuratorSwitch("Change data"))

/**
 * This method applies configuration to entries.
 *
 * @param getterMethod Method reference for getting configurator entry.
 * @param message The message to be set as paragraph header in configurator entry.
 * @return ConfiguratorEntry The modified configurator entry with new paragraph header.
 */
ConfiguratorEntry configureEntries(def getterMethod, String message) {
    ConfiguratorEntry configuratorEntry = getterMethod.call()
    configuratorEntry.setMessage(message)
    return configuratorEntry
}