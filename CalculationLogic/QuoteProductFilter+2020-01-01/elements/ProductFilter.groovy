if (quote.quoteType == "QuoteProductFilter_QuoteType") {
    def businessUnit = quote.inputs?.find {it.name == "BusinessUnit"}?.value
    return Filter.equal("attribute2", businessUnit)
}