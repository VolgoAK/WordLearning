package com.attiladroid.data.entities

import com.google.gson.annotations.Expose


/**
 * Created by alex on 1/3/18.
 */

class Dictionary {

    @Expose
    var sets: List<Set>? = null
    @Expose
    var themes: List<Theme>? = null
}
