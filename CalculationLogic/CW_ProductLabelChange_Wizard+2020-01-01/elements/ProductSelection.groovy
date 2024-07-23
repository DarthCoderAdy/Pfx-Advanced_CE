def sku = input[Const.PRODUCT_INPUT_NAME]

def inputField = api.inputBuilderFactory()
        .createProductEntry(Const.PRODUCT_INPUT_NAME)
        .setRequired(true)
        .setLabel("Please Select Product")
        .setValue(sku)
        .buildContextParameter()

def formSection = api.createConfiguratorEntry()
formSection.createParameter(inputField)

return formSection