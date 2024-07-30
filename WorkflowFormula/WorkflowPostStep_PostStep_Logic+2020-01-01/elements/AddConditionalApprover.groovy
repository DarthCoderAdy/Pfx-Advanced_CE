if (workflowHistory.activeStep.approved && workflowHistory.activeStep.uniqueName == "Managers") {
    api.logInfo("workflowHistory.activeStep", api.jsonEncode(workflowHistory.activeStep))

    if ("Price_Manager" in workflowHistory.activeStep.executedByUsers[0].allGroups.uniqueName) {
//    if (workflowHistory.activeStep.executedByUsers.any { user ->
//        user.allGroups.any { group -> group.uniqueName == "Price_Manager" }
//    }) {
        currentWorkflow.insertApprovalStep("Sales Manager")
        .withUserGroupApprovers("Sales_Manager")
    }
}