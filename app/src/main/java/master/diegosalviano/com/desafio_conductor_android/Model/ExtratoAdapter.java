package master.diegosalviano.com.desafio_conductor_android.Model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import master.diegosalviano.com.desafio_conductor_android.Controller.Extrato;
import master.diegosalviano.com.desafio_conductor_android.R;

public class ExtratoAdapter extends ArrayAdapter<Extrato> {

    ArrayList<Extrato> extrato;
    Context context;
    int id;

    public ExtratoAdapter(Context context, int id, ArrayList<Extrato> extrato) {
        super(context, id, extrato);
        this.extrato = extrato;
        this.context = context;
        this.id = id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.lista_extrato, null, true);

            Extrato extrato = getItem(position);

            TextView tvData = (TextView)convertView.findViewById(R.id.tvData);
            TextView tvDescricao = (TextView)convertView.findViewById(R.id.tvDescricao);
            TextView tvValor = (TextView)convertView.findViewById(R.id.tvValor);

            tvData.setText(Dados.data(extrato.getData()));
            tvDescricao.setText(extrato.getDescricao());
            tvValor.setText(Dados.valor(extrato.getValor()));

        }


        return convertView;
    }
}
