final String INPUT_NAME = "Quantity"
if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory()
            .createIntegerUserEntry(INPUT_NAME)
            .setRequired(true)
            .setLabel("Quantity")
            .getInput()
} else {
    return input[INPUT_NAME]
}