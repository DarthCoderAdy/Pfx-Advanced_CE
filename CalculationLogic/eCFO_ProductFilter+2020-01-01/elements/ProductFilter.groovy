def param = api.jsonDecode(filterFormulaParam)
def productGroup = param?.attribute1

List filters = []

if (productGroup) {
    filters.add(Filter.equal("attribute1", productGroup))
}

filters.add(Filter.like("label", "%Basic%"))

if (filters.isEmpty()) {
    return null
}

return Filter.and(*filters)
