def ctx = api.getDatamartContext()

def dm = ctx.getDatamart("Transactions")

def query = ctx.newQuery(dm, true)
        .select("BusinessUnit")
        .select("ProductGroup")
        .select("SUM(InvoicePrice)", "Revenue")

        .orderBy("BusinessUnit", "ProductGroup")

def result = ctx.executeQuery(query)

return result?.getData()

