import net.pricefx.common.api.FieldFormatType
import net.pricefx.common.api.InputType

import net.pricefx.server.dto.calculation.ConfiguratorEntry
import net.pricefx.server.dto.calculation.ContextParameter

/**
 * Retrieves the main configurator entry based on a supplied CFO form ID.
 * If no form ID is supplied, it will attempt to retrieve the entry based on default behaviour.
 *
 * @param cfoFormId Optional identifier for the CFO form. If {@code null}, the function will execute using the default value.
 * @return the primary configurator entry or a default value if it does not exist.
 *
 * @throws any relevant exceptions here, if applicable
 */
ConfiguratorEntry getMainConfiguratorEntry(Long cfoFormId) {
    Script constantConfigLibrary = libs.sCFO_ProductDiscountLib.ConstConfig
    Map inputConfiguration = constantConfigLibrary.CFO_CONFIGURATOR_CONFIG

    def inputOptions = inputConfiguration.INPUTS.MGMT_OPTIONS
    def mgmtOptions = inputConfiguration.OPTIONS.MGMT_OPTIONS

    ContextParameter contextParameter = getContextParameter(inputOptions, mgmtOptions)
    return safeGetConfiguratorEntryWithDefaultValue(contextParameter, inputConfiguration, cfoFormId)
}

/**
 * This method constructs and returns a context parameter given input options and management options.
 *
 * @param inputOptions A dynamic object that contains information such as the name and label of the context parameter.
 *                     It is expected that this object has properties 'NAME' and 'LABEL'.
 * @param mgmtOptions A dynamic object that contains management options for the context parameter.
 *                    It is provided as an argument to the setOptions method while building the context parameter.
 * @return ContextParameter The built context parameter with provided options.
 * @throws IllegalArgumentException if inputOptions.NAME or inputOptions.LABEL is not provided.
 */
ContextParameter getContextParameter(def inputOptions, def mgmtOptions) {
    return api.inputBuilderFactory()
            .createOptionEntry(inputOptions.NAME)
            .setLabel(inputOptions.LABEL)
            .setOptions(mgmtOptions)
            .buildContextParameter()
}

/**
 * This method constructs a ConfiguratorEntry object based on the selected option from
 * the INPUT_CONFIG and processes it based on the value of that selection.
 *
 * @return ConfiguratorEntry The constructed ConfiguratorEntry object
 */
ConfiguratorEntry getChangeDataConfiguratorEntry() {
    ConfiguratorEntry changeDataSection = api.createConfiguratorEntry()
    Map inputConfig = libs.sCFO_ProductDiscountLib.ConstConfig.CFO_CONFIGURATOR_CONFIG
    createInputOption(changeDataSection, 'ppTableInput', 'Select table', inputConfig.OPTIONS.TABLE_OPTIONS)

    String selectedTable = changeDataSection.getInputs()[0].value as String
    if (selectedTable == 'Discount') processDiscountTable(changeDataSection)
    else if (selectedTable == 'Product Family Mapping') processProductFamilyMapping(changeDataSection, inputConfig)
    else if (selectedTable == 'Discount Level Definition') processDiscountLevelDefinition(changeDataSection)
    return changeDataSection
}

/**
 * Processes a provided discount table. This process includes the addition of the discount table,
 * creation of an input option based on the unique product groups in the discount table, and
 * further processing of the selected group if it exists.
 *
 * @param changeDataSection the ConfiguratorEntry instance that holds change data information.
 */
void processDiscountTable(ConfiguratorEntry changeDataSection) {
    addDiscountTable(changeDataSection)
    List groupOptions = getDiscount()['productGroup'].unique()
    createInputOption(changeDataSection, 'productGroupOption', 'Select the value you want to change.', groupOptions)

    String selectedGroup = changeDataSection.getInputs()[2]?.value as String
    if (selectedGroup) {
        processDiscountGroup(changeDataSection, selectedGroup)
    }
}

