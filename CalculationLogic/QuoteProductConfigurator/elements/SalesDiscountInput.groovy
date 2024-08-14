final String INPUT_NAME = "Discount"
if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory()
            .createUserEntry(INPUT_NAME)
            .setRequired(true)
            .setLabel("Sales Discount (%)")
            .setFormatType("PERCENT")
            .setValue(0)
            .getInput()
} else {
    return input[INPUT_NAME]
}