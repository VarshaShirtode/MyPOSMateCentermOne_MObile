//package com.quagnitia.myposmate.arke;
//
//import android.content.Context;
//import android.support.design.widget.TextInputLayout;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//
//import com.arke.vas.R;
//import com.data.RequestParameters;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class RequestParametersListViewAdapter extends BaseAdapter {
//    private Context context;
//    private List<RequestParameters> requestParametersList;
//
//
//    class ViewHolder {
//        TextInputLayout parametersName;
//        EditText parametersValue;
//        LinearLayout LL;
//    }
//
//    public RequestParametersListViewAdapter(Context context, List<RequestParameters> requestParametersList) {
//        this.requestParametersList = requestParametersList;
//        this.context = context;
//        // 初始化数据
//        // Initialization data
//        initDate();
//
//    }
//
//    /**
//     * Initialization data
//     * <p>
//     * 初始化数据
//     */
//    private void initDate() {
//        if (requestParametersList == null) {
//            requestParametersList = new ArrayList<RequestParameters>();
//        }
//    }
//
//    @Override
//    public int getCount() {
//        return requestParametersList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return requestParametersList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        ViewHolder holder = null;
//        RequestParameters parameter = requestParametersList.get(position);
//        LayoutInflater inflater = LayoutInflater.from(context);
//        if (convertView == null) {
//            convertView = inflater.inflate(
//                    R.layout.parameters_list_item, null);
//            holder = new ViewHolder();
//            holder.parametersName = (TextInputLayout) convertView.findViewById(R.id.textInputEmail);
//            holder.parametersValue = (EditText) convertView.findViewById(R.id.parametersValue);
//            holder.LL = (LinearLayout) convertView.findViewById(R.id.itemLinearLayout);
//            convertView.setTag(holder);
//        } else {
//            // 取出holder
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        holder.parametersValue.setText(parameter.getValue());
//        holder.parametersName.setHint(parameter.getDisplayName());
//
//        // 保存参数设置的值
//        // Save the value of the parameter settings
//        holder.parametersValue.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
//                                          int arg3) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable arg0) {
//                if (position < requestParametersList.size()) {
//                    requestParametersList.get(position).setValue(arg0.toString());
//                }
//
//            }
//        });
//        return convertView;
//    }
//}
