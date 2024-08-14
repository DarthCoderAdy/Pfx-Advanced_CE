// 1. If the customFormProcessor is in the pre-phase,
//    end the function immediately
if (customFormProcessor.isPrePhase()) {
    return
}

// 2. get the current item from the api
def currentItem = api.currentItem()
api.trace("def currentItem = api.currentItem()", currentItem)

// 3. find 'saveChangesBoolean' from currentItem's inputs,
//    which determines whether changes should be saved
def save = currentItem.inputs.find { it.name == "saveChangesBoolean" }.value

// 4. If changes need to be saved, proceed
if (save) {
    // 5. get the output from the out.Output
    def output = out.Outputs

    // 7. create empty lists for addedBaseValue and addedProducts
    def addedBaseValue = []
    def addedProducts = []

    // 8. for each product in output
    output.each { product ->
        def label = product["Label"]
        def sku = product["Product Id"]
        def basePrice = product["Base Price"]

        api.trace("Product", product)
        // check if the product exists in Product Master table
        def productExistsInPMaster = api.find("P", 0, 0, null, Filter.equal("label", label))
        api.trace("productExistsInPMaster", productExistsInPMaster)
        // check if product base value exists in Product Extensions table
        def filters = [
                Filter.equal("name", "ProductBaseValues"),
                Filter.equal("label", label)
        ]
        def productExistsInPExtension = api.find("PX3", 0, 0, null, *filters)
        api.trace("productExistsInPExtension", productExistsInPExtension)

        // if the product does not exist in Product Master table
        // a) add the product
        // b) add the product to the list of added products
        if (productExistsInPMaster.isEmpty()) {
                def updateProductMap = [
                        "sku"       : sku,
                        "label"     : label,
                        "attribute1": "DoorSet",
                        "unitOfMeasure": "EA",
                        "currency": "EUR",
                        "attribute2": "BU-DoorSet",
                        "attribute3": "UNDEFINED",
                        "attribute4": "default",
                        "attribute5": "default",
                        "attribute6": "Introduction",
                ]
                api.add("P", updateProductMap)
            addedProducts << product
        }

        // if product base value does not exist in Product Extensions table
        // a) add product base value
        // b) add the product to the list of added base value
        if (productExistsInPExtension.isEmpty()) {
            def updateValueMap = [
                    "name"      : "ProductBaseValues",
                    "sku"       : sku,
                    "attribute1": label,
                    "attribute2": basePrice
            ]

            api.add("PX3", updateValueMap)
            addedBaseValue << product
        }
    }

    // add information message about the added products and base values
    api.trace("addedProducts", addedProducts)
    api.trace("addedBaseValue", addedBaseValue)
    Utils.addInfoMessage(addedProducts, addedBaseValue)
}