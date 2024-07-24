final String EMBEDDED_DASHBOARD_INPUT_FIELD_YEAR = "Year"
final String EMBEDDED_DASHBOARD_INPUT_FIELD_CUSTOMER_ID = "CustomerId"

return api.dashboard('DASH_Embedded')
        .setParam("Year", input[Const.INPUT_FIELD_YEAR]) // pass value of filter from Main to the Embedded dashboard
        .showEmbedded()
        .andRecalculateOn(api.dashboardWideEvent(Const.EVENT_NAME_SELECT_CUSTOMER)) //embedded dashboard will listen to this Event
        .withEventDataAttr(Const.EVENT_ATTRIBUTE_CUSTOMER_ID).asParam(EMBEDDED_DASHBOARD_INPUT_FIELD_CUSTOMER_ID) // pass the Event attribute value as parameter to the embedded dashboard
