if (out.ListPrice != null && out.Cost != null) {
    return out.ListPrice - out.Cost
}

api.addWarning("Cannot caclulate the Gross Margin, becasue either ListPrice or Cost is not available.")
return null

