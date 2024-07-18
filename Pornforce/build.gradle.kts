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
    iconUrl = "https://www.google.com/url?sa=t&source=web&rct=j&opi=89978449&url=https://pornforce.com/&ved=2ahUKEwiT9o3gz7GHAxVZQkEAHeZ5CcMQFnoECBQQAQ&usg=AOvVaw13Y_7fvmMkc_anqNFKgpE4"
}