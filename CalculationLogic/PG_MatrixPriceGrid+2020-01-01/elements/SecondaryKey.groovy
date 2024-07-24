if (!api.isDebugMode()) {
    return api.getSecondaryKey()
} else {
    def volumeBreaks = api.findLookupTableValues("VolumeBreaks", "VolumeBreak")?.key3?.unique()
    if (api.isInputGenerationExecution()) {
        return api.option("VolumeBreak", volumeBreaks)
    } else {
        return input.VolumeBreak
    }
}
