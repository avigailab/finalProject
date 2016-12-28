package com.havenondemand.wikipediasearch;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hod.api.hodclient.*;
import hod.response.parser.*;

public class MainActivity extends ListActivity implements IHODClientCallback {

    public class QTIResp {
        public List<Document> documents;

        public class Document {
            public String reference;
            public Double weight;
            public String title;
            public String summary;
        }
    }
    private Map<String, String> mLanguages;

    private ArrayList<QTIResp.Document> m_templates = null;
    private WikiItemTemplates m_adapter;

    private QTIResp.Document mSelectedItem;
    private EditText mSearchEt;
    private ListView mItemList;
    private Spinner mWikiLanguageSp;
    private ProgressBar mLoadingPb;

    private HODClient mHodClient;
    private HODResponseParser mParser;

    private String mHodApiKey = "84e3d485-9e3c-4d51-ab24-88695fd363b7"; // place you apikey here
    private String mHodApp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingPb = (ProgressBar) findViewById(R.id.loading_pb);

        mLanguages = new HashMap<>();
        mLanguages.put("English", "wiki_eng");
        mLanguages.put("Spanish", "wiki_spa");
        mLanguages.put("French", "wiki_fra");
        mLanguages.put("Italian", "wiki_ita");
        mLanguages.put("German", "wiki_ger");
        mLanguages.put("Chinese", "wiki_chi");

        mWikiLanguageSp = (Spinner) findViewById(R.id.wiki_language_sp);
    }
    @Override
    protected void onPause()
    {
        super.onPause();

    }
    @Override
    protected void onResume()
    {
        super.onResume();
        if (m_templates == null) {
            m_templates = new ArrayList<QTIResp.Document>();

            this.m_adapter = new WikiItemTemplates(this, R.layout.row, m_templates);
            setListAdapter(this.m_adapter);
        }

        if (mHodClient == null)
            mHodClient = new HODClient(mHodApiKey, this);

        if (mParser == null)
            mParser = new HODResponseParser();

        if (mSearchEt == null) {
            mSearchEt = (EditText) findViewById(R.id.search_et);

            mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    switch (actionId) {
                        case EditorInfo.IME_ACTION_GO:
                        case EditorInfo.IME_ACTION_NEXT:
                        case EditorInfo.IME_ACTION_DONE:
                            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            in.hideSoftInputFromWindow(mSearchEt.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            SearchWikipedia();
                            break;
                        default:
                            break;

                    }
                    return false;
                }
            });
        }
        if (mItemList == null) {
            mItemList = (ListView) findViewById(android.R.id.list);

            mItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapter, View myView, int myItemInt, long mylng) {
                    mSelectedItem = m_templates.get(myItemInt);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mSelectedItem.reference)));
                }
            });
        }
    }
    private void SearchWikipedia()
    {
        if (mHodApiKey.length() == 0) {
            mLoadingPb.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Please provide your apikey to mHodApiKey then recompile the project!", Toast.LENGTH_LONG).show();
            return;
        }
        String searchArg = mSearchEt.getText().toString();
        if (searchArg.length() == 0)
            searchArg = "*";

        if (m_templates.size() > 0) {
            m_templates.clear();
            m_adapter.notifyDataSetChanged();
        }
        mHodApp = HODApps.RECOGNIZE_SPEECH;

        String lang = mWikiLanguageSp.getSelectedItem().toString();

        String indexName = mLanguages.get(lang);
        Map<String, Object> params = new HashMap<String, Object>();
       /* params.put("text", searchArg);
        params.put("indexes", indexName);
        params.put("summary", "quick");*/
        params.put("url", "file:///C:/Users/User/Downloads/rec_2s.wav");

        mLoadingPb.setVisibility(View.VISIBLE);
        mHodClient.PostRequest(params, mHodApp, HODClient.REQ_MODE.SYNC);
    }
    @Override
    public void onErrorOccurred(String errorMessage) {
        Toast.makeText(this, errorMessage+"!!!!!", Toast.LENGTH_LONG).show();
        Log.e("err",errorMessage);
        mLoadingPb.setVisibility(View.INVISIBLE);
    }

    @Override
    public void requestCompletedWithJobID(String response) {
        Log.e("request complate----","request complate----");
        String jobID = mParser.ParseJobID(response);
        if (jobID.length() > 0)
            mHodClient.GetJobStatus(jobID);
    }

    @Override
    public void requestCompletedWithContent(String response) {
        mLoadingPb.setVisibility(View.INVISIBLE);
        if (mHodApp.equals(HODApps.RECOGNIZE_SPEECH)) {
            QTIResp resp = (QTIResp) mParser.ParseCustomResponse(QTIResp.class, response);
            if (resp != null) {
                if (resp.documents.size() == 0)
                {
                    Toast.makeText(this, "Not found", Toast.LENGTH_LONG).show();
                    return;
                }
                for (QTIResp.Document doc : resp.documents) {
                    m_templates.add(doc);
                }
                m_adapter.notifyDataSetChanged();
            }
        }
    }

    public class WikiItemTemplates extends ArrayAdapter<QTIResp.Document> {

        private ArrayList<QTIResp.Document> items;

        public WikiItemTemplates(Context context, int textViewResourceId, ArrayList<QTIResp.Document> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row, parent, false);
            }
            QTIResp.Document o = items.get(position);
            if (o != null) {
                TextView template_name = (TextView) v.findViewById(R.id.p_title);
                if (template_name != null) {
                    template_name.setText(o.title);
                }
                template_name = (TextView) v.findViewById(R.id.p_weight);
                if (template_name != null) {
                    template_name.setText(String.format("%.2f",o.weight) + "%");
                }
                TextView template_summary = (TextView) v.findViewById(R.id.p_summary);
                if (template_summary != null) {
                    template_summary.setText(o.summary);
                }
            }
            return v;
        }
    }
}
