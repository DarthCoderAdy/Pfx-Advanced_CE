import net.pricefx.common.api.InputType

if (input.ShipTo) {
    def section = api.createConfiguratorEntry()
    def deliveryTypeInput = section.createParameter(InputType.OPTION, "DeliveryType")
            .setValueOptions(
                    api.namedEntities(api.findLookupTableValues("FreightSurcharge", Filter.equal("Country", input.ShipTo))
                    )?.collect { it.DeliveryType }?.sort()
            )
    return section
}