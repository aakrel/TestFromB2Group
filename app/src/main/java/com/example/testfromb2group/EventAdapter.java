package com.example.testfromb2group;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
//подкапотная настройка отображения в RecyclerView
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<HandbookOfConsequencesOfViolations> eventList;
    private Context context;

    public EventAdapter(Context context, List<HandbookOfConsequencesOfViolations> eventList) {
        this.eventList = eventList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //тут целая куча параметров. в том числе и тот список что мы хотим вывести метод определяет как будет отображаться каждый элемент
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HandbookOfConsequencesOfViolations event = eventList.get(position);
        holder.textViewName.setText(event.getName());
        holder.textViewId.setText(String.valueOf(event.getId())); // Сохраняем ID в TextView, даже если он скрыт

        holder.itemView.setOnClickListener(new View.OnClickListener() { // Обработчик клика
            @Override
            public void onClick(View v) {
                // Создаем Intent для открытия DetailActivity
                Intent intent = new Intent(context, DetailActivity.class);

                // Передаем данные EventData в DetailActivity иными словами заполняем данными каждый элемент по клику
                intent.putExtra("eventId", event.getId());
                intent.putExtra("eventName", event.getName());
                intent.putExtra("eventDivisionType", event.getDivisionType());
                if (event.getDel() != null) {
                    intent.putExtra("eventDel", event.getDel().toString());
                } else {
                    intent.putExtra("eventDel", -1); // Или другое значение по умолчанию
                }
                context.startActivity(intent);
            }
        });
    }
    //узнает сколько элементов нужно отобразить в списке
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewId = itemView.findViewById(R.id.textViewId);
        }
    }
}