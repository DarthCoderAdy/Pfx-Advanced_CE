// TODO: provide real data in out.Data

final String EMPTY_BUSINESS_UNIT = "No Name BU"
final String EMPTY_PRODUCT_GROUP = "No Name PG"

def data = out.Data.collect { row->
    [
            BusinessUnit : row.BusinessUnit ?: EMPTY_BUSINESS_UNIT,
            ProductGroup : row.ProductGroup ?: EMPTY_PRODUCT_GROUP,
            Revenue : row.Revenue
    ]
}
.groupBy { row ->
    row.BusinessUnit
}
api.trace(data)
def seriesData = data.collect {
    [
            name: it.key,
            y: it.value.sum {it.Revenue},
            drilldown: it.key,
    ]
}

api.trace(seriesData)
// TODO: provide real drill-down data in out.Data
def drilldownSeriesData = data.collect {
        [
                name: it.key,
                id  : it.key,
                data: it.value.collect { row ->
                    [ //can be defined as map of list
                          row.ProductGroup,
                          row.Revenue,
                        ]
                    }
                ]
}
def definition = [
        chart      : [
                type: 'column',
        ],

        title      : [
                text: 'Revenue per Business Unit and Product Group',
        ],

        subtitle   : [
                text: 'Click on the column to drill-down',
        ],

        legend     : [
                enabled: false,
        ],

        plotOptions: [
                series: [
                        borderWidth: 0,
                        dataLabels : [
                                enabled: true,
                                format : '{point.y:,.0f}' + out.Currency,
                        ]
                ],
        ],

        drilldown  : [
                series: drilldownSeriesData,
        ],

        series     : [
                [
                        name        : "Business Units",
                        colorByPoint: true,
                        data        : seriesData,
                ],
        ],

        tooltip    : [
                headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
                pointFormat : '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:,.0f} ' + out.Currency + '</b><br/>'
        ],

        xAxis      : [
                type: 'category',
        ],

        yAxis      : [
                title: [
                        text: 'Revenue',
                ]

        ],
]

return api.buildHighchart(definition)
