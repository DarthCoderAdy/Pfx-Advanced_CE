def pgId = out.CurrentItem?.id

def specialAdjPctInputValue = api.jsonDecode(out.CurrentItem["configuration"])
        ?.headerInputs.find { it.name == "SpecialAdjPct" }
        ?.value ?: 0.0

if (pgId) {
    api.setPricegridCalculationOutput(pgId, "specialAdjPct", "Special Adj %", "${api.formatNumber('#,##0.00%', specialAdjPctInputValue)}", null)
    api.setPricegridCalculationOutput(pgId, "sumListPrice", "Sum List Price", "€ ${api.formatNumber('#,##0.00', out.Summary.sumListPrice)}", null)
    api.setPricegridCalculationOutput(pgId, "sumCost", "Sum Cost", "€ ${api.formatNumber('#,##0.00', out.Summary.sumCost)}", null)
    api.setPricegridCalculationOutput(pgId, "sumGrossMargin", "Sum Gross MArgin", "€ ${api.formatNumber('#,##0.00', out.Summary.sumGrossMargin)}", null)

}
//api.trace(specialAdjPctInputValue)
//api.trace(out.Summary.sumListPrice)
//api.trace(out.Summary.sumCost)
//api.trace(out.Summary.sumGrossMargin)