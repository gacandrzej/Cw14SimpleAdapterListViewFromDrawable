package andrzej.gac.cw14simpleadapterlistviewfromdrawable;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabDelete;
    private List<Map<String, Object>> fruitsList;
    private SimpleAdapter adapter;
    private int selectedItem = -1;
    private String[] productNames;
    private int[] productImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.listView);
        fabAdd = findViewById(R.id.fabAdd);
        fabDelete = findViewById(R.id.fabDelete);

        fruitsList = new ArrayList<>();

        loadDefaultProducts(); // Dodajemy domyślne produkty

        adapter = new SimpleAdapter(
                this,
                fruitsList,
                R.layout.list_item,
                new String[]{"image", "name", "description"},
                new int[]{R.id.itemImage, R.id.itemName, R.id.itemDescription});

        listView.setAdapter(adapter);



        listView.setOnItemClickListener((parent, view, position, id) -> selectedItem = position);

        fabAdd.setOnClickListener(v -> showAddItemDialog());
        fabDelete.setOnClickListener(v -> removeSelectedItem());
    }

    private void loadDefaultProducts() {
        productNames = getResources().getStringArray(R.array.product_names);
        String[] productDescriptions = getResources().getStringArray(R.array.product_descriptions);

        productImages = new int[]{
                R.drawable.apple, R.drawable.banana, R.drawable.avocado,
                R.drawable.cherries, R.drawable.currant, R.drawable.blueberries,
                R.drawable.dragonfruit, R.drawable.coconut, R.drawable.blackberry
        };

        for (int i = 0; i < productNames.length; i++) {
            addProduct(productNames[i], productDescriptions[i], productImages[i]);
        }
    }
    private void addProduct(String name, String description, int imageResId) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("description", description);
        item.put("image", imageResId);
        fruitsList.add(item);
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dodaj produkt");

        // Tworzymy layout dla dialogu
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView);

        EditText inputName = dialogView.findViewById(R.id.editTextProductName);
        EditText inputDescription = dialogView.findViewById(R.id.editTextProductDescription);
        Spinner imageSpinner = dialogView.findViewById(R.id.spinnerImages);

        // Adapter dla spinnera
        ArrayAdapter<String> productNameAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                productNames // Używamy nazw produktów, a nie tablicy obrazków!
        );

        imageSpinner.setAdapter(productNameAdapter);

        // Przyciski dialogu
        builder.setPositiveButton("Dodaj", (dialog, which) -> {
            String name = inputName.getText().toString().trim();
            String description = inputDescription.getText().toString().trim();
            int selectedImageIndex = imageSpinner.getSelectedItemPosition(); // Pobranie pozycji wybranego obrazka
            int imageResId = productImages[selectedImageIndex]; // Pobranie ID obrazka na podstawie indeksu

            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Wypełnij wszystkie pola!", Toast.LENGTH_SHORT).show();
                return;
            }

            addProduct(name, description, imageResId);
            adapter.notifyDataSetChanged(); // Odświeżenie listy po dodaniu produktu
        });

        builder.setNegativeButton("Anuluj", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void removeSelectedItem() {
        if (selectedItem != -1) {
            fruitsList.remove(selectedItem);
            adapter.notifyDataSetChanged();
            selectedItem = -1;
        } else {
            Toast.makeText(this, "Wybierz element do usunięcia", Toast.LENGTH_SHORT).show();
        }
    }

}