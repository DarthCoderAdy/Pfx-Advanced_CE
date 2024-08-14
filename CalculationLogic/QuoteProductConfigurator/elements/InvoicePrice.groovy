if (out.BasePrice == null) {
    api.addWarning("Cannot calculate Invoice Price, "+
            "because Base Price is not available.")
    return
}

def basePrice = out.BasePrice ?: 0.0
def discount = out.Discount ?: 0.0
def quantity = out.Quantity ?: 0.0
def invoicePrice = basePrice * quantity

return invoicePrice - (invoicePrice*discount)