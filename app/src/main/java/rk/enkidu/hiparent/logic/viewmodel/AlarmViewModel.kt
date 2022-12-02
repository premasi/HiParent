package rk.enkidu.hiparent.logic.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import rk.enkidu.hiparent.data.entity.remote.Alarm
import rk.enkidu.hiparent.data.repository.Repository

class AlarmViewModel(private val repository: Repository): ViewModel() {
    private val _data = MutableLiveData<List<Alarm>>()
    val data : LiveData<List<Alarm>> =_data

    fun fetchAlarm() = repository.fetchAlarm(_data)

    fun update(id: String, date: String, time: String, title: String, desc: String) = repository
        .updateAlarm(id, date, time, title, desc)
}