/**
 * This method processes the discount group settings.
 *
 * @param changeDataSection The ConfiguratorEntry object that is used for the configuration.
 * @param selectedGroup The name of the group that has been selected.
 *
 * It begins by retrieving the discount information about the product family.
 * Then, it filters the product family to find all products that belong to the selected group.
 * It retrieves the discount level of these products and creates a list of strings.
 * Finally, it creates a series of input options for the new target and max discount percentages.
 */
void processDiscountGroup(ConfiguratorEntry changeDataSection, String selectedGroup) {
    def productsFamily = getDiscount()
    def discountLevels = productsFamily.findAll { it['productGroup'] == selectedGroup }['discountLevel'].collect { it.toString() }
    createInputOption(changeDataSection, 'discountLevelsOption', 'Select Discount Level', discountLevels)
    createIntegerInputEntry(changeDataSection, 'newTargetDiscountPct', 'New Target Discount %')
    createIntegerInputEntry(changeDataSection, 'newMaxDiscountPct', 'New Max Discount %')
}

/**
 * Processes the Product Family Mapping based on the provided configurator entry and user-selected input.
 * It adds a product family mapping table, creates an input option depending on user's selected column,
 * and creates a string input entry for the new name.
 *
 * @param changeDataSection The configurator entry to be processed.
 * @param inputConfig The input configuration map from user containing the input options.
 */
void processProductFamilyMapping(ConfiguratorEntry changeDataSection, Map inputConfig) {
    addProductFamilyMappingTable(changeDataSection)
    createInputOption(changeDataSection, 'changeGroupOrFamily', 'Select Product Group or Product Family', inputConfig.OPTIONS.PRODUCT_FAMILY_OPTIONS)

    String selectedColumn = changeDataSection.getInputs()[2].value as String
    if (selectedColumn == 'Product Group') {
        List groupOptions = getProductFamilyMapping()['productGroup'].unique()
        createInputOption(changeDataSection, 'productGroupOption', 'Select the value you want to change.', groupOptions)
    }
    if (selectedColumn == 'Product Family') {
        List familyOptions = getProductFamilyMapping()['productFamily'].unique()
        createInputOption(changeDataSection, 'productFamilyOption', 'Select the value you want to change.', familyOptions)
    }
    createStringInputEntry(changeDataSection, 'newName', 'New name')
}

/**
 * Processes discount level definition for a given configurator entry.
 *
 * Initializes the discount level definition table with the specified configurator data section.
 * Fetches unique product family options from the discount level definition and sets them as input options.
 * If a family has been selected, triggers a process for the selected family.
 *
 * @param changeDataSection Configurator entry which holds the data for processing.
 */
void processDiscountLevelDefinition(ConfiguratorEntry changeDataSection) {
    addDiscountLevelDefinitionTable(changeDataSection)
    List familyOptions = getDiscountLevelDefinition()['productFamily'].unique()
    createInputOption(changeDataSection, 'productFamilyOption', 'Select Product Family you want to change.', familyOptions)

    String selectedFamily = changeDataSection.getInputs()[2]?.value as String
    if (selectedFamily) processSelectedFamily(changeDataSection, selectedFamily)
}

/**
 * This method processes a selected product family and updates relevant configurations accordingly.
 *
 * @param changeDataSection       The ConfiguratorEntry where the change data is stored.
 * @param selectedFamily          The name of the selected product family.
 *
 * It first retrieves the discount level definitions for products.
 * It then finds the discount levels for the selected family from the definitions, and creates an input option for them in the changeDataSection.
 * Finally, it adds an input entry for the new minimum revenue in the changeDataSection.
 */
void processSelectedFamily(ConfiguratorEntry changeDataSection, String selectedFamily) {
    def productsFamily = getDiscountLevelDefinition()
    def discountLevels = productsFamily.findAll { it['productFamily'] == selectedFamily }['discountLevel'].collect { it.toString() }
    createInputOption(changeDataSection, 'discountLevelsOption', 'Select Discount Level', discountLevels)
    createIntegerInputEntry(changeDataSection, 'newMinRevenue', 'New Min Revenue')
}

