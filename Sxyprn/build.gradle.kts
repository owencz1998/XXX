// use an integer for version numbers
version = 2

cloudstream {
    // All of these properties are optional, you can safely remove them

    description = "sxyprn"
    authors = listOf("Coxju, megix")

    /**
    * Status int as the following:
    * 0: Down
    * 1: Ok
    * 2: Slow
    * 3: Beta only
    * */
    status = 1 // will be 3 if unspecified

    // List of video source types. Users are able to filter for extensions in a given category.
    // You can find a list of avaliable types here:
    // https://recloudstream.github.io/cloudstream/html/app/com.lagradost.cloudstream3/-tv-type/index.html
    tvTypes = listOf("NSFW")

    iconUrl = "https://www.google.com/url?sa=t&source=web&rct=j&opi=89978449&url=https://www.sxyprn.com/New.html%3Fpage%3D150&ved=2ahUKEwj9kIGspbKGAxWGU0EAHZCYChAQFnoECA8QAQ&usg=AOvVaw0JnTRXdF_iswS6XRtqa_JK"

    language = "en"
}
