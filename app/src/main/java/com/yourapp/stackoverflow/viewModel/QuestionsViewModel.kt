package com.yourapp.stackoverflow.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourapp.stackoverflow.apiService.QuestionsApi
import com.yourapp.stackoverflow.model.QuestionsModel
import kotlinx.coroutines.launch

class QuestionsViewModel : ViewModel() {
    private val _questionsModel = MutableLiveData<QuestionsModel>()
    val questionsModel : LiveData<QuestionsModel>
    get() = _questionsModel

    fun getQuestions(){
        viewModelScope.launch{
            try{
                _questionsModel.value = QuestionsApi.service.getQuestions()
            }
            catch (e : Exception){
                Log.d("MainActivity", e.message.toString())
            }
        }
    }
}