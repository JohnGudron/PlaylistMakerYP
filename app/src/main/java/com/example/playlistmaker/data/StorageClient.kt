package com.example.playlistmaker.data
// TODO include to or remove from project
interface StorageClient <T> {
    fun storeData(data: T)
    fun getData(): T?
}
