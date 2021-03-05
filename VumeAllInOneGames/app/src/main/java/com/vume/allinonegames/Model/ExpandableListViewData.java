package com.vume.allinonegames.Model;

import android.app.Activity;

import com.vume.allinonegames.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListViewData {
    public static HashMap<String, List<String>> getFaqOneData(Activity mActivity) {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> ansnone = new ArrayList<String>();
        ansnone.add(mActivity.getResources().getString(R.string.faqone_ansone));

        List<String> anstwo = new ArrayList<String>();
        anstwo.add(mActivity.getResources().getString(R.string.faqone_anstwo));

        List<String> ansthree = new ArrayList<String>();
        ansthree.add(mActivity.getResources().getString(R.string.faqone_ansthree));

        List<String> ansfour = new ArrayList<String>();
        ansfour.add(mActivity.getResources().getString(R.string.faqone_ansfour));

        List<String> ansfive = new ArrayList<String>();
        ansfive.add(mActivity.getResources().getString(R.string.faqone_ansfive));

        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnone), ansnone);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustntwo), anstwo);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnthree), ansthree);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnfour), ansfour);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnfive), ansfive);
        return expandableListDetail;
    }

    public static List<String> getFaqOneQuestionList(Activity mActivity) {
        List<String> qustnListData = new ArrayList<>();
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnone));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustntwo));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnthree));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnfour));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnfive));
        return qustnListData;
    }

    public static HashMap<String, List<String>> getFaqTwoData(Activity mActivity) {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> ansnone = new ArrayList<String>();
        ansnone.add(mActivity.getResources().getString(R.string.faqone_ansone));

        List<String> anstwo = new ArrayList<String>();
        anstwo.add(mActivity.getResources().getString(R.string.faqone_anstwo));

        List<String> ansthree = new ArrayList<String>();
        ansthree.add(mActivity.getResources().getString(R.string.faqone_ansthree));

        List<String> ansfour = new ArrayList<String>();
        ansfour.add(mActivity.getResources().getString(R.string.faqone_ansfour));

        List<String> ansfive = new ArrayList<String>();
        ansfive.add(mActivity.getResources().getString(R.string.faqone_ansfive));

        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnone), ansnone);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustntwo), anstwo);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnthree), ansthree);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnfour), ansfour);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnfive), ansfive);
        return expandableListDetail;
    }

    public static List<String> getFaqTwoQuestionList(Activity mActivity) {
        List<String> qustnListData = new ArrayList<>();
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnone));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustntwo));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnthree));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnfour));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnfive));
        return qustnListData;
    }

    public static HashMap<String, List<String>> getFaqThreeData(Activity mActivity) {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> ansnone = new ArrayList<String>();
        ansnone.add(mActivity.getResources().getString(R.string.faqone_ansone));

        List<String> anstwo = new ArrayList<String>();
        anstwo.add(mActivity.getResources().getString(R.string.faqone_anstwo));

        List<String> ansthree = new ArrayList<String>();
        ansthree.add(mActivity.getResources().getString(R.string.faqone_ansthree));

        List<String> ansfour = new ArrayList<String>();
        ansfour.add(mActivity.getResources().getString(R.string.faqone_ansfour));

        List<String> ansfive = new ArrayList<String>();
        ansfive.add(mActivity.getResources().getString(R.string.faqone_ansfive));

        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnone), ansnone);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustntwo), anstwo);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnthree), ansthree);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnfour), ansfour);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnfive), ansfive);
        return expandableListDetail;
    }

    public static List<String> getFaqThreeQuestionList(Activity mActivity) {
        List<String> qustnListData = new ArrayList<>();
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnone));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustntwo));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnthree));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnfour));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnfive));
        return qustnListData;
    }

    public static HashMap<String, List<String>> getFaqFourData(Activity mActivity) {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> ansnone = new ArrayList<String>();
        ansnone.add(mActivity.getResources().getString(R.string.faqone_ansone));

        List<String> anstwo = new ArrayList<String>();
        anstwo.add(mActivity.getResources().getString(R.string.faqone_anstwo));

        List<String> ansthree = new ArrayList<String>();
        ansthree.add(mActivity.getResources().getString(R.string.faqone_ansthree));

        List<String> ansfour = new ArrayList<String>();
        ansfour.add(mActivity.getResources().getString(R.string.faqone_ansfour));

        List<String> ansfive = new ArrayList<String>();
        ansfive.add(mActivity.getResources().getString(R.string.faqone_ansfive));

        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnone), ansnone);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustntwo), anstwo);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnthree), ansthree);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnfour), ansfour);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnfive), ansfive);
        return expandableListDetail;
    }

    public static List<String> getFaqFourQuestionList(Activity mActivity) {
        List<String> qustnListData = new ArrayList<>();
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnone));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustntwo));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnthree));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnfour));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnfive));
        return qustnListData;
    }

    public static HashMap<String, List<String>> getFaqFiveData(Activity mActivity) {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> ansnone = new ArrayList<String>();
        ansnone.add(mActivity.getResources().getString(R.string.faqone_ansone));

        List<String> anstwo = new ArrayList<String>();
        anstwo.add(mActivity.getResources().getString(R.string.faqone_anstwo));

        List<String> ansthree = new ArrayList<String>();
        ansthree.add(mActivity.getResources().getString(R.string.faqone_ansthree));

        List<String> ansfour = new ArrayList<String>();
        ansfour.add(mActivity.getResources().getString(R.string.faqone_ansfour));

        List<String> ansfive = new ArrayList<String>();
        ansfive.add(mActivity.getResources().getString(R.string.faqone_ansfive));

        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnone), ansnone);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustntwo), anstwo);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnthree), ansthree);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnfour), ansfour);
        expandableListDetail.put(mActivity.getResources().getString(R.string.faqone_qustnfive), ansfive);
        return expandableListDetail;
    }

    public static List<String> getFaqFiveQuestionList(Activity mActivity) {
        List<String> qustnListData = new ArrayList<>();
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnone));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustntwo));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnthree));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnfour));
        qustnListData.add(mActivity.getResources().getString(R.string.faqone_qustnfive));
        return qustnListData;
    }
}