def m = api.newMatrix("Name", "Value")

m.addRow("Year", input.Year) //Year value was passed from the calling Main dashboard
m.addRow("CustomerId", input.CustomerId) //CustomerId value was passed from the calling Main dashboard

return m