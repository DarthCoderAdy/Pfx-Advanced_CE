import net.pricefx.server.dto.calculation.ConfiguratorEntry
import net.pricefx.common.api.InputType

// Create a key for the CFO form ID from specified library
String cfoFormIdKey = libs.sCFO_ProductDiscountLib.ConstConfig.CFO_CONFIGURATOR_PASSED_VALUES["CFO_FORM_ID"]

// Create a new ConfiguratorEntry using the API
//    ConfiguratorEntry configuratorEntry = api.createConfiguratorEntry()

// Create a HiddenEntry InputBuilder with the CFO form ID key and build a context parameter for it
//    def param = api.inputBuilderFactory()
//        .createHiddenEntry(cfoFormIdKey)
//        .setLabel(cfoFormIdKey)
//        .setValue(api.currentItem()?.id)
//        .buildContextParameter()

// Add the newly created parameter to the ConfiguratorEntry. Use createParameter method.
// Return the modified ConfiguratorEntry
return api.createConfiguratorEntry(InputType.HIDDEN, cfoFormIdKey)

//    return configuratorEntry.createParameter(param)