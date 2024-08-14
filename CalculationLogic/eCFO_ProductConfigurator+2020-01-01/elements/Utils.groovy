//Output.groovy methods
/**
 * This method is used to create a label configuration based on the provided parameters.
 *
 * @param label     The initial label.
 * @param doorSize  The size of the door. If it's available, it will be appended to the label after a dash.
 * @param material  The material of the door. If it's not empty/null, it will be appended to the label after a dash.
 * @param colour    The colour of the door. If defined, it will be appended to the label after a dash.
 * @return          A String that represents the new label. If the original label starts with 'Basic-',
 *                  it is replaced. Dash-separated size, material, and colour are appended if available.
 */
def labelConfigurator(def label, def doorSize, def material, def colour) {
    //Your code: check the requirements for calculation result table
    def newLabel = label?.replaceFirst(/^Basic-/, '')
    def parts = []
    if (doorSize) {
        parts << doorSize
    }
    if (material) {
        parts << material
    }
    if (colour) {
        parts << colour
    }
    if (!parts.isEmpty()) {
        newLabel += "-" + parts.join('-')
    }

    return newLabel
}

/**
 * Calculates the price of a door based on various parameters.
 *
 * @param basePrice  The base price of the door. It must be a valid number.
 * @param doorSize   The size of the door. If not provided, it defaults to "default".
 * @param material   The material of the door. If not provided, it defaults to "default".
 * @param colour     The colour of the door. If not provided, it defaults to "default".
 *
 * @return Returns the total price as a BigDecimal.
 *         The price is calculated as the base price multiplied by the total increase percent which includes the material,
 *         color and door size price increments.
 */
def calculateDoorPrice(basePrice, doorSize, material, colour) {
    //Your code: check the requirements for calculation result table
    doorSize = doorSize ?: "default"
    material = material ?: "default"
    colour = colour ?: "default"

    def tableDoorsize = api.findLookupTableValues("DoorSizeAdjustment", Filter.equal("name", doorSize))?.getAt(0)?.value
    def tableMaterial = api.findLookupTableValues("MaterialAdjustment", Filter.equal("name", material))?.getAt(0)?.value
    def tableColour = api.findLookupTableValues("ColorAdjustment", Filter.equal("name", colour))?.getAt(0)?.value


    BigDecimal doorSizeMultiplied = basePrice * tableDoorsize
    BigDecimal materialMultiplied = basePrice * tableMaterial
    BigDecimal colourMultiplied = basePrice * tableColour
    BigDecimal sum = basePrice + doorSizeMultiplied + materialMultiplied + colourMultiplied

    return sum
}

/**
 * This function generates a new product Id in a specific format.
 * The format is "DOORSET-" followed by a UUID of length 10 characters.
 * The UUID is generated using an external API.
 *
 * @return A New formatted Product ID string
 */
def generateNewProductId(def productId) {
    //Your code: check the requirements for calculation result table
    def uuid = api.uuid(10)
    return "DOORSET-${uuid}"
}

// SaveData.groovy methods
/**
 * This method creates information messages about products and their base values.
 * Then, it uses this data to set an alert message through the `api`.
 *
 * @param addedProducts  - A list of products represented as Maps where each Map should contain a "LABEL" key. The values of "LABEL" are used to construct a part of the information message.
 * @param addedBaseValue - A list of product base values represented as Maps where each Map should contain "LABEL" and "BASE_PRICE" keys. The values of these keys are used to construct a part of the information message.
 *
 * Note: Any changes or operations executed inside this method does not affect original list of maps passed as `addedProducts` and `addedBaseValue`.
 */
def addInfoMessage(addedProducts, addedBaseValue) {
    addedProducts  = addedProducts.collect({
        it["Label"]
    }).join("<br>")

    addedBaseValue  = addedBaseValue.collect({
        it["Label"]+", Base Price: "+it["Base Price"]
    }).join("<br>")

    String productInfo = "<br>Following products has been added to Product Table: <br>$addedProducts <br>"
    String valuesInfo = "<br>Following products has been added to Product Base Values Table: <br>$addedBaseValue<br>"
    String info = ""

    if (!addedBaseValue.isEmpty()) {
        info += valuesInfo
    }

    if (!addedProducts.isEmpty()) {
        info += productInfo
    }

    api.setAlertMessage(info)
}