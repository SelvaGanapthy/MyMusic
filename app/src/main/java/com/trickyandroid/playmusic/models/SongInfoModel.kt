package com.trickyandroid.playmusic.models

import android.os.Parcel
import android.os.Parcelable

class SongInfoModel() : Parcelable {

    private var Id: Int = 0
    private var AlbumnewId: Int = 0
    private var ArtistId: Int = 0
    private var TrackId: Int = 0
    private var albumId: Long = 0
    private var SongFileId: String? = null
    private var songName: String? = null
    private var songArtist: String? = null
    private var songTime: String? = null
    private var songComposer: String? = null
    private var songMoviename: String? = null
    private var songPath: String? = null
    private var songImgPath: String? = null

    constructor(parcel: Parcel) : this() {
        Id = parcel.readInt()
        AlbumnewId = parcel.readInt()
        ArtistId = parcel.readInt()
        TrackId = parcel.readInt()
        albumId = parcel.readLong()
        SongFileId = parcel.readString()
        songName = parcel.readString()
        songArtist = parcel.readString()
        songTime = parcel.readString()
        songComposer = parcel.readString()
        songMoviename = parcel.readString()
        songPath = parcel.readString()
        songImgPath = parcel.readString()
    }

    fun getAlbumnewId(): Int = AlbumnewId

    fun setAlbumnewId(albumnewId: Int) {
        AlbumnewId = albumnewId
    }

    fun getArtistId(): Int = ArtistId


    fun setArtistId(artistId: Int) {
        ArtistId = artistId
    }

    fun getTrackId(): Int = TrackId

    fun setTrackId(trackId: Int) {
        TrackId = trackId
    }

    fun getId(): Int =Id


    fun setId(id: Int) {
        Id = id
    }

    fun getAlbumId(): Long = albumId


    fun setAlbumId(albumId: Long) {
        this.albumId = albumId
    }

    fun getSongName(): String = songName!!


    fun setSongName(songName: String) {
        this.songName = songName
    }

    fun getSongArtist(): String =songArtist!!


    fun setSongArtist(songArtist: String) {
        this.songArtist = songArtist
    }

    fun getSongTime(): String =songTime!!


    fun getSongFileId(): String {
        return SongFileId!!
    }


    fun setSongTime(songTime: String) {
        this.songTime = songTime
    }

    fun getSongComposer(): String = songComposer!!


    fun setSongComposer(songComposer: String) {
        this.songComposer = songComposer
    }

    fun getSongMoviename(): String = songMoviename!!



    fun setSongFileId(SongFileId: String) {
        this.SongFileId = SongFileId
    }


    fun setSongMoviename(songMoviename: String) {
        this.songMoviename = songMoviename
    }

    fun getSongPath(): String =songPath!!


    fun setSongPath(songPath: String) {
        this.songPath = songPath
    }


    fun getSongImgPath(): String =songImgPath!!


    fun setSongImgPath(songImgPath: String) {
        this.songImgPath = songImgPath
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(Id)
        dest?.writeInt(AlbumnewId)
        dest?.writeInt(ArtistId)
        dest?.writeInt(TrackId)
        dest?.writeLong(albumId)
        dest?.writeString(SongFileId)
        dest?.writeString(songName)
        dest?.writeString(songArtist)
        dest?.writeString(songTime)
        dest?.writeString(songComposer)
        dest?.writeString(songMoviename)
        dest?.writeString(songPath)
        dest?.writeString(songImgPath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SongInfoModel> {
        override fun createFromParcel(parcel: Parcel): SongInfoModel {
            return SongInfoModel(parcel)
        }

        override fun newArray(size: Int): Array<SongInfoModel?> {
            return arrayOfNulls(size)
        }
    }
}