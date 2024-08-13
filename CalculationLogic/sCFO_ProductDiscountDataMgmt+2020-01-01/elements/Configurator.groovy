import net.pricefx.common.api.InputType

if (customFormProcessor.isPrePhase()) {
    //1. Create two variables named 'configuratorName' and 'cfoFormIdKey'. Assign values to them from the Constconfig file.
    String configuratorName = libs.sCFO_ProductDiscountLib.ConstConfig.CFO_CONFIGURATOR_NAME
    String cfoFormIdKey = libs.sCFO_ProductDiscountLib.ConstConfig.CFO_CONFIGURATOR_PASSED_VALUES["CFO_FORM_ID"]
//    api.trace("configName", configuratorName)
//    api.trace("cfoFormIdKey", cfoFormIdKey)

    //2. Assign value to currentCfoForm using api.currentItem()
    def currentCfoForm = api.currentItem()
//    api.trace(currentCfoForm)

    //3. Get id from currentItem. Remember that currentItem can be null
    Long cfoFormId = currentCfoForm ? currentCfoForm.id : null
//    api.trace("cfoFormId", cfoFormId)
    //4. Get inputs from currentCfoFrom.
    def inputs = currentCfoForm?.inputs
    //5. Find input with a name that matches configuratorName
    def matchingInput = inputs?.find {it.name == configuratorName}

    //6. Take value
    def valueOfMatchingInput = matchingInput?.value

    //7. If the above operation returns null, return an empty map [:]. You can use elvis operator '?:'
    //If you want to prevent NullPointerException, it is recommended to use the safe navigation operator '?'.
    //This will ensure that your code doesn't throw an exception if a null value is encountered.
    Map configuratorValueBackup = valueOfMatchingInput ?: [:]
//    api.trace("configuratorValueBackup", configuratorValueBackup)

    //8. Add current form id to the configuratorValueBackup. Use cfoFormIdKey as a key and cfoFormId as value.
    configuratorValueBackup.put(cfoFormIdKey, cfoFormId)

    //9. update the input "ROOT" with a map that provides the name, label, url, type and value.
    customFormProcessor.addOrUpdateInput("ROOT",
            [
                    name : configuratorName,
                    label: configuratorName,
                    url  : configuratorName,
                    type : InputType.INLINECONFIGURATOR,
                    value: configuratorValueBackup
            ]
    )
}