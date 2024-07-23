if (!api.isDebugMode()) {
    return api.getSecondaryKey()
}

if (api.isSyntaxCheck()) {
    api.option("CountryCode", api.findLookupTableValues("CountryAdjustment", null, ["name"], "name")?.name)
}

return input.CountryCode

