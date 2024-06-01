// use an integer for version numbers
version = 10


cloudstream {
    language = "en"
    // All of these properties are optional, you can safely remove them

     description = "Series porn (use VPN if links not working)"
     authors = listOf("Sora")

    /**
     * Status int as the following:
     * 0: Down
     * 1: Ok
     * 2: Slow
     * 3: Beta only
     * */
    status = 1 // will be 3 if unspecified
    tvTypes = listOf(
        "NSFW",
    )

    iconUrl = "https://pandamovies.pw/wp-content/uploads/2023/04/pandamoviefavicon-1.png"
}