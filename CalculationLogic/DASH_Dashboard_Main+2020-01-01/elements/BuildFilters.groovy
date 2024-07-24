def ctx = api.getDatamartContext()
def dm = ctx.getDatamart("Transactions")
ctx.dimFilterEntry(Const.INPUT_FIELD_YEAR, dm.getColumn("InvoiceDateYear"))
