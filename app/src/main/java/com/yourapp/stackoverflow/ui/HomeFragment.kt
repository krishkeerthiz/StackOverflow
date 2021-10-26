package com.yourapp.stackoverflow.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yourapp.stackoverflow.R
import com.yourapp.stackoverflow.databinding.FragmentHomeBinding
import com.yourapp.stackoverflow.listAdapter.QuestionsCustomAdapter
import com.yourapp.stackoverflow.model.QuestionCardModel
import com.yourapp.stackoverflow.viewModel.QuestionsViewModel
import org.w3c.dom.Text
import kotlin.math.roundToLong

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private val questions : MutableList<QuestionCardModel> = mutableListOf<QuestionCardModel>()
    private var tags : MutableList<String> = mutableListOf<String>()
    private var questionsTitle : MutableList<String> = mutableListOf<String>()
    private var owners : MutableList<String> = mutableListOf<String>()
    private var averageAnswerCount = MutableLiveData<Float>()
    private var averageViewCount = MutableLiveData<Float>()
    private var totalAnswerCount : Float = 0.0f
    private var totalViewCount : Float = 0.0f
    private val viewModel : QuestionsViewModel by lazy{
        ViewModelProvider(requireActivity()).get(QuestionsViewModel::class.java)
    }
    private lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getQuestions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        val searchText = binding.search

        initializeAds()

        averageAnswerCount.value = 0.0f
        averageViewCount.value = 0.0f

        viewModel.questionsModel.observe(requireActivity(),{questionsModel ->
            val _tags = mutableListOf<String>()
            var averageView = 0
            var averageAnswer = 0

            //Toast.makeText(requireContext(), questionsModel.items?.get(0)?.title, Toast.LENGTH_SHORT).show()
            for(item in questionsModel.items!!){
                questions.add(QuestionCardModel(item.title, item.owner.displayName, item.creationDate, item.owner.profileImage,
                item.link, item.tags, item.answerCount, item.viewCount))

                _tags += item.tags.toMutableList()

                questionsTitle.add(item.title.toLowerCase()) // List of questions used for search
                owners.add(item.owner.displayName.toLowerCase()) //List of owners used for search

                averageView += item.viewCount
                averageAnswer += item.answerCount
            }
            tags = _tags.toSet().toMutableList() // Unique tags to be displayed in filter

            totalAnswerCount = averageAnswer.toFloat()/30
            totalViewCount = averageView.toFloat()/30
            averageAnswerCount.value = totalAnswerCount
            averageViewCount.value = totalViewCount

            showFullResult()
        })

        averageAnswerCount.observe(requireActivity(),{
            binding.answerCount.text = String.format("%.1f", it)
        })

        averageViewCount.observe(requireActivity(),{
            binding.viewCount.text = String.format("%.1f", it)
        })

        binding.filter.setOnClickListener{
            showTags()
        }

        val questionWatcher = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val text = searchText.text.toString().toLowerCase()
                if(text.isNotBlank()){
                    showFilteredQuestions(text)
                }
                else
                    showFullResult()

            }
        }

        searchText.addTextChangedListener(questionWatcher)
    }

    private fun onListItemClicked(link : String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }

    private fun showTags(){
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)

        val tagsList = bottomSheetDialog.findViewById<ListView>(R.id.tags_list)
        val clearButton = bottomSheetDialog.findViewById<TextView>(R.id.clear_button)

        tagsList?.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, tags)

        tagsList?.setOnItemClickListener{ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            showFilteredTag(tags[i])
            bottomSheetDialog.dismiss()
        }

        clearButton?.setOnClickListener {
            showFullResult()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
//        val builder = AlertDialog.Builder(requireActivity())
//        builder.setTitle(getString(R.string.filter))
//
//        val listDialogView = layoutInflater.inflate(R.layout.list, null)
//        builder.setView(listDialogView)
//
//        val listView = listDialogView.findViewById<ListView>(R.id.lv)
//        listView.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, tags)
//
//        listView.setOnItemClickListener { p0, p1, p2, p3 ->
//            showFilteredResult(tags[p2])
//        }
//
//        val dialog = builder.create()
//        dialog.show()

    }

    private fun getRecyclerView(questions: MutableList<QuestionCardModel>){
        val questionsCustomAdapter = QuestionsCustomAdapter(activity?.applicationContext, questions){
                position -> onListItemClicked(questions[position].questionLink!!)
        }

        val recyclerView = binding.questionsRecyclerView
        val linearLayoutManager = LinearLayoutManager(activity?.applicationContext)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = questionsCustomAdapter
    }


    private fun showFilteredTag(tag : String){
        val filteredQuestions = mutableListOf<QuestionCardModel>()
        var averageView = 0
        var averageAnswer = 0
        var counter = 0
        for(question in questions){
            if(question.tags.contains(tag)){
                filteredQuestions.add(question)
                averageAnswer+= question.answerCount
                averageView += question.viewCount
                counter++
            }
        }

        setCounts(averageAnswer.toFloat()/counter, averageView.toFloat()/counter)
        getRecyclerView(filteredQuestions)
    }

    private fun showFilteredQuestions(title : String){
        val filteredQuestions = mutableListOf<QuestionCardModel>()
        var averageView = 0
        var averageAnswer = 0
        var counter = 0
        for(i in 0 until questions.size){
            if(questionsTitle[i].contains(title) or owners[i].contains(title)){
                filteredQuestions.add(questions[i])
                averageAnswer+= questions[i].answerCount
                averageView += questions[i].viewCount
                counter++
            }
        }

        setCounts(averageAnswer.toFloat()/counter, averageView.toFloat()/counter)
        getRecyclerView(filteredQuestions)
    }

    private fun showFullResult(){
        setCounts(totalAnswerCount, totalViewCount)
        getRecyclerView(questions)
    }

    private fun setCounts(answer : Float, view : Float){
        averageAnswerCount.value = answer
        averageViewCount.value = view
    }

    private val adSize: AdSize
        get() {
            val display = requireActivity().windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density
            var adWidthPixels = outMetrics.widthPixels.toFloat()

            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(requireActivity(), adWidth)
        }

    private fun initializeAds(){
        // Calling Ads.
        MobileAds.initialize(requireActivity())

        mAdView = AdView(requireActivity())
        mAdView.adSize = adSize
        mAdView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
        binding.adViewContainer.addView(mAdView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }
}