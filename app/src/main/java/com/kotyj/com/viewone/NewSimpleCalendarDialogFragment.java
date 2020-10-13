package com.kotyj.com.viewone;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kotyj.com.R;
import com.kotyj.com.callback.CalendarSelectCallback;
import com.kotyj.com.utils.LogUtils;
import com.kotyj.com.utils.ViewUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NewSimpleCalendarDialogFragment extends DialogFragment implements OnDateSelectedListener {
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("dd");
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    @BindView(R.id.tv_year)
    TextView tvYear;
    @BindView(R.id.tv_monthAndDay)
    TextView tvMonthAndDay;
    @BindView(R.id.tv_weekDay)
    TextView tvWeekDay;
    @BindView(R.id.calendarView)
    MaterialCalendarView calendarView;
    @BindView(R.id.tv_diss)
    TextView tvDiss;
    @BindView(R.id.tv_sub)
    TextView tvSub;
    @BindView(R.id.ll1)
    LinearLayout ll1;
    private Calendar calendar;
    private List<Date> selectedDates;
    private CalendarSelectCallback mCalendarSelectCallback;
    Unbinder unbinder;
    private Context context;

    public static NewSimpleCalendarDialogFragment getInstance(boolean zhia) {
        NewSimpleCalendarDialogFragment simpleCalendarDialogFragment = new NewSimpleCalendarDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isZhia", zhia);
        simpleCalendarDialogFragment.setArguments(bundle);
        return simpleCalendarDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.MyProgressDialog);
        setCancelable(false);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_basic, container);
        unbinder = ButterKnife.bind(this, view);
        initData(view);
        return view;
    }

    private void initData(View view) {

        calendar = Calendar.getInstance();

//        calendar.add(Calendar.DAY_OF_MONTH, 1);

        try {
            calendarView.setOnDateChangedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        calendarView.setSelectedDate(calendar);
//        calendarView.setCurrentDate(calendar);
//        calendarView.state().edit().setMinimumDate(calendar).commit();
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

        calendarView.addDecorator(new SameDayDecorator());
        calendarView.addDecorator(new PrimeDayDisableDecorator());

        tvDiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        tvSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<CalendarDay> selectedDays = calendarView.getSelectedDates();
                if (selectedDays.size() >= 1) {
                    selectedDates = new ArrayList<>();
                    for (CalendarDay day : selectedDays) {
                        selectedDates.add(day.getDate());
                    }
                    Collections.sort(selectedDates);
                    StringBuilder sb = new StringBuilder();
                    for (Date date : selectedDates) {
                        sb.append(sdf2.format(date)).append(",");
                    }
                    String str = sb.toString();
                    if (mCalendarSelectCallback != null) {
                        mCalendarSelectCallback.success(selectedDates, str.substring(0, str.length() - 1));
                        dismiss();
                    }
                } else {
                    ViewUtils.makeToast(getActivity(), "至少选择1个日期", 1000);
                }
            }
        });

    }

    public void setCalendarSelectCallback(CalendarSelectCallback calendarSelectCallback) {
        mCalendarSelectCallback = calendarSelectCallback;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget,
                               @NonNull CalendarDay date, boolean selected) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private static class PrimeDayDisableDecorator implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            LogUtils.i("day=" + day + ",compare" + day.getCalendar().compareTo(Calendar.getInstance()));


            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.DAY_OF_MONTH, -1);
            int hour = instance.get(Calendar.HOUR_OF_DAY);
            Log.e("时:::::::::::::::", hour + "");
            if (hour >= 15) {
                return day.getCalendar().compareTo(Calendar.getInstance()) == -1;
            } else {
                return day.getCalendar().compareTo(instance) == -1;
            }

        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true);
        }


    }

    public class SameDayDecorator implements DayViewDecorator {
        @Override
        public boolean shouldDecorate(CalendarDay day) {
            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.DAY_OF_MONTH, -1);
            int hour = instance.get(Calendar.HOUR_OF_DAY);
            if (hour >= 15) {
                return day.getCalendar().compareTo(Calendar.getInstance()) == 1;
            } else {
                return day.getCalendar().compareTo(instance) == 1;
            }
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.parseColor("#EEEEEE")));
        }
    }

}