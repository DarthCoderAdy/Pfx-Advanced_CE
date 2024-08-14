import net.pricefx.common.apibuilder.clicoll.LineItemHelper

if (quoteProcessor.isPrePhase()) {
    return
}

List<LineItemHelper.LineItem> lineItems = quoteProcessor.quoteView.lineItems.findAll {
    !it.folder
}

BigDecimal sum = 0.0
List<String> warnings = null

for (lineItem in lineItems) {

    BigDecimal price = lineItem?.outputs?.find {
        lineItemOtuput -> lineItemOtuput.resultName == "InvoicePrice"
    }?.result

    if (price == null) {
        sum = null
        warnings = ["Unable to calculate: value for TotalInvoicePrice is " +
                            "missing on one of the line items."]
        break
    }

    sum += price

}

def output = [
        resultName   : "TotalInvoicePrice",
        resultLabel  : "Total Invoice Price",
        result       : sum,
        formatType   : "MONEY_EUR",
        resultType   : "SIMPLE",
        cssProperties: "background-color:#99FFDD",
        warnings     : warnings
]

quoteProcessor.addOrUpdateOutput(output)
