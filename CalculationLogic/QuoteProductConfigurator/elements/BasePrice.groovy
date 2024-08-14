def productSku = out.ProductId

def filters = [
        Filter.equal("sku", productSku),
        Filter.equal("name", "ProductBaseValues")
]

def basicValue = api.find("PX3", 0, 0, null, ['attribute2'], *filters)

if (basicValue == []) {
    return null
}

return basicValue?.attribute2?.getAt(0) as BigDecimal