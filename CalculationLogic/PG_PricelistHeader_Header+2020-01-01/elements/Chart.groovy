def pgId = out.CurrentItem?.id

// color of the positive elements (green)
def colorUp = '#89a54e'

// color of the negative elements (red)
def colorDown = '#aa4643'

// explicitly specify the color, otherwise series.color will be used (blue)
def colorBalance = '#4573a7'

// TODO: provide real data in out.Data
def data = [
        [
                name : 'List Price',
                y    : out.Summary.sumListPrice,
                color: '#4573a7',
        ],
        [
                name: 'Cost',
                y   : -out.Summary.sumCost,
        ],
        [
                name: 'Gross Margin',
                y   : out.Summary.sumGrossMargin,
        ],

]

def definition = [
        chart   : [
                type: 'waterfall',
        ],

        title   : [
                text: 'Waterfall',
        ],

        subtitle: [
                text: '',
        ],

        legend  : [
                enabled: false,
        ],

        series  : [
                [
                        upColor     : colorUp,
                        color       : colorDown,
                        data        : data,
                        dataLabels  : [
                                enabled: true,
                                format : '{point.y:,.2f} EUR',
                                style  : [
                                        fontWeight: 'bold',
                                ],
                        ],
                        pointPadding: 0,
                ]
        ],

        tooltip : [
                pointFormat: '<b>{point.y:,.2f}</b> EUR',
        ],

        xAxis   : [
                // when type is 'category', series.data.name will become X-axis value
                type: 'category',
        ],

        yAxis   : [
                title: [
                        enabled: false,
                ],
        ],
]

return api.setPricegridCalculationChart(definition, pgId)
