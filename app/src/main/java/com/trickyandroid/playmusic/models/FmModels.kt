package com.trickyandroid.playmusic.models


class FmModels {

    private var id: Int = 0
    private var fmSite: String? = null
    private var fmName: String? = null
    //    private var fmImage: Drawable? = null
    private var fmUrl: String? = null
    private var flag: Boolean? = null


    fun getId(): Int = id


    fun setId(id: Int) {
        this.id = id
    }

    fun getFmSite(): String = fmSite!!


    fun setFmSite(fmSite: String) {
        this.fmSite = fmSite
    }

    fun getFmName(): String = fmName!!


    fun setFmName(fmName: String) {
        this.fmName = fmName
    }

//    fun getFmImage(): Drawable {
//        return fmImage!!
//    }
//
//    fun setFmImage(fmImage: Drawable) {
//        this.fmImage = fmImage
//    }

    fun getFmUrl(): String =fmUrl!!


    fun setFmUrl(fmUrl: String) {
        this.fmUrl = fmUrl
    }

    fun getFlag(): Boolean? =flag


    fun setFlag(flag: Boolean?) {
        this.flag = flag
    }

}