/**
 * The getAddGroupConfiguratorEntry method is used for creating a configurator entry and adding a new product group.
 * It also sets up parameters for the input matrix and discount configuration.
 *
 * @return  ConfiguratorEntry - A complex configurator entry with options for inputs and parameter configurations.
 */
ConfiguratorEntry getAddGroupConfiguratorEntry() {
    ConfiguratorEntry productGroupSection = api.createConfiguratorEntry()
    List<Object> familyNames = api.findLookupTableValues('ProductFamilyMapping')['value'].unique()

    createStringInputEntry(productGroupSection, 'productGroupInput', 'New Product Group')
    createInputOption(productGroupSection, 'assignProductFamily', 'Assign Product Family', familyNames)

    def newProductGroup = productGroupSection.getInputs()[0].value as String

    def table = productGroupSection.createParameter(InputType.INPUTMATRIX, 'New Discount')
    table.addParameterConfigEntry('columns', ['productGroup', 'discountLevel', 'targetDiscount', 'maxDiscount', 'id'])
    table.addParameterConfigEntry('hiddenColumns', ['id'])
    table.addParameterConfigEntry('columnLabels', ['Product Group', 'Discount Level', 'Target Discount %', 'Max Discount %'])
    table.addParameterConfigEntry('columnType', ['Option', 'Option', 'Numeric', 'Numeric'])
    table.addParameterConfigEntry('columnValueOptions', ['discountLevel': ['1', '2', '3', '4', '5', '6'], 'productGroup': [newProductGroup]])
    table.addParameterConfigEntry('requiredColumns', ['productGroup', 'discountLevel', 'targetDiscount', 'maxDiscount'])

    return productGroupSection
}

/**
 * This method is used to get the configurator entry for adding a product family.
 * It creates a section for setting up the product family where users specify the family name,
 * optionally assign it to an existing product group, and set up the discount levels.
 *
 * @return ConfiguratorEntry the configured entry with product family details.
 */
ConfiguratorEntry getAddFamilyConfiguratorEntry() {
    ConfiguratorEntry productFamilySection = api.createConfiguratorEntry()

    createStringInputEntry(productFamilySection, 'productFamilyInput', 'New Product Family')

    productFamilySection.createParameter(InputType.BOOLEAN, "assignToGroup")
            .setLabel('Assign new Product Family to an already existing Product Group.')

    if (productFamilySection.getInputs()[1].value) {
        List groupNames = api.findLookupTableValues('ProductFamilyMapping')['name'].unique()
        createInputOption(productFamilySection, 'assignProductGroup','Assign to:', groupNames as List<String>)
    }

    def newProductFamily = productFamilySection.getInputs()[0].value as String

    def table = productFamilySection.createParameter(InputType.INPUTMATRIX, 'New Discount Levels')
    table.addParameterConfigEntry('columns', ['productFamily', 'discountLevel', 'minRevenue', 'id'])
    table.addParameterConfigEntry('hiddenColumns', ['id'])
    table.addParameterConfigEntry('columnLabels', ['Product Family', 'Discount Level', 'Min Revenue'])
    table.addParameterConfigEntry('columnType', ['Option', 'Option', 'Numeric'])
    table.addParameterConfigEntry('columnValueOptions', ['discountLevel': ['1', '2', '3', '4', '5', '6'], 'productFamily': [newProductFamily]])
    table.addParameterConfigEntry('requiredColumns', ['productFamily', 'discountLevel', 'minRevenue'])
    return productFamilySection
}

/**
 * Constructs and initializes a new input option for the ConfiguratorEntry object
 *
 * @param changeDataSection the ConfiguratorEntry object for which the input option will be created
 * @param name              the name of the input option
 * @param label             the label of the input option
 * @param options           a List containing the possible values for the input option
 */
