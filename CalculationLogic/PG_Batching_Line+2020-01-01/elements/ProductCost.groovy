def sku = api.product("sku")

def isNewBatch = api.global.currentBatch == null || !api.global.currentBatch.contains(sku)

if (isNewBatch) {
    api.global.currentBatch = api.getBatchInfo()?.collect { it.first() }?.unique() ?: ([sku] as Set)
}

if (isNewBatch) {
    api.logInfo("NewBatchOfSKUs: ", api.jsonEncode(api.global.currentBatch))


    def rowIterator = api.stream("PX3", "sku", ["sku", "attribute1"], Filter.equal("name", "ProductCost"), Filter.in("sku", api.global.currentBatch)).withCloseable { rowIterator ->
        api.global.productCosts = rowIterator?.collectEntries { [(it.sku): (it.attribute1 as BigDecimal)]}
    }
}
return api.global.productCosts[sku]