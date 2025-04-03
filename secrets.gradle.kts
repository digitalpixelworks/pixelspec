extra.apply {
    set(
        "admobAppId",
        project.findProperty("ADMOB_APP_ID")
            ?: "ca-app-pub-3940256099942544~3347511713" // Test ID for local builds
    )
    set(
        "admobBannerId",
        project.findProperty("ADMOB_BANNER_ID")
            ?: "ca-app-pub-3940256099942544/9214589741" // Test ID for local builds
    )
}