void createInputOption(ConfiguratorEntry changeDataSection, String name, String label, List options) {
    changeDataSection.createParameter(InputType.OPTION, name)
            .setLabel(label)
            .setValueOptions(options)
            .setRequired(true)
}

/**
 * Constructs and initializes a new string input entry for the ConfiguratorEntry object
 *
 * @param changeDataSection the ConfiguratorEntry object for which the string input entry will be created
 * @param name              the name of the string input entry
 * @param label             the label of the string input entry
 */
void createStringInputEntry(ConfiguratorEntry changeDataSection, String name, String label) {
    changeDataSection.createParameter(InputType.STRINGUSERENTRY, name)
            .setLabel(label)
            .setRequired(true)
}

/**
 * Constructs and initializes a new integer input entry for the ConfiguratorEntry object
 *
 * @param changeDataSection the ConfiguratorEntry object for which the integer input entry will be created
 * @param name              the name of the integer input entry
 * @param label             the label of the integer input entry
 */
void createIntegerInputEntry(ConfiguratorEntry changeDataSection, String name, String label) {
    changeDataSection.createParameter(InputType.INTEGERUSERENTRY, name)
            .setLabel(label)
            .setRequired(true)
}

protected ConfiguratorEntry safeGetConfiguratorEntryWithDefaultValue(ContextParameter contextParameter, Map inputConfig, Long cfoFormId = null) {
    return api.createConfiguratorEntry().createParameter(contextParameter) {
        String defaultValue = libs.sCFO_ProductDiscountLib.CfoConfiguratorUtils.extractCustomFormAttribute(inputConfig, cfoFormId)

        Map result = null
        if (defaultValue) {
            try {
                result = api.jsonDecode(defaultValue)
            } catch (any) {
                api.logWarn('Cannot decode default value for [$inputConfig.NAME] input. Provided input filter may be broken.')
            }
        }

        return result
    }
}

protected String extractCustomFormAttribute(Map inputConfig, Long cfoFormId) {
    if (!cfoFormId) {
        return
    }
    String attributeName = inputConfig.ATTRIBUTE_NAME
    return api.find('CFO', 0, 1, null, [attributeName], Filter.equal('id', cfoFormId)).getAt(0).getAt(attributeName)
}

/**
 * This method is responsible for adding a product family mapping table to the given configurator entry.
 * It uses the 'Product Family Mapping' from the inputBuilder factory to create a configuration of a matrix with certain parameters.
 * The matrix includes columns 'productGroup', 'productFamily', and 'typedId'.
 * Also, it sets several configuration parameters, like 'columnLabels', 'columnType', 'hiddenColumns', 'readOnlyColumns', and more.
 *
 * @param changeDataSection The ConfiguratorEntry instance to which the product family mapping table is to be added.
 */
void addProductFamilyMappingTable(ConfiguratorEntry changeDataSection) {
    def rows = getProductFamilyMapping()

    ContextParameter inputMatrix = api.inputBuilderFactory()
            .createInputMatrix('Product Family Mapping')
            .setColumns(['productGroup', 'productFamily', 'typedId'])
            .setLabel('Product Family Mapping')
            .setTitle('Product Family Mapping')
            .setValue(rows)
            .buildContextParameter()

    inputMatrix.addParameterConfigEntry('columnLabels', ['Product Group', 'Product Family'])
    inputMatrix.addParameterConfigEntry('columnType', ['Text', 'Text'])
    inputMatrix.addParameterConfigEntry('hiddenColumns', ['typedId'])
    inputMatrix.addParameterConfigEntry('readOnlyColumns', ['productGroup', 'productFamily'])
    inputMatrix.addParameterConfigEntry('hideAddButton', true)
    inputMatrix.addParameterConfigEntry('hideRemoveButton', true)
    inputMatrix.addParameterConfigEntry('disableRowSelection', true)

    changeDataSection.createParameter(inputMatrix)
}

