package com.lichen.youtubedownloader;

        import java.util.ArrayList;
        import java.util.List;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.CheckBox;
        import android.widget.CompoundButton;
        import android.widget.CompoundButton.OnCheckedChangeListener;
        import android.widget.TextView;

public class LvAdapter extends BaseAdapter {
    // 填充数据的list
    private ArrayList<String> list;
    //
    public  static boolean flag = false;
    // 用来控制CheckBox的选中状况
    private static List<Boolean> isSelected;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;
    // 构造器
    public LvAdapter(ArrayList<String> list, Context context) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        isSelected = new ArrayList<Boolean>();
        // 初始化数据
        setData(list);

    }

    // 初始化isSelected的数据，设置cheakbox的初始状态  ，false为没选
    //getIsSelected()相当于HashMap<Integer, Boolean> isSelected
    private void setData(ArrayList<String> lList) {
        if (lList != null){
            this.list = lList;
        }else{
            this.list = new ArrayList<String>();
        }

        if (list.size()>0) {
            for (int i = 0; i < list.size(); i++) {
                isSelected.add(false);
            }
        }
    }

    // 更新adapter
    public void updataAdapter(ArrayList<String> lList,boolean f) {
        this.setData(lList);
        this.flag = f;
        this.notifyDataSetChanged();
    }

    //获取ListView个数
    @Override
    public int getCount() {
        return list.size();
    }
    //获取第position的数据资料
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }
    //
    @Override
    public long getItemId(int position) {
        return position;
    }
    //
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //？？？
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.lv_item, null);
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            holder.tv = (TextView) convertView.findViewById(R.id.textView1);
            holder.cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
            if (flag) {
                holder.cb.setVisibility(View.VISIBLE);
            }else {
                holder.cb.setVisibility(View.INVISIBLE);

            }
            // 为view设置标签  ？？？？
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        // 设置list中TextView的显示
        holder.tv.setText(list.get(position));
//        // 根据isSelected来设置checkbox的选中状况

        holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                isSelected.set(position, arg1);

            }
        });
        holder.cb.setChecked(getIsSelected().get(position));
        notifyDataSetChanged();
        return convertView;
    }

    public static List<Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(List<Boolean> isSelected) {
        LvAdapter.isSelected = isSelected;
    }

    public static class ViewHolder {
        TextView tv;
        CheckBox cb;
    }
} 

