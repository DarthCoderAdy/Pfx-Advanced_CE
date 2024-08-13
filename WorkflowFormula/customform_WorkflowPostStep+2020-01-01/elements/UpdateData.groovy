if (workflowHistory.steps.findAll { it.approvalStep }.every { it.approved } || workflowHistory.activeStep?.isEmpty()) {
    api.logInfo("api.currentItem()", api.currentItem())
    api.logInfo("workflowHistory", workflowHistory)

    //Get user inputs from current item assign it to inputs variable
    def inputs = api.currentItem()?.inputs?.get(0)?.value

    //Get individual field values from user inputs:
    //"managementOptionsInput" - assign it to the optionInput variable
    def optionInput = inputs?.get("managementOptionsInput")

    //"changeGroupOrFamily" - assign it to the changeGroupOrFamily variable
    def changeGroupOrFamily = inputs?.get("changeGroupOrFamily")

    //"ppTableInput" - assign it to the ppTableInput variable
    def ppTableInput = inputs?.get("ppTableInput")

    //"newName" - assign it to the newName variable
    def newName = inputs?.get("newName")

    api.logInfo("inputs", inputs)
    api.logInfo("optionInput", optionInput)
    api.logInfo("changeGroupOrFamily", changeGroupOrFamily)
    api.logInfo("ppTableInput", ppTableInput)
    api.logInfo("newName", newName)


    if (optionInput == "Change data") {
        if (changeGroupOrFamily == "Product Group") {
            //Get value of "productGroupOption" from user inputs and assign it to the oldName variable
            def oldName = inputs.productGroupOption

            //Get "Discount" lookup table Id assign it to the discountID variable.
            def discountID = api.findLookupTable("Discount").id
            //Create filters:
            def filters = [
                    Filter.equal("lookupTable.id", discountID),
                    Filter.equal("ProductGroup", oldName)
            ]
            //Update "Discount" lookup table. To obtain accurate data, make use of filters.
            def updatedDiscountTable = api.find('MLTV2', 0, *filters).collect {
                def entry = [
                        lookupTableName: "Discount",
                        id             : it.id,
                        valueType      : it.valueType,
                        typedId        : it.typedId,
                        key1           : newName,
                        key2           : it.key2,
                        attribute1     : it.attribute1,
                        attribute2     : it.attribute2
                ]
                api.addOrUpdate("MLTV2", entry)
            }
            api.logInfo("updatedDiscountTable", updatedDiscountTable)

            //Get "ProductFamilyMapping" lookup table Id assign it to the productFamilyMappingID variable.
            def productFamilyMappingID = api.findLookupTable("ProductFamilyMapping").id
            //Create filters:
            def productFamilyFilters = [
                    Filter.equal("lookupTable.id", productFamilyMappingID),
                    Filter.equal("name", oldName)
            ]
            // Update "ProductFamilyMapping" lookup table.To obtain accurate data , make use of filters.
            api.find('LTV', 0, *productFamilyFilters).collect {
                def entry = [
                        lookupTableName: "ProductFamilyMapping",
                        id             : it.id,
                        valueType      : it.valueType,
                        typedId        : it.typedId,
                        name           : newName,
                        value          : it.value
                ]
                api.addOrUpdate("LTV", entry)
            }
        } else if (changeGroupOrFamily == "Product Family") {
            //Get value of "productGroupOption" from user inputs and assign it to the oldValue variable
            def oldValue = inputs.productFamilyOption

            //Get "DiscountLevelDefinition" lookup table Id assign it to the discountLevelDefinitionID variable.
            def discountLevelDefinitionID = api.findLookupTable("DiscountLevelDefinition").id
            //Create filters:
            def discountLevelFilters = [
                    Filter.equal("lookupTable.id", discountLevelDefinitionID),
                    Filter.equal("key1", oldValue)
            ]
            //Update "DiscountLevelDefinition" lookup table. To obtain accurate data, make use of filters.
            api.find('MLTV2', 0, *discountLevelFilters).collect {
                def entry = [
                        lookupTableName: "DiscountLevelDefinition",
                        id             : it.id,
                        valueType      : it.valueType,
                        typedId        : it.typedId,
                        key1           : newName,
                        key2           : it.key2,
                        attribute1     : it.attribute1,
                ]
                api.addOrUpdate("MLTV2", entry)
            }

            //Get "ProductFamilyMapping" lookup table Id assign it to the productFamilyMappingID variable.
            def productFamilyMappingID = api.findLookupTable("ProductFamilyMapping").id
            //Create filters:
            def productFamilyFilters = [
                    Filter.equal("lookupTable.id", productFamilyMappingID),
                    Filter.equal("value", oldValue)
            ]
            //Update "ProductFamilyMapping" lookup table. To obtain accurate data, make use of filters.
            api.find('LTV', 0, *productFamilyFilters).collect {
                def entry = [
                        lookupTableName: "ProductFamilyMapping",
                        id             : it.id,
                        valueType      : it.valueType,
                        typedId        : it.typedId,
                        name           : it.name,
                        value          : newName
                ]
                api.addOrUpdate("LTV", entry)
            }

        } else if (ppTableInput == "Discount") {
            //Get individual field values from user inputs:
            def productGroupOption = inputs.productGroupOption
            def discountLevelsOption = inputs.discountLevelsOption
            def newTargetDiscountPct = inputs.newTargetDiscountPct as Integer
            def newMaxDiscountPct = inputs.newMaxDiscountPct as Integer

            //Get "Discount" lookup table Id assign it to the discountID variable.
            def discountID = api.findLookupTable("Discount").id
            //Create filters:
            def discountFilters = [
                    Filter.equal("lookupTable.id", discountID),
                    Filter.equal("ProductGroup", productGroupOption),
                    Filter.equal("key2", discountLevelsOption)
            ]
            //Update "Discount" lookup table. To obtain accurate data, make use of filters.
            api.find('MLTV2', 0, *discountFilters).collect {
                def entry = [
                        lookupTableName: "Discount",
                        id             : it.id,
                        valueType      : it.valueType,
                        typedId        : it.typedId,
                        key1           : it.key1,
                        key2           : it.key2,
                        attribute1     : newTargetDiscountPct/100,
                        attribute2     : newMaxDiscountPct/100
                ]
                api.addOrUpdate("MLTV2", entry)
            }

        } else if (ppTableInput == "Discount Level Definition") {
            //Get individual field values from user inputs:
            def newMinRevenue = inputs.newMinRevenue
            def productFamilyOption = inputs.productFamilyOption
            def discountLevelsOption = inputs.discountLevelsOption

            //Get "DiscountLevelDefinition" lookup table Id assign it to the discountLevelDefinitionID variable.
            def discountLevelDefinitionID = api.findLookupTable("DiscountLevelDefinition").id
            //Create filters:
            def discountLevelFilters = [
                    Filter.equal("lookupTable.id", discountLevelDefinitionID),
                    Filter.equal("key1", productFamilyOption),
                    Filter.equal("key2", discountLevelsOption)
            ]
            //Update "DiscountLevelDefinition" lookup table. To obtain accurate data, make use of filters.
            api.find('MLTV2', 0, *discountLevelFilters).collect {
                def entry = [
                        lookupTableName: "DiscountLevelDefinition",
                        id             : it.id,
                        valueType      : it.valueType,
                        typedId        : it.typedId,
                        key1           : it.key1,
                        key2           : it.key2,
                        attribute1     : newMinRevenue,
                ]
                api.addOrUpdate("MLTV2", entry)
            }

        }
    } else {
        //Get following tables outputs from current item:
        def outputs = api.currentItem()?.outputs
        api.logInfo("Outputs", outputs)
        def newDiscountLevelTable
        def productFamilyMapping
        def newDiscountTable

        newDiscountLevelTable = outputs?.find {it.resultName == "newDiscountLevelTable"}
        productFamilyMapping = outputs?.find {it.resultName == "productFamilyMapping"}
        newDiscountTable = outputs?.find {it.resultName == "newDiscountTable"}

        //If output is null get an empty list
        newDiscountLevelTable = newDiscountLevelTable ? newDiscountLevelTable : []
        productFamilyMapping = productFamilyMapping ? productFamilyMapping : []
        newDiscountTable = newDiscountTable ? newDiscountTable : []

        api.logInfo("newDiscountLevelTable", newDiscountLevelTable)
        api.logInfo("productFamilyMapping", productFamilyMapping)
        api.logInfo("newDiscountTable", newDiscountTable)

        //Filter the rows in their respective tables based on the values of Map.
        def discountLevelDefinitionEntries = newDiscountLevelTable.result.entries.findAll { it['Product Family'] instanceof Map }
        def productFamilyMappingEntriesGroup = productFamilyMapping.result.entries.findAll { it['Product Group'] instanceof Map }
        def productFamilyMappingEntriesFamily = productFamilyMapping.result.entries.findAll { it['Product Family'] instanceof Map }
        def newDiscountTableEntriesGroup = newDiscountTable.result.entries.findAll { it['Product Group'] instanceof Map }

        api.logInfo("discountLevelDefinitionEntries", discountLevelDefinitionEntries)
        api.logInfo("productFamilyMappingEntriesGroup", productFamilyMappingEntriesGroup)
        api.logInfo("newDiscountTableEntriesGroup", newDiscountTableEntriesGroup)

        if (optionInput == "Add new Product Family") {
            //Get individual field values from user inputs:
            //def assignProductGroup = inputs.assignProductGroup
            //def productFamilyInput = inputs.productFamilyInput

            //Get "ProductFamilyMapping" lookup table Id assign it to the productFamilyMappingID variable.
            //def productFamilyMappingID = api.findLookupTable("ProductFamilyMapping").id
            //Create filters:
//            def productFamilyFilters = [
//                    Filter.equal("lookupTable.id", productFamilyMappingID),
//                    Filter.equal("name", assignProductGroup)
//            ]
            //Update "ProductFamilyMapping" lookup table. To obtain accurate data, make use of filters.
//            api.find('LTV', 0, *productFamilyFilters).collect {
//                def entry = [
//                        lookupTableName: "ProductFamilyMapping",
//                        id             : it.id,
//                        valueType      : it.valueType,
//                        typedId        : it.typedId,
//                        name           : assignProductGroup ?: "New Group",
//                        value          : productFamilyInput
//                ]
//                api.addOrUpdate("LTV", entry)
//            }

            //new approach so that it also works when assignProductGroup == false
            productFamilyMappingEntriesGroup.each {
                api.addOrUpdate("LTV", [
                        lookupTableName: "ProductFamilyMapping",
                        name           : it["Product Group"].value,
                        value          : it["Product Family"].value
                ])
            }

            //Use discountLevelDefinitionEntries and update "DiscountLevelDefinition" lookup table
            discountLevelDefinitionEntries.each {
                api.addOrUpdate("MLTV2", [
                        lookupTableName: "DiscountLevelDefinition",
                        key1           : it["Product Family"].value,
                        key2           : it["Discount Level"].value,
                        attribute1     : it["Min Revenue"].value,
                ])
            }

        } else if (optionInput == "Add new Product Group") {
            //Use productFamilyMappingEntries and update "ProductFamilyMapping" lookup table
            productFamilyMappingEntriesGroup.each {
                api.addOrUpdate("LTV", [
                        lookupTableName: "ProductFamilyMapping",
                        name           : it["Product Group"].value,
                        value          : it["Product Family"].value
                ])
            }

            //Use discountEntries and update "Discount" lookup table
            newDiscountTableEntriesGroup.each {
                api.addOrUpdate("MLTV2", [
                        lookupTableName: "Discount",
                        key1           : it["Product Group"].value,
                        key2           : it["Discount Level"].value,
                        attribute1     : it["Target Discount %"].value/100,
                        attribute2     : it["Max Discount %"].value/100
                ])
            }
        }
    }
}