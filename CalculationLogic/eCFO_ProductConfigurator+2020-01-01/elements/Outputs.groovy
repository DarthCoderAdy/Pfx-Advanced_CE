import net.pricefx.common.api.FieldFormatType

// 1. If the form processor is in pre-phase,
//    exit from the function
if (customFormProcessor.isPrePhase()) {
    return
}

// 2. Get the current item from the API
def currentItem = api.currentItem()
api.trace("currentItem", currentItem)

// 3.Find the 'LocksAndHandlesMatrix' values
//   from the currentItem's inputs.
//   Assign these values to the LocksAndHandles collection.
def lockAndHandles = currentItem.inputs.find { it.name == "LocksAndHandlesMatrix" }.value

// 4.Find the 'DoorframesMatrix' values from the currentItem's inputs.
//   Assign these values to the doorframes collection.
def doorframes = currentItem.inputs.find { it.name == "DoorframesMatrix" }.value

// 5. Use nested loops to add new door product to the newDoors collection
def newDoors = []

// Iterate over the doorframes collection.
doorframes.each { doorframe ->
    // Configure and assign a label to the doorframe item by using its properties.
    // Calculate price for the doorframe item by using its properties.
    // Use calculateDoorPrice and labelConfigurator methods
    def labelDoorFrame = doorframe?.get("Label") ?: null
    def doorSizeDoorFrame = doorframe?.get("Door size") ?: null
    def materialDoorFrame = doorframe?.get("Material") ?: null
    def colourDoorFrame = doorframe?.get("Colour") ?: null
    def basePriceDoorFrame = doorframe?.get("Base Price") ?: null

    def complexLabelDoorFrame = Utils.labelConfigurator(labelDoorFrame, doorSizeDoorFrame, materialDoorFrame, colourDoorFrame)
    def complexPriceDoorFrame = Utils.calculateDoorPrice(basePriceDoorFrame, doorSizeDoorFrame, materialDoorFrame, colourDoorFrame)

    // Iterate over the lockAndHandles collection.
    lockAndHandles.each { lockAndHandle ->
        // Configure and assign a label to the lockAndHandle item by using its properties.
        // Calculate price for the lockAndHandle item by using its properties.
        // Use calculateDoorPrice and labelConfigurator methods
        def labelLockHandles = lockAndHandle?.get("Label") ?: null
        def materialLockHandles = lockAndHandle?.get("Material") ?: null
        def colourLockHandles = lockAndHandle?.get("Colour") ?: null
        def basePriceLockHandles = lockAndHandle?.get("Base Price") ?: null

        def complexLabelHandle = Utils.labelConfigurator(labelLockHandles, null, materialLockHandles, colourLockHandles)
        def complexPriceHandle = Utils.calculateDoorPrice(basePriceLockHandles, null, materialLockHandles, colourLockHandles)

        // Concatenate doorframeLabel and lockHandleLabel separated by a slash.(newLabel)
        def newLabel = "${complexLabelDoorFrame} / ${complexLabelHandle}"
        api.trace("newLabel", newLabel)

        //Calculate the total price by adding doorframePrice and lockHandlePrice . (newPrice)
        def newPrice = complexPriceDoorFrame + complexPriceHandle

       // Create a row for the new door product with its ID, label, and base price.
      def singleRow = [
              'Product Id': Utils.generateNewProductId(),
               'Label'     : newLabel,
               'Base Price': newPrice
       ]

        // Add the new door product to the newDoors collection.
        newDoors.add(singleRow)
    }
}

api.trace("newDoors", newDoors)

// 6. Generate a new matrix for the new products
def newProducts = api.newMatrix("Product Id", "Label", "Base Price")
newProducts.setColumnFormat('Product Id', FieldFormatType.TEXT)
newProducts.setColumnFormat('Label', FieldFormatType.TEXT)
newProducts.setColumnFormat('Base Price', FieldFormatType.INTEGER)

// 7. Add the generated newDoors to de newProducts matrix
newDoors.each { row ->
    def newRow = [
            'Product Id': row["Product Id"],
            'Label': row["Label"],
            'Base Price': row["Base Price"]
    ]
    newProducts.addRow(newRow)
}

// 8. Add or update the output of the processed form by
//    establishing a new "discountMatrix" result
customFormProcessor.addOrUpdateOutput(
        [
                "resultName" : "discountMatrix",
                "resultLabel": "Discount Table",
                "resultType" : "MATRIX",
                "result"     : newProducts,
        ]
)

// 9. return the generated rows
return newDoors