package com.example.testfromb2group;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.widget.SearchView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ManagerDB dbManager;
    private RecyclerView recyclerViewEvents;
    private EventAdapter eventAdapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private SearchView searchView;
    private List<HandbookOfConsequencesOfViolations> allEventData; //  Сохраняем все данные
    private List<HandbookOfConsequencesOfViolations> filteredEventData; //  Список для фильтрации

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbManager = new ManagerDB(this);

        Button insertButton = findViewById(R.id.insertButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this)); // Устанавливаем LayoutManager говорим как они должны отображаться
        searchView = findViewById(R.id.searchView);

        allEventData = new ArrayList<>();
        filteredEventData = new ArrayList<>();

        eventAdapter = new EventAdapter(this, filteredEventData);  //
        recyclerViewEvents.setAdapter(eventAdapter); // Устанавливаем адаптер

        insertButton.setOnClickListener(v -> {
            // Получаем JSON данные из сети (пример)
            fetchAndSaveData(getString(R.string.api_url));
            loadData();
        });

        deleteButton.setOnClickListener(v -> {
            boolean isDeleted = dbManager.deleteDatabase(MainActivity.this);
            if (isDeleted) {
                Toast.makeText(MainActivity.this, "Database deleted", Toast.LENGTH_SHORT).show();
                deleteData();

            } else {
                Toast.makeText(MainActivity.this, "Database deletion failed", Toast.LENGTH_SHORT).show();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterEvents(newText);
                return true;
            }
        });
        loadData(); //  Загружаем данные при создании Activity
    }

    private void loadData() {
        executorService.execute(() -> {
            allEventData = dbManager.getAllData();
            runOnUiThread(() -> {
                eventAdapter = new EventAdapter(MainActivity.this, allEventData);
                recyclerViewEvents.setAdapter(eventAdapter);
            });
        });
    }

    private void deleteData(){
        executorService.execute(() -> {
            allEventData.clear();
            runOnUiThread(() -> {
                eventAdapter = new EventAdapter(MainActivity.this, allEventData);
                recyclerViewEvents.setAdapter(eventAdapter);//снова закидываем сюда список но уже пустой
            });
        });
    }

    private void fetchAndSaveData(String url) {
        executorService.execute(() -> { // Запускаем в фоновом потоке
            try {
                String jsonString = getContent(url);
                if (jsonString != null && !jsonString.isEmpty()) {
                    // Преобразуем JSON-строку в JSONObject
                    JSONArray jArr = new JSONArray(jsonString);

                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jsonData = jArr.getJSONObject(i);

                        // Сохраняем данные в базу данных
                        boolean isInserted = dbManager.insertData(jsonData); // Передаем JSON-строку

                        if (!isInserted) {
                            Log.e(TAG, "Failed to insert data for object at index " + i);
                        }
                    }

                    // Отображаем сообщение в UI потоке
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Data inserted", Toast.LENGTH_SHORT).show());

                } else {
                    Log.e(TAG, "Failed to fetch data. getContent() returned null.");
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching or parsing data", e);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error fetching or parsing data", Toast.LENGTH_SHORT).show());
            }
        });
    }

    //  Метод для фильтрации данных
    private void filterEvents(String searchText) {
        filteredEventData.clear();  // Очищаем список отфильтрованных данных
        if (searchText.isEmpty()) {
            filteredEventData.addAll(allEventData); // Если строка поиска пуста, показываем все данные
        } else {
            searchText = searchText.toLowerCase();
            if (allEventData != null) { // Проверяем, что allEventData не null
                for (var event : allEventData) {
                    if (event.getName().toLowerCase().contains(searchText)) {
                        filteredEventData.add(event);
                    }
                }
            }
        }
        //  Обновляем RecyclerView
        runOnUiThread(() -> {
            eventAdapter = new EventAdapter(this, filteredEventData);
            recyclerViewEvents.setAdapter(eventAdapter);

            eventAdapter.notifyDataSetChanged();
        });

    }

    public static String getContent(String path) throws IOException {
        BufferedReader reader = null;
        InputStream stream = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.connect();
            stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder buf = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                buf.append(line).append("\n");
            }
            return (buf.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (stream != null) {
                    stream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace(); //  Логируем ошибки закрытия ресурсов
            }
        }
    }
}


