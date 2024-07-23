if (api.isInputGenerationExecution()) {
    return api.inputBuilderFactory()
            .createConfiguratorInputBuilder(Constants.CONFIGURATOR_INPUT_NAME, Constants.CONFIGURATOR_LOGIC_NAME, true)
            .setLabel("Configure the Delivery")
            .getInput()
//    api.configurator(Constants.CONFIGURATOR_INPUT_NAME, Constants.CONFIGURATOR_LOGIC_NAME)
    //Set the label of the button, which will open the Configurator
//    api.getParameter(Constants.CONFIGURATOR_INPUT_NAME)?.setLabel("Configure the Delivery")
} else {
    return input[Constants.CONFIGURATOR_INPUT_NAME]
}