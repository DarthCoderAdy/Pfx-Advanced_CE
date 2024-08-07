def listPrice = out.ListPrice
def salesDiscountPct = out.SalesDiscountPct

if (null in [listPrice, salesDiscountPct]) {
    api.addWarning("Sales Discount cannot be calculated due to missing ListPrice or SalesDiscount%")
    return null
}

return listPrice * salesDiscountPct
