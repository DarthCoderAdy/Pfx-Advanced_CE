
/**
 * Returns a map with the quantities of each raw material that the product is composed of.
 * @param sku - sku
 * @param quantity - quantity
 */
Map<String, BigDecimal> getRawMaterialQuantities(String sku, BigDecimal quantity = 1) {
    Map<String, BigDecimal> quantities = [:]
    getRawMaterials(sku, quantity).each {
        if (!quantities[it.rawMaterial as String]) {
            quantities[it.rawMaterial as String] = 0.0
        }
        quantities[it.rawMaterial as String] += it.quantity
    }
    return quantities
}

/**
 * @param sku - sku
 * @param quantity - quantity
 * Recursively iterates the sub-components of a product.
 */
List<Object> getRawMaterials(String sku, BigDecimal quantity = 1) {
    List<Object> bomEntries = api.find("PBOME", 0, api.getMaxFindResultsLimit(), null, Filter.equal("sku", sku)).collect {
        [
                sku            : it.sku,
                quantity       : it.quantity,
                rawMaterial    : it.rawMaterial,
                subComponentSku: it.subComponentSku,
        ]
    }
    List<Object> rawMaterials = bomEntries.findAll {
        it?.rawMaterial
    }
    List<Object> subComponents = bomEntries.findAll {
        it?.subComponentSku
    }
    for (subComponent in subComponents) {
        List<Object> subComponentRawMaterials = getRawMaterials(subComponent.subComponentSku as String, subComponent.quantity as BigDecimal)
        for (subComponentRawMaterial in subComponentRawMaterials) {
            rawMaterials << subComponentRawMaterial
        }
    }
    for (rawMaterial in rawMaterials) {
        rawMaterial.quantity *= quantity
    }
    return rawMaterials
}
