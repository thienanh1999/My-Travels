package com.vtca.mytravels.thienanh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.snackbar.Snackbar;
import com.vtca.mytravels.R;
import com.vtca.mytravels.base.MyConst;
import com.vtca.mytravels.dao.TravelExpenseDao;
import com.vtca.mytravels.database.AppDatabase;
import com.vtca.mytravels.entity.TravelExpense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpensesChartActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "ExpenseChartActivity";

    private TravelExpenseDao mTravelExpenseDao;
    private PieChart mChart;
    private List<TravelExpense> mTravelExpenseList;
    private List<String> mCurrencyList;
    private Map<String, List<TravelExpense>> mMap;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_chart);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTravelExpenseList = new ArrayList<>();
        mCurrencyList = new ArrayList<>();

        spinner = findViewById(R.id.currency_spin);

        long travelId = getIntent().getLongExtra(MyConst.REQKEY_TRAVEL_ID, 0);

        AppDatabase db = AppDatabase.getDatabase(getApplication());
        mTravelExpenseDao = db.travelExpenseDao();

        getAllExpensesOfTravel(travelId).observe(this, new Observer<PagedList<TravelExpense>>() {
            @Override
            public void onChanged(PagedList<TravelExpense> travelExpenses) {
                setData(travelExpenses);
            }
        });
    }

    private void setData(PagedList<TravelExpense> travelExpenses) {
        mMap = new HashMap<>();
        for (TravelExpense travelExpense : travelExpenses) {
            mTravelExpenseList.add(travelExpense);

            Log.d(TAG, "setData: " + travelExpense.getAmount() + " " + travelExpense.getCurrency()
                    + " " + travelExpense.getType() + " " + travelExpense.getTitle());
            List<TravelExpense> list = mMap.get(travelExpense.getCurrency());
            if (list == null) {
                list = new ArrayList<>();
                mMap.put(travelExpense.getCurrency(), list);
                mCurrencyList.add(travelExpense.getCurrency());
            }
            list.add(travelExpense);
        }

        if (mCurrencyList.size() == 0) return;

        //setup Spinner
        String[] c = getResources().getStringArray(R.array.currency_key);
        spinner.setOnItemSelectedListener(this);
        for (int i = 0; i < c.length; i++) {
            if (c[i].equals(mCurrencyList.get(0))) {
                spinner.setSelection(i);
                break;
            }
        }

        initGraph(mCurrencyList.get(0));
    }


    private void initGraph(String currency) {
        mChart = findViewById(R.id.chart);
        List<PieEntry> entries = new ArrayList<>();

        float totalBudget = 0;
        float totalExp = 0;

        List<TravelExpense> mList = mMap.get(currency);

        if (mList == null) {
            mChart.clear();
            Snackbar.make(mChart, "There is no data for this currency", Snackbar.LENGTH_LONG).show();
            return;
        }

        for (TravelExpense travelExpense : mList) {
            if (travelExpense.getType().equals("BUD")) {
                totalBudget += travelExpense.getAmount();
            }
            else  totalExp += travelExpense.getAmount();
        }

        float realBudget = totalBudget;
        if (totalExp > totalBudget) totalBudget = totalExp;

        for (TravelExpense travelExpense : mList) {
            if (travelExpense.getType().equals("EXP")) {
                PieEntry pieEntry = new PieEntry((float)travelExpense.getAmount()/totalBudget,
                        travelExpense.getTitle());
                entries.add(pieEntry);
            }
        }

        PieEntry pieEntry = new PieEntry((totalBudget-totalExp)/totalBudget, "balance");
        entries.add(pieEntry);

        PieDataSet set = new PieDataSet(entries, "Expenses by " + currency);
        PieData pieData = new PieData(set);
        final int[] pieColors = {
                getAppColor(R.color.pale_green),
                getAppColor(R.color.aqua),
                getAppColor(R.color.bisque),
                getAppColor(R.color.blue_violet),
                getAppColor(R.color.deep_sky_blue),
                getAppColor(R.color.red),
                getAppColor(R.color.gray),
                getAppColor(R.color.green),
                getAppColor(R.color._light_green),
                getAppColor(R.color.yellow),
                getAppColor(R.color.brown)
        };
        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : pieColors) {
            colors.add(color);
        }
        while (entries.size() > colors.size())
        {
            colors.addAll(colors);
        }

        set.setColors(colors);
        set.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        set.setValueLineWidth(2.0f);
        set.setValueLinePart1OffsetPercentage(80f);
        set.setValueLinePart1Length(0.4f);
        set.setValueLinePart2Length(0.3f);
        set.setSliceSpace(3f);
        set.setSelectionShift(5f);

        mChart.setData(pieData);
        mChart.setCenterText("Total Budget: " + "\n" + Float.toString(realBudget));
        mChart.setEntryLabelColor(Color.BLACK);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(10f,10f,10f,10f);
        mChart.setUsePercentValues(true);

        Legend legend = mChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setWordWrapEnabled(true);
        mChart.invalidate();
    }

    private LiveData<PagedList<TravelExpense>> getAllExpensesOfTravel(long travelId) {
        return new LivePagedListBuilder<>(mTravelExpenseDao.getAllExpensesOfTravel(travelId), 20).build();
    }

    private int getAppColor(int resourceId) {
        int color;
        color = ExpensesChartActivity.this.getResources().getColor(resourceId);
        return color;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        initGraph(spinner.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
