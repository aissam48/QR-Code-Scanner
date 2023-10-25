package fr.myticket.moov.checker.repository

import fr.myticket.moov.checker.models.DetailsModel
import fr.myticket.moov.checker.models.Event
import fr.myticket.moov.checker.models.EventRepo
import kotlinx.coroutines.flow.Flow

interface ApiServices {

    suspend fun login(params: HashMap<String, Any>): Flow<EventRepo<Boolean>>
    suspend fun getDetails(params: HashMap<String, Any>): Flow<EventRepo<DetailsModel>>
    suspend fun getEvent(params: HashMap<String, Any>): Flow<EventRepo<Event>>
}