def calculationFlowTraitPayload(String calculationFlowName) {
    def filters = [
            Filter.equal("uniqueName", calculationFlowName),
            Filter.equal("draft", false)
    ]
    def cf = api.find("CF", *filters)?.getAt(0)

    if (cf == null) {
        //api.throwException("Cannot find Calculation Flow '${calculationFlowName}")
        return null
    }


    def flowId = cf?.flowId
    def flowConfiguration = cf ? api.jsonDecode(cf.cofiguration) : null
    def entries = flowConfiguration?.entries
    def flowItemId = entries?.getAt(0)?.id

    def callPayloadMap = [
            data          : [
                    flowId       : flowId,
                    flowItemId   : flowItemId,
                    traitType    : "START_IMMEDIATELY",
                    configuration: [],
            ],
            oldValues     : null,
            operationType : "add",
            textMatchStyle: "exact",
    ]
    return api.jsonEncode(callPayloadMap)
}