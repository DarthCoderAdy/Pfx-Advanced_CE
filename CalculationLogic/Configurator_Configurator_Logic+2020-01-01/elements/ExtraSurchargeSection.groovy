import net.pricefx.common.api.InputType

if (input.DeliveryType == "Extra") {
    def section = api.createConfiguratorEntry()
    def deliveryTypeInput = section.createParameter(InputType.USERENTRY, "ExtraSurcharge")

    if (deliveryTypeInput.getValue() < 3) {
        section.setMessage("<div style='color:red;'>" + "error message: Extra Surcharge must be at least 3" + "</div>")
    }
    return section
}