import net.pricefx.common.api.FieldFormatType

if (customFormProcessor.isPostPhase()) {

    // chosenOption is a value from managementOptionsInput
    def inputs = input["LogicsCFO_ProductDiscountHeader_Configurator"]
    def chosenOption = inputs.managementOptionsInput
    api.logInfo("chosenOption", input["LogicsCFO_ProductDiscountHeader_Configurator"])
    if (chosenOption == "Add new Product Group") {
        //Create a table with the following columns:
        def newTable = api.newMatrix("Product Group", "Discount Level", "Target Discount %", "Max Discount %")
        newTable.setColumnFormat('Product Group', FieldFormatType.TEXT)
        newTable.setColumnFormat('Discount Level', FieldFormatType.NUMERIC)
        newTable.setColumnFormat('Target Discount %', FieldFormatType.PERCENT)
        newTable.setColumnFormat('Max Discount %', FieldFormatType.PERCENT)
        //Get data from 'Discount' company parameter table.
        def oldData = libs.sCFO_ProductDiscountLib.CfoConfiguratorUtils.getDiscount()
        //Add data from 'Discount' to the newly created.
        oldData.each { row ->
            def newRow = [
                    'Product Group': row.productGroup,
                    'Discount Level': row.discountLevel,
                    'Target Discount %': row.targetDiscount,
                    'Max Discount %': row.maxDiscount
            ]
            newTable.addRow(newRow)
        }

        //Get data from 'New Discount' table. This table has been created in getAddGroupConfiguratorEntry method.
        def newData = inputs.newDiscount
        //Add data from 'New Discount' to the newly created with proper style
        newData.each { row ->
            def newRow = [
                    'Product Group': newTable.styledCell(row.productGroup, "black", "yellow", "bold"),
                    'Discount Level': newTable.styledCell(row.discountLevel, "black", "yellow", "bold"),
                    'Target Discount %': newTable.styledCell(row.targetDiscount / 100, "black", "yellow", "bold"),
                    'Max Discount %': newTable.styledCell(row.maxDiscount / 100, "black", "yellow", "bold")
            ]

            newTable.addRow(newRow)
        }
        //Add new table from point 1 to the customFormProcessor
        customFormProcessor.addOrUpdateOutput(
                [
                        "resultName" : "newDiscountTable",
                        "resultLabel": "Discount",
                        "resultType" : "MATRIX",
                        "result"     : newTable,
                ]
        )

        //Create a second table with the following columns:
        def secondTable = api.newMatrix("Product Group", "Product Family")
        secondTable.setColumnFormat('Product Group', FieldFormatType.TEXT)
        secondTable.setColumnFormat("Product Family", FieldFormatType.TEXT)

        //Get data from 'ProductFamilyMapping' company parameter table.
        def oldDataFamilyMapping = libs.sCFO_ProductDiscountLib.CfoConfiguratorUtils.getProductFamilyMapping()

        //Add data from 'ProductFamilyMapping' to the newly created.
        oldDataFamilyMapping.each { row ->
            def newRow = [
                    'Product Group': row.productGroup,
                    'Product Family': row.productFamily
            ]
            secondTable.addRow(newRow)
        }


        //Get data from productGroupInput and assignProductFamily and add them to the newly created table. Remember to apply appropriate formatting.
        def styledProductGroup = secondTable.styledCell(inputs.productGroupInput, "black", "yellow", "bold")
        def styledProductFamily = secondTable.styledCell(inputs.assignProductFamily, "black", "yellow", "bold")
        secondTable.addRow(['Product Group': styledProductGroup, 'Product Family': styledProductFamily])

        //Add new table from point 7 to the customFormProcessor.
        customFormProcessor.addOrUpdateOutput(
                [
                        "resultName" : "productFamilyMapping",
                        "resultLabel": "Product Family Mapping",
                        "resultType" : "MATRIX",
                        "result"     : secondTable,
                ]
        )

    } else if (chosenOption == "Add new Product Family") {
        //Create a table with the following columns:
        def newTable = api.newMatrix("Product Family", "Discount Level", "Min Revenue")
        newTable.setColumnFormat('Product Family', FieldFormatType.TEXT)
        newTable.setColumnFormat('Discount Level', FieldFormatType.INTEGER)
        newTable.setColumnFormat('Min Revenue', FieldFormatType.NUMERIC)

        //Get data from 'DiscountLevelDefinition' company parameter table.
        def oldData = libs.sCFO_ProductDiscountLib.CfoConfiguratorUtils.getDiscountLevelDefinition()
        api.logInfo("oldData", oldData)

        //Add data from 'DiscountLevelDefinition' to the newly created.
        oldData.each { row ->
            def newRow = [
                    'Product Family': row.productFamily,
                    'Discount Level': row.discountLevel,
                    'Min Revenue': row.minRevenue,
            ]
            newTable.addRow(newRow)
        }

        //Get data from 'New Discount Levels' table. This table has been created in getAddFamilyConfiguratorEntry method.
        def newData = inputs.newDiscountLevels

        //Add data from 'New Discount Levels' to the newly created. Remember to apply appropriate formatting.
        newData.each { row ->
            def newRow = [
                    'Product Family': newTable.styledCell(row.productFamily, "black", "yellow", "bold"),
                    'Discount Level': newTable.styledCell(row.discountLevel, "black", "yellow", "bold"),
                    'Min Revenue': newTable.styledCell(row.minRevenue, "black", "yellow", "bold"),
            ]

            newTable.addRow(newRow)
        }
        //Add new table from point 1 to the customFormProcessor
        customFormProcessor.addOrUpdateOutput(
                [
                        "resultName" : "newDiscountLevelTable",
                        "resultLabel": "Discount Level Definition",
                        "resultType" : "MATRIX",
                        "result"     : newTable,
                ]
        )

        //Create a second table with the following columns:
        def secondTable = api.newMatrix("Product Group", "Product Family")
        secondTable.setColumnFormat('Product Group', FieldFormatType.TEXT)
        secondTable.setColumnFormat("Product Family", FieldFormatType.TEXT)

        //Get values from assignProductGroup and productFamilyInput
        def productFamilyInput = inputs.productFamilyInput
        def assignProductGroup = inputs.assignProductGroup

        //Get data from 'ProductFamilyMapping' company parameter table
        def oldDataFamilyMapping = libs.sCFO_ProductDiscountLib.CfoConfiguratorUtils.getProductFamilyMapping()

        //Iterate through 'ProductFamilyMapping' data and change product family for product group. Ensure that only a single value is modified. Remember to apply appropriate formatting.
        oldDataFamilyMapping.each { row ->
            def newRow = [
                    'Product Group': row.productGroup,
                    'Product Family': row.productFamily
            ]
            secondTable.addRow(newRow)
        }
        def styledProductGroup = secondTable.styledCell(assignProductGroup ?: "New Group", "black", "yellow", "bold")
        def styledProductFamily = secondTable.styledCell(productFamilyInput, "black", "yellow", "bold")
        secondTable.addRow(['Product Group': styledProductGroup, 'Product Family': styledProductFamily])

        //Add new table from point 7 to the customFormProcessor.
        customFormProcessor.addOrUpdateOutput(
                [
                        "resultName" : "productFamilyMapping",
                        "resultLabel": "Product Family Mapping",
                        "resultType" : "MATRIX",
                        "result"     : secondTable,
                ]
        )


    } else if (chosenOption == "Change data") {
        //Get the changeGroupOrFamily value. This is an input option from changeDataSection.
        api.logInfo("inputs", inputs)
        def chosenTable = inputs.changeGroupOrFamily

        //Get the newName vaue. This is an input string entry from changeDataSection
        def newName = inputs.newName

        //Get the ppTableInput value. This is an input option from changeDataSection
        def ppTableInput = inputs.ppTableInput

        if (ppTableInput == "Product Family Mapping") {
            //Create a table with the following columns:
            def productGroupFamilyTable = api.newMatrix("Product Group", "Product Family")
            productGroupFamilyTable.setColumnFormat('Product Group', FieldFormatType.TEXT)
            productGroupFamilyTable.setColumnFormat("Product Family", FieldFormatType.TEXT)

            //Get data from 'ProductFamilyMapping' company parameter table.
            def productFamilyMappingTable = libs.sCFO_ProductDiscountLib.CfoConfiguratorUtils.getProductFamilyMapping()


            if (chosenTable == "Product Family") {
                //Get the productFamilyOption value.
                def productFamilyOption = inputs.productFamilyOption

                //Iterate through 'ProductFamilyMapping'. If productFamilyOption equals product family add newName value to the row. Remember to apply appropriate formatting.
                def styledCell = productGroupFamilyTable.styledCell(newName, "black", "yellow", "bold")
                productFamilyMappingTable.each { row ->
                    def newRow = [
                            'Product Group': row.productGroup,
                            'Product Family': (productFamilyOption == row.productFamily) ? styledCell : row.productFamily
                    ]
                    productGroupFamilyTable.addRow(newRow)
                }


                //Create a table with the following columns:
                def secondTable = api.newMatrix("Product Family", "Discount Level", "Min Revenue")
                secondTable.setColumnFormat('Product Family', FieldFormatType.TEXT)
                secondTable.setColumnFormat("Discount Level", FieldFormatType.INTEGER)
                secondTable.setColumnFormat("Min Revenue", FieldFormatType.NUMERIC)

                //Get data from 'DiscountLevelDefinition' company parameter table.
                def oldData = libs.sCFO_ProductDiscountLib.CfoConfiguratorUtils.getDiscountLevelDefinition()

                //Add data from 'DiscountLevelDefinition' to the newly created from point 3. Iterate through data 'DiscountLevelDefinition' add and new product family. Remember to apply appropriate formatting.
                oldData.each { row ->
                    def newRow = [
                            'Product Family': (productFamilyOption == row.productFamily) ? styledCell : row.productFamily,
                            'Discount Level': row.discountLevel,
                            'Min Revenue': row.minRevenue,
                    ]
                    secondTable.addRow(newRow)
                }

                //Add new table from point 3 to the customFormProcessor
                customFormProcessor.addOrUpdateOutput(
                        [
                                "resultName" : "DiscountLevelDefinition",
                                "resultLabel": "Discount Level Definition",
                                "resultType" : "MATRIX",
                                "result"     : secondTable,
                        ]
                )

            } else if (chosenTable == "Product Group") {
                //Get the productGroupOption value.
                def productGroupOption = inputs.productGroupOption

                //Iterate through 'ProductFamilyMapping'. If productFamilyOption equals product group add newName value to the row. Remember to apply appropriate formatting.
                def styledCell = productGroupFamilyTable.styledCell(newName, "black", "yellow", "bold")
                productFamilyMappingTable.each { row ->
                    def newRow = [
                            'Product Group': (productGroupOption == row.productGroup) ? styledCell : row.productGroup,
                            'Product Family': row.productFamily
                    ]
                    productGroupFamilyTable.addRow(newRow)
                }

                //Create a table with the following columns:
                def secondTable = api.newMatrix("Product Group", "Discount Level", "Target Discount %", "Max Discount %")
                secondTable.setColumnFormat('Product Group', FieldFormatType.TEXT)
                secondTable.setColumnFormat("Discount Level", FieldFormatType.NUMERIC)
                secondTable.setColumnFormat("Target Discount %", FieldFormatType.PERCENT)
                secondTable.setColumnFormat("Max Discount %", FieldFormatType.PERCENT)

                //Get data from 'Discount' company parameter table.
                def discount = libs.sCFO_ProductDiscountLib.CfoConfiguratorUtils.getDiscount()

                //Add data from 'Discount' to the newly created from point 3. Iterate through data 'Discount' add and new product group. Remember to apply appropriate formatting.
                discount.each { row ->
                    def newRow = [
                            'Product Group': (productGroupOption == row.productGroup) ? styledCell : row.productGroup,
                            'Discount Level': row.discountLevel,
                            'Target Discount %': row.targetDiscount,
                            'Max Discount %': row.maxDiscount
                    ]
                    secondTable.addRow(newRow)
                }

                //Add new table from point 3 to the customFormProcessor.
                customFormProcessor.addOrUpdateOutput(
                        [
                                "resultName" : "discount",
                                "resultLabel": "Discount",
                                "resultType" : "MATRIX",
                                "result"     : secondTable,
                        ]
                )
            }
            customFormProcessor.addOrUpdateOutput(
                    [
                            "resultName" : "secondDiscountTable",
                            "resultLabel": "Product Family Mapping",
                            "resultType" : "MATRIX",
                            "result"     : productGroupFamilyTable,
                    ]
            )
        } else if (ppTableInput == "Discount Level Definition") {
            //Create a table with the following columns:
            def newTable = api.newMatrix("Product Family", "Discount Level", "Min Revenue")
            newTable.setColumnFormat('Product Family', FieldFormatType.TEXT)
            newTable.setColumnFormat("Discount Level", FieldFormatType.INTEGER)
            newTable.setColumnFormat("Min Revenue", FieldFormatType.NUMERIC)

            //Get data from 'DiscountLevelDefinition' company parameter table.
            def discountLevel = libs.sCFO_ProductDiscountLib.CfoConfiguratorUtils.getDiscountLevelDefinition()

            //Get the discountLevelsOption value.
            def discountLevelsOption = inputs.discountLevelsOption as Integer

            //Get the productFamilyOption value.
            def productFamilyOption = inputs.productFamilyOption

            //Add data from 'DiscountLevelDefinition' to the newly created table. Iterate through data from 'DiscountLevelDefinition'.
            // If product family equals productFamilyOption and discount level equals discountLevelsOption change newMinRevenue for the row. Remember to apply appropriate formatting.
            def newMinRevenue = inputs.newMinRevenue
            def styledCell = newTable.styledCell(newMinRevenue, "black", "yellow", "bold")
            discountLevel.each { row ->
                def newRow = [
                        'Product Family': row.productFamily,
                        'Discount Level': row.discountLevel,
                        'Min Revenue': (row.productFamily == productFamilyOption && row.discountLevel == discountLevelsOption) ? styledCell : row.minRevenue,
                ]
                newTable.addRow(newRow)
            }

            //Add new table from point 1 to the customFormProcessor.
            customFormProcessor.addOrUpdateOutput(
                    [
                            "resultName" : "discountLevelDefinition",
                            "resultLabel": "Discount Level Definition",
                            "resultType" : "MATRIX",
                            "result"     : newTable,
                    ]
            )

        } else if (ppTableInput == "Discount") {
            //Create a table with the following columns:
            def newTable = api.newMatrix("Product Group", "Discount Level", "Target Discount %", "Max Discount %")
            newTable.setColumnFormat('Product Group', FieldFormatType.TEXT)
            newTable.setColumnFormat("Discount Level", FieldFormatType.NUMERIC)
            newTable.setColumnFormat("Target Discount %", FieldFormatType.PERCENT)
            newTable.setColumnFormat("Max Discount %", FieldFormatType.PERCENT)

            //Get data from 'Discount' company parameter table.
            def discount = libs.sCFO_ProductDiscountLib.CfoConfiguratorUtils.getDiscount()

            //Get the productGroupOption value.
            def productGroupOption = inputs.productGroupOption

            //Get the discountLevelsOption value.
            def discountLevelsOption = inputs.discountLevelsOption as Integer

            //Add data from 'Discount' to the newly created table. Iterate through data from 'Discount'.
            // If product group equals productGroupOption and discount level equals discountLevelsOption change max discount to (newMaxDiscountPct/100) and target discount to (newTargetDiscountPct/100).
            // Remember to apply appropriate formatting.
            def newMaxDiscountPct = inputs.newMaxDiscountPct as Integer
            def newTargetDiscountPct = inputs.newTargetDiscountPct as Integer

            def styledMaxDiscount = newTable.styledCell(newMaxDiscountPct/100, "black", "yellow", "bold")
            def styledTargetDiscount = newTable.styledCell(newTargetDiscountPct/100, "black", "yellow", "bold")
            discount.each { row ->
                def newRow = [
                        'Product Group': row.productGroup,
                        'Discount Level': row.discountLevel,
                        'Target Discount %': (row.productGroup == productGroupOption && row.discountLevel == discountLevelsOption) ? styledTargetDiscount : row.targetDiscount,
                        'Max Discount %': (row.productGroup == productGroupOption && row.discountLevel == discountLevelsOption) ? styledMaxDiscount : row.maxDiscount
                ]
                newTable.addRow(newRow)
            }

            //Add new table from point 1 to the customFormProcessor.
            customFormProcessor.addOrUpdateOutput(
                    [
                            "resultName" : "discount",
                            "resultLabel": "Discount",
                            "resultType" : "MATRIX",
                            "result"     : newTable,
                    ]
            )
        }
    }

}