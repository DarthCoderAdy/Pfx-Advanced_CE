def sku = input[Const.PRODUCT_INPUT_NAME]

if (sku) {
    def description = api.product("label", sku)

    def inputField = api.inputBuilderFactory()
            .createStringUserEntry(Const.DESCRIPTION_INPUT_NAME)
            .setRequired(true)
            .setLabel("Set new Product Description")
            .setValue(description)
            .setNoRefresh(true)
            .buildContextParameter()

    def formSection = api.createConfiguratorEntry()
    formSection.createParameter(inputField)
    formSection.setMessage("<div style='font-size:12px; color:#555555'>" + "Original Label: ${description}</div>")

    return formSection
}