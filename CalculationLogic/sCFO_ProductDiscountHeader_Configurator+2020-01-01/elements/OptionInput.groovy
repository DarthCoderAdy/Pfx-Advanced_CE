
import net.pricefx.server.dto.calculation.ConfiguratorEntry

// Initialize a constant for main paragraph message
String PARAGRAPH_MESSAGE_MAIN = libs.sCFO_ProductDiscountLib.ConstConfig.CFO_CONFIGURATOR_CONFIG["PARAGRAPH_MESSAGE_MAIN"]

// Get value of the first input from the ConfiguratorEntry, only if it exists
//Long formId = out.ConfiguratorEntry ? out.ConfiguratorEntry.firstInput.value : null
Long formId = out.ConfiguratorEntry.getFirstInput()?.getValue()

// Get the product discount configurator utility script from the product discount library
Script productDiscountConfigurator = libs.sCFO_ProductDiscountLib.CfoConfiguratorUtils

// Instantiate the main entry of the product discount configurator.
// Use getMainConfiguratorEntry method with formId as a parameter
ConfiguratorEntry mainConfiguratorEntry = productDiscountConfigurator.getMainConfiguratorEntry(formId)

// Set the paragraph header for the main configurator entry. Use the constant initialized earlier
//mainConfiguratorEntry.setMessage(PARAGRAPH_MESSAGE_MAIN)
mainConfiguratorEntry.setMessage(PARAGRAPH_MESSAGE_MAIN)

// Return the main configurator entry
return mainConfiguratorEntry