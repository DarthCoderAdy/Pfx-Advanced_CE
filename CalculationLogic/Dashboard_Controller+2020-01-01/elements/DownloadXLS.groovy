def controller = api.newController()

controller.addHTML("<h2>Download XLS report</h2>")

def pg = api.find("PG")?.find()
api.trace(pg)
def payLoad = api.jsonEncode(
        [
                pgId : pg?.id
        ]
)
api.trace(payLoad)
controller.addDownloadButton("DownloadXLS of LPG '${pg.label}'",
        "/formulamanager.executeformula/DashboardPricelistExport?output=xls", payLoad)

return controller