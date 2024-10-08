def pgId = input.pgId
api.logInfo("Dashboard LivePriceGrid Export for PG ID: ", pgId)

def matrix = api.newMatrix("SKU", "Price", "Currency")

if (pgId) {
    def rowIterator = api.stream("PGI", "sku", ["sku", "resultPrice", "currency"], Filter.equal("priceGridId", pgId))?.withCloseable { iterator ->
        iterator.each { pgItem ->
            matrix.addRow(pgItem.sku, pgItem.resultPrice, pgItem.currency)
        }
    }
    return rowIterator
}

return matrix