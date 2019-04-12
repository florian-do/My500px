package com.do_f.my500px.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

class PhotoDetailViewModel: ViewModel() {
    val title : ObservableField<String> = ObservableField()
    val author : ObservableField<String> = ObservableField()
    val datetime : ObservableField<String> = ObservableField()
    val commentsCounts : ObservableField<String> = ObservableField()
    val likesCounts : ObservableField<String> = ObservableField()

    val description : ObservableField<String> = ObservableField()
    val pulse : ObservableField<String> = ObservableField()
    val views : ObservableField<String> = ObservableField()
    val popular : ObservableField<String> = ObservableField()
    val brand : ObservableField<String> = ObservableField()
    val lens : ObservableField<String> = ObservableField()
    val exif : ObservableField<String> = ObservableField()
}