/**
 * Retrieves the mapping for Product Family.
 *
 * The method uses the 'ProductFamilyMapping' lookup table to extract the relevant data.
 * A set of filters is applied to find the 'LTV' table where the 'lookupTable.id' equals the id of 'ProductFamilyMapping'.
 * The method then transforms the result into a collection of Maps containing 'productGroup', 'productFamily', and 'typeId'.
 *
 * @return a List of Maps containing 'productGroup', 'productFamily', and 'typeId'.
 */
def getProductFamilyMapping() {
    def id = api.findLookupTable('ProductFamilyMapping').id
    def filters = [Filter.equal('lookupTable.id', id)]
    def discountTable = api.find('LTV', 0,0,null, *filters)

    def rows = discountTable.collect {
        ['productGroup' : it.name,
         'productFamily': it.value,
         'typeId'       : it.typedId]
    }

    return rows
}

/**
 * Populates an Input Matrix with the discount level definition information
 * and adds it to the changeDataSection.
 *
 * The Input Matrix will have the following details:
 * Title : 'Discount Level Definition',
 * Label : 'Discount Level Definition',
 * Columns : 'productFamily', 'discountLevel', 'minRevenue', 'typedId'
 *
 * Configurations added to the Input Matrix:
 * - Column Labels : 'Product Family', 'Discount Level', 'Min Revenue'
 * - Column Types  : 'Text', 'Numeric', 'Numeric'
 * - Hidden Columns: 'typedId'
 * - Read-Only Columns : 'productFamily', 'discountLevel', 'minRevenue'
 * - Hide Add Button: true
 * - Hide Remove Button: true
 * - Disable Row Selection: true
 *
 * After building the Input Matrix and adding all configurations,
 * it is added to the changeDataSection.
 *
 * @param changeDataSection ConfiguratorEntry object to which the Input Matrix is added
 */
void addDiscountLevelDefinitionTable(ConfiguratorEntry changeDataSection) {
    def rows = getDiscountLevelDefinition()

    ContextParameter inputMatrix = api.inputBuilderFactory()
            .createInputMatrix('Discount Level Definition')
            .setColumns(['productFamily', 'discountLevel', 'minRevenue', 'typedId'])
            .setLabel('Discount Level Definition')
            .setTitle('Discount Level Definition')
            .setValue(rows)
            .buildContextParameter()

    inputMatrix.addParameterConfigEntry('columnLabels', ['Product Family', 'Discount Level', 'Min Revenue'])
    inputMatrix.addParameterConfigEntry('columnType', ['Text', 'Numeric', 'Numeric'])
    inputMatrix.addParameterConfigEntry('hiddenColumns', ['typedId'])
    inputMatrix.addParameterConfigEntry('readOnlyColumns', ['productFamily', 'discountLevel', 'minRevenue'])
    inputMatrix.addParameterConfigEntry('hideAddButton', true)
    inputMatrix.addParameterConfigEntry('hideRemoveButton', true)
    inputMatrix.addParameterConfigEntry('disableRowSelection', true)

    changeDataSection.createParameter(inputMatrix)
}

/**
 * This method retrieves the `DiscountLevelDefinition` from the lookup table and converts it into a list of maps.
 * Each map in the list represents a `DiscountLevelDefinition` row with the keys as column names.
 *
 * @return A list of maps where each map represents a `DiscountLevelDefinition` row.
 *         Each row-map contains following properties:
 *         - productFamily: Represents the first key of the lookup table
 *         - discountLevel: Represents the second key of the lookup table
 *         - minRevenue: Represents the first attribute in the lookup table
 *         - typedId: Represents the typed ID of the lookup table
 *
 * @throws ApiException if there's any problem with the API call
 */
