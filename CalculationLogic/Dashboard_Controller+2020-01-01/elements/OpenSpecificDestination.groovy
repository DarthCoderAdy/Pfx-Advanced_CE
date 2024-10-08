//def controller = api.newController()
//
//controller.addHTML("<h2>Navigates to specific destination</h2>")
//controller.addHTML("<h3>Specific Data Tables</h3>")
//
//def ppTableName = api.find("LT", 0, 1, "uniqueName")?.uniqueName?.find()
//controller.addButton("Open PriceParameter '${ppTableName}'", "pricingParametersPage", api.findLookupTable(ppTableName)?.id)
//
//controller.addHTML("<h3 style='margin-top: 20px;'>Specific Documents</h3>")
//
//def priceList = api.find("PL", 0, 1, "label")?.find()
//controller.addButton("Open PriceList '${priceList?.label}'", "priceListPage", priceList?.id)
//
//def quote = api.find("Q", 0, 1, "uniqueName")?.find()
//controller.addButton("Open Quote '${quote?.uniqueName}'", "priceShopPage", quote?.uniqueName)
//
//controller.addHTML("<h3 style='margin-top: 20px;'>Specific Dashboard or Wizard</h3>")
//
//def dashboard = api.find("DB", Filter.isNull("hide"))?.find()
//controller.addButton("Open Dashboard '${dashboard?.uniqueName}'", "dashboardPage", null, dashboard?.uniqueName)
//
//def configurationWizard = api.find("CW", 0, 1, "uniqueName")?.find()
//controller.addButton("Open Configuration Wizards '${configurationWizard?.uniqueName}'", "configWizardPage", configurationWizard?.typedId)
//
//return controller

def controller = api.newController()


controller.addHTML("<h2>Navigates to specific destination</h2>")
controller.addHTML("<h3>Specific Data Tables</h3>")


def ppTableName = api.find("LT", 0, 1, "uniqueName")?.find()
api.trace("PP", ppTableName)
controller.addButton("Open PriceParameter '${ppTableName?.label}'", "pricingParametersPage", ppTableName?.typedId)


controller.addHTML("<h3 style='margin-top: 20px;'>Specific Documents</h3>")


def pricelist = api.find("PL", 0, 1, "label")?.find()
api.trace("PRICELIST", pricelist)
controller.addButton("Open Pricelist '${pricelist?.label}'", "priceListPage", pricelist?.id)

def quote = api.find("Q", 0, 1, "uniqueName")?.uniqueName?.find()
api.trace("QUOTE", quote)
controller.addButton("Open Quote '${quote}'", "priceShopPage", quote)


controller.addHTML("<h3 style='margin-top: 20px;'>Specific Dashboard or Wizard</h3>")


def dashboard = api.find("DB", Filter.isNull("hide"))?.uniqueName?.find()
api.trace("DASH", dashboard)
controller.addButton("Open Dashboard '${dashboard}'", "dashboardPage", null, dashboard)

def configurationWizard = api.find("CW", 0, 1, "uniqueName")?.typedId?.find()
api.trace("CONF", configurationWizard)
controller.addButton("Open Configuration Wizards '${configurationWizard}'", "configWizardPage", configurationWizard)

return controller