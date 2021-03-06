package com.cogentworks.forthq;

import android.content.Context;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class GetChallenges extends GetData {

    private ChallengesFragment mFragment;

    public GetChallenges(Context context, ChallengesFragment fragment) {
        super(context);
        final Context fContext = context;
        mFragment = fragment;
        endpoint = "https://fortnite-public-api.theapinetwork.com/prod09/challenges/get?season=current";
        //params.put("season", "current");

        responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String season;

                try {
                    JSONObject jResponse = new JSONObject(response);
                    ArrayList<ArrayList<ChallengeItem>> results = new ArrayList<ArrayList<ChallengeItem>>();
                    season = jResponse.getString("season");
                    JSONObject weeks = jResponse.getJSONObject("challenges");
                    Iterator<?> keys = weeks.keys();
                    while (keys.hasNext()) {
                        ArrayList<ChallengeItem> week = new ArrayList<>();
                        String key = (String)keys.next();
                        JSONArray jsonArray = weeks.getJSONArray(key);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject challenge = jsonArray.getJSONObject(i);
                            ChallengeItem item = new ChallengeItem(
                                    challenge.getString("challenge"),
                                    challenge.getInt("total"),
                                    challenge.getInt("stars"),
                                    challenge.getString("difficulty"));
                            week.add(item);
                        }

                        if (week.size() > 0)
                            results.add(0, week);
                    }

                    mFragment.season = season;
                    ((TextView)mFragment.mListView.findViewById(R.id.title)).setText("Season " + season);

                    mFragment.listItems.clear();
                    mFragment.listItems.addAll(results);
                    ((BaseAdapter)((HeaderViewListAdapter)mFragment.mListView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();

                } catch (WindowManager.BadTokenException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    Toast.makeText(fContext, "Network Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    hideProgressBar(mFragment);
                }

            }
        };

    }
}
