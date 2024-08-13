workflow.addWatcherStep("Sales Manger")
        .withUserWatchers("admin")
        .withReasons("Sales Manager needs to approve this form")

workflow.addApprovalStep("Sales Manager")
        .withPostStepLogic("customform_WorkflowPostStep")
        .withApprovers("admin")
        .withReasons("Sales Manager needs to approve this form")