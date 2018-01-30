package cey.training.personal.android_iot;

import android.widget.BaseAdapter;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ModeBoxAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<ModeView> objects;

    ModeBoxAdapter(Context context, ArrayList<ModeView> devices) {
        ctx = context;
        objects = devices;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_mode, parent, false);
        }

        ModeView p = getProduct(position);

        // заполняем View в пункте списка данными из товаров: наименование, цена
        // и картинка
        ((TextView) view.findViewById(R.id.tvDescr)).setText(p.name);
//        ((TextView) view.findViewById(R.id.tvAdr)).setText(p.address);
        ((ImageView) view.findViewById(R.id.ivImage)).setImageResource(p.image);
        return view;
    }

    // товар по позиции
    ModeView getProduct(int position) {
        return ((ModeView) getItem(position));
    }

    void clear(){
        objects.clear();
    }

    // содержимое корзины
    ArrayList<ModeView> getBox() {
        ArrayList<ModeView> box = new ArrayList<ModeView>();
//        for (DeviceView p : objects) {
//            // если в корзине
//            if (p.)
//                box.add(p);
//        }
        return box;
    }
}
