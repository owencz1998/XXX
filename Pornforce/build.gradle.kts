version = 1

cloudstream {
    authors     = listOf("OwenConnor")
    language    = "en"
    description = "nsfw"

    /**
     * Status int as the following:
     * 0: Down
     * 1: Ok
     * 2: Slow
     * 3: Beta only
    **/
    status  = 1 // will be 3 if unspecified
    tvTypes = listOf("NSFW")
    iconUrl = "https://www.google.com/s2/favicons?domain=pornforce.com&sz=%size%"
}