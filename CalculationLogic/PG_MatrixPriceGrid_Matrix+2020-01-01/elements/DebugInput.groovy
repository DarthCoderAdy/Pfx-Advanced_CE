if (api.isDebugMode()) {
    if (api.isInputGenerationExecution()) {
        def volumeBreaks = api.findLookupTableValues("VolumeBreaks", "Region")?.key1?.unique()
        api.option("Region", volumeBreaks)
    }
}