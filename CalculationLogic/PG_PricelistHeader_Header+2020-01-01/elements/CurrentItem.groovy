def currentItem = api.currentItem()

if (api.isDebugMode()) {
    currentItem = [
            label               : "PriceListHeader",
            configuration       :
                    '''
                        {
                        "inputs": [],
                        "headerInputs" : [
                        {
                        "name" : "SpecialAdjPct",
                        "label" : "Enter Special Adjustment (in %)",
                        "type" : "USERENTRY",
                        "value" : 0.02
                        }
                        ],
                        "outputs" : [],
                        "outputChartDefinition" : {}
                        }
                    ''',
            priceGridType       : "SIMPLE",
            headerTypeUniqueName: "PriceListHeaderType",
            id                  : 4639
    ]
}
return currentItem