import net.pricefx.common.api.InputType
import net.pricefx.server.dto.calculation.ConfiguratorEntry
import net.pricefx.server.dto.calculation.ContextParameter

// 1. If the form being processed is in the post phase,
//    immediately return and skip the rest of the code.
if (customFormProcessor.isPostPhase()) {
    return
}


// 2. Add a product group entry for doorframes,
//    filtered by 'Doorframes' to the form.
api.inputBuilderFactory()
        .createProductGroupEntry('productDoorframes')
        .setLabel("Doorframes")
        .setFilterFormulaName("eCFO_ProductFilter")
        .setFilterFormulaParam(api.jsonEncode(["attribute1": "Doorframes"]))
        .addOrUpdateInput(customFormProcessor, 'ROOT')

// 3. Add a product group entry for locks and handles,
//    filtered by 'Locks&handles' to the form.
api.inputBuilderFactory()
        .createProductGroupEntry('productLocksHandles')
        .setLabel("Locks&handles")
        .setFilterFormulaName("eCFO_ProductFilter")
        .setFilterFormulaParam(api.jsonEncode(["attribute1": "Locks&handles"]))
        .addOrUpdateInput(customFormProcessor, 'ROOT')

// 4. Get the names from the 'Material', 'Color' and 'DoorSizes' look-up tables.
def nameColor = api.findLookupTableValues('ColorAdjustment')['name']
def nameMaterial = api.findLookupTableValues('MaterialAdjustment')['name']
def nameDoorsize = api.findLookupTableValues('DoorSizeAdjustment')['name']


// 5. Create configurator tables for doorframes and locks/handles.
def doorframesMatrixBuilder = api.inputBuilderFactory()
        .createInputMatrix("DoorframesMatrix")
        .setLabel("Doorframes Configurator Table")
        .setHideAddButton(true)
        .setHideRemoveButton(false)
        .setColumnValueOptions(['Colour': nameColor , 'Material': nameMaterial, 'Door size': nameDoorsize])
        .setColumns(['Product Id', 'Label', 'Base Price', 'Door size', 'Material', 'Colour'])
        .setReadOnlyColumns(['Product Id', 'Label', 'Base Price'])
        .addOrUpdateInput(customFormProcessor, 'ROOT')

def locksAndHandlesMatrixBuilder = api.inputBuilderFactory()
        .createInputMatrix("LocksAndHandlesMatrix")
        .setLabel("Locks and Handles Configurator Table")
        .setHideAddButton(true)
        .setHideRemoveButton(false)
        .setColumnValueOptions(['Colour': nameColor, 'Material': nameMaterial])
        .setColumns(['Product Id', 'Label', 'Base Price', 'Material', 'Colour'])
        .setReadOnlyColumns(['Product Id', 'Label', 'Base Price'])
        .addOrUpdateInput(customFormProcessor, 'ROOT')

// 6. Get Products data from "productDoorframes" and "productLocksAndHandles"
def productDoorframes = input.productDoorframes?.label
def productLocksAndHandles = input.productLocksHandles?.label

api.trace("productDoorframes", productDoorframes)
api.trace("productLocksAndHandles", productLocksAndHandles)

def arrayProductDoorframes = productDoorframes?.trim()?.split(",")?.toList()
def arrayProductLocksAndHandles = productLocksAndHandles?.trim()?.split(",")?.toList()

def filteredRecordsProductDoorframes = []
def filteredRecordsProductLocksAndHandles = []


def table = api.find("P", 0, 0, null, Filter.like("label", "%DR%"))
def currentItem = api.currentItem()
api.trace("currentItem", currentItem)
def doorframes = currentItem.inputs.find {it.name == "DoorframesMatrix"}?.value
def lockAndHandles = currentItem.inputs.find {it.name == "LocksAndHandlesMatrix"}?.value

// 7. If there are any Doorframes products, populate
//    the DoorframesMatrix of the form with relevant data.
//    Otherwise, set the matrix to empty.

if (arrayProductDoorframes && !arrayProductDoorframes.isEmpty()) {
    arrayProductDoorframes.each { id ->
        def record  = table.find { row ->
            row.sku == id
        }
        filteredRecordsProductDoorframes << record
    }
    def doorFramesRows = filteredRecordsProductDoorframes.collect { row ->
        def filters = [
                Filter.equal("name", "ProductBaseValues"),
                Filter.equal("sku", row["sku"])
        ]
        def doorFrame = doorframes?.find {it["Product Id"] == row["sku"]}

        [
                'Product Id': row["sku"],
                'Label': row["label"],
                'Base Price': api.find("PX3", 0, 1, null, *filters)?.getAt(0)?.attribute2,
                'Colour': doorFrame?.get("Colour") ?: null,
                'Material': doorFrame?.get("Material") ?: null,
                'Door size': doorFrame?.get("Door size") ?: null
        ]

    }

    api.logInfo("doorFramesRows Before Setting", doorFramesRows)

    doorframesMatrixBuilder.setValue(doorFramesRows)
    doorframesMatrixBuilder.addOrUpdateInput(customFormProcessor, 'ROOT')
} else {
    // No products found, clear the matrix
    doorframesMatrixBuilder.setValue([])
    doorframesMatrixBuilder.addOrUpdateInput(customFormProcessor, 'ROOT')
    api.logInfo("DoorframesMatrix cleared because no products were found.")
}

// 8. If there are any LocksAndHandles products, populate
///   the LocksAndHandlesMatrix of the form with relevant data.
//    Otherwise, set the matrix to empty.
if (arrayProductLocksAndHandles && !arrayProductLocksAndHandles.isEmpty()) {
    arrayProductLocksAndHandles.each { id ->
        def record  = table.find { row ->
            row.sku == id
        }
        filteredRecordsProductLocksAndHandles << record
    }
    def locksAndHandlesRows = filteredRecordsProductLocksAndHandles.collect { row ->
        def filters = [
                Filter.equal("name", "ProductBaseValues"),
                Filter.equal("sku", row["sku"])
        ]
        def lockHandle = lockAndHandles?.find {it["Product Id"] == row["sku"]}
        [
                'Product Id': row["sku"],
                'Label': row["label"],
                'Base Price': api.find("PX3", 0, 1, null, *filters)?.getAt(0)?.attribute2,
                'Colour': lockHandle?.get("Colour") ?: null,
                'Material': lockHandle?.get("Material") ?: null
        ]

    }

    api.logInfo("locksAndHandlesRows Before Setting", locksAndHandlesRows)

    locksAndHandlesMatrixBuilder.setValue(locksAndHandlesRows)
    locksAndHandlesMatrixBuilder.addOrUpdateInput(customFormProcessor, 'ROOT')
} else {
    // No products found, clear the matrix
    locksAndHandlesMatrixBuilder.setValue([])
    locksAndHandlesMatrixBuilder.addOrUpdateInput(customFormProcessor, 'ROOT')
    api.logInfo("DoorframesMatrix cleared because no products were found.")
}


api.logInfo("filteredRecordsProductDoorframes", filteredRecordsProductDoorframes)
api.logInfo("filteredRecordsProductLocksAndHandles", filteredRecordsProductLocksAndHandles)

// 9. Create a boolean entry in the form processor to hold
//    the state of whether changes should be saved.
api.inputBuilderFactory()
        .createBooleanUserEntry("saveChangesBoolean")
        .setLabel("Create new Products")
        .addOrUpdateInput(customFormProcessor, 'ROOT')