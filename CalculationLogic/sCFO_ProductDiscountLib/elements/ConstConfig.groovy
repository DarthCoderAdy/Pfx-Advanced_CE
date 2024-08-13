import groovy.transform.Field

//Configurator Name and Values
@Field final String CFO_CONFIGURATOR_NAME = 'LogicsCFO_ProductDiscountHeader_Configurator'
@Field Map CFO_CONFIGURATOR_PASSED_VALUES = [CFO_FORM_ID: 'cfoFormId']

//These define the names of the Company Parameters tables
@Field String PRODUCT_FAMILY = 'Product Family Mapping'
@Field String DISCOUNT_LEVEL = 'Discount Level Definition'
@Field String DISCOUNT       = 'Discount'
@Field List PP_TABLES = [PRODUCT_FAMILY, DISCOUNT_LEVEL, DISCOUNT]

//Management options: define different management operations that can be performed.
@Field String CHANGE_DATA = 'Change data'
@Field String NEW_GROUP = 'Add new Product Group'
@Field String NEW_FAMILY = 'Add new Product Family'
@Field List CFO_MANAGEMENT_OPTIONS = [CHANGE_DATA, NEW_GROUP, NEW_FAMILY]

//productOrFamily
@Field String FAMILY = 'Product Family'
@Field String GROUP = 'Product Group'
@Field List PRODUCT_FAMILY_OPTIONS = [FAMILY, GROUP]

@Field Map CFO_CONFIGURATOR_CONFIG = [
        INPUTS                           : [MGMT_OPTIONS              : [LABEL         : 'Please select the management option.',
                                                                         NAME          : 'managementOptionsInput',
                                                                         ATTRIBUTE_NAME: 'managementOptions']],
        OPTIONS                          : [MGMT_OPTIONS              : CFO_MANAGEMENT_OPTIONS,
                                            TABLE_OPTIONS             : PP_TABLES,
                                            PRODUCT_FAMILY_OPTIONS    : PRODUCT_FAMILY_OPTIONS],
        PARAGRAPH_MESSAGE_MAIN           : '<h2>Products Discount Management Options</h2><p>What do you want to do? Please select the appropriate option from the list below.</p><hr>',
        PARAGRAPH_MESSAGE_CHANGE         : '<h2>Change Data</h2><p>Please modify the tables below.</p><hr>',
        PARAGRAPH_MESSAGE_ADD_GROUP      : '<h2>Add a new Product Group.</h2><p>Choose the name for the new product group.</p><hr>',
        PARAGRAPH_MESSAGE_ADD_FAMILY     : '<h2>Add a new Product Family.</h2><p>Choose the name for the new product family.</p><hr>',
        PARAGRAPH_MESSAGE_ADD_DISCOUNT   : '<h2>Add a new Discount.</h2><p>Set a value of the new Discount.</p><hr>',
]