List getDiscountLevelDefinition() {
    def id = api.findLookupTable('DiscountLevelDefinition').id
    def filters = [ Filter.equal('lookupTable.id', id) ]
    def discountTable = api.find('MLTV2', 0,0,null, *filters)

    def rows = discountTable.collect({
        [productFamily: it.key1,
         discountLevel: it.key2,
         minRevenue   : it.attribute1,
         typedId      : it.typedId]
    })
    return rows
}

/**
 * This method is used to add a discount table to a data configuration section.
 *
 * It constructs a data table with necessary information, label, title and value from the
 * discount product. Then it sets additional parameter entries such as columnLabels, columnType,
 * hiddenColumns, readOnlyColumns, hideAddButton, hideRemoveButton, disableRowSelection.
 * Finally adds it to the changeDataSection.
 *
 * @param changeDataSection The ConfiguratorEntry object in which the inputMatrix is created,
 *                          that is, the data configuration section where the discount table should be added
 */
void addDiscountTable(ConfiguratorEntry changeDataSection) {
    def rows = getDiscount().collect({
        [productGroup  : it.productGroup,
         discountLevel : it.discountLevel,
         targetDiscount: it.targetDiscount * 100,
         maxDiscount   : it.maxDiscount * 100,
         typedId       : it.typedId]
    })

    ContextParameter inputMatrix = api.inputBuilderFactory()
            .createInputMatrix('Discount')
            .setColumns(['productGroup', 'discountLevel', 'targetDiscount', 'maxDiscount', 'typedId'])
            .setLabel('Discount')
            .setTitle('Discount')
            .setValue(rows)
            .buildContextParameter()

    inputMatrix.addParameterConfigEntry('columnLabels', ['Product Group', 'Discount Level', 'Target Discount %', 'Max Discount %'])
    inputMatrix.addParameterConfigEntry('columnType', [FieldFormatType.TEXT, FieldFormatType.NUMERIC, FieldFormatType.PERCENT, FieldFormatType.PERCENT])
    inputMatrix.addParameterConfigEntry('hiddenColumns', ['typedId'])
    inputMatrix.addParameterConfigEntry('readOnlyColumns', ['productGroup', 'discountLevel', 'targetDiscount', 'maxDiscount'])
    inputMatrix.addParameterConfigEntry('hideAddButton', true)
    inputMatrix.addParameterConfigEntry('hideRemoveButton', true)
    inputMatrix.addParameterConfigEntry('disableRowSelection', true)

    changeDataSection.createParameter(inputMatrix)
}

/**
 * Retrieves the discount table from a lookup table named "Discount" in the system.
 *
 * This method fetches the discount lookup table by its name, applies a filter to match
 * the ID of the retrieved table, and retrieves the corresponding rows. Each row represents
 * a specific product's discount configuration, including the product group, the discount level,
 * the target and maximum discount levels, and the typed ID of this configuration.
 *
 * @return A list of maps where each map represents a row in the table. Each map includes:
 * - productGroup: The group to which the product belongs.
 * - discountLevel: The level of discount for the product group.
 * - targetDiscount: The target discount level for the product group.
 * - maxDiscount: The maximum discount level for the product group.
 * - typedId: The typed ID for this discount configuration.
 */
def getDiscount() {
    def id = api.findLookupTable("Discount").id
    def filters = [ Filter.equal('lookupTable.id', id) ]
    def discountTable = api.find('MLTV2', 0, 0, null, *filters)

    def rows = discountTable.collect {
        [
                productGroup  : it.key1,
                discountLevel : it.key2,
                targetDiscount: it.attribute1,
                maxDiscount   : it.attribute2,
                typedId       : it.typedId]
    }
    return rows
}

def debugTool(def changeDataSection) {
    def inputs = changeDataSection.getInputs()
    def debugValue = "#DataChangeConfigurator $inputs"
    changeDataSection.createParameter(InputType.TEXTUSERENTRY, 'debugTool')
            .setValue(debugValue)
            .setAlwaysEditable(false)
}