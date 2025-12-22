package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.util.Resource
import java.util.concurrent.ExecutorService

class TracksInteractorImpl(private val repository: TracksRepository, private val executor: ExecutorService): TracksInteractor {

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute{
            when (val resource = repository.searchTracks(expression)) {
                is Resource.Error -> consumer.consume(null, resource.message)
                is Resource.Success -> consumer.consume(resource.data, null)
            }
        }
    }
}