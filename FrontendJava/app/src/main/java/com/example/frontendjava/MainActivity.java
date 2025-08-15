package com.example.frontendjava;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.frontendjava.api.ActeurApiService;
import com.example.frontendjava.api.RetrofitClient;
import com.example.frontendjava.model.Acteur;
import com.example.frontendjava.model.ActeurUpdate;
import com.example.frontendjava.ui.ActeurAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ActeurAdapter.OnActeurInteractionListener {

    private RecyclerView recyclerView;
    private TextView textError;
    private ActeurAdapter adapter;
    private FloatingActionButton fabAdd;
    private ActeurApiService service;
    private SwipeRefreshLayout swipeRefresh;
    private ShimmerFrameLayout shimmer;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerActeurs);
        textError = findViewById(R.id.textError);
        fabAdd = findViewById(R.id.fabAdd);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        shimmer = findViewById(R.id.shimmerContainer);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ActeurAdapter();
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(180);
        animator.setRemoveDuration(140);
        animator.setMoveDuration(200);
        animator.setChangeDuration(200);
        recyclerView.setItemAnimator(animator);

        service = RetrofitClient.getActeurService();

        fabAdd.setOnClickListener(v -> openActeurDialog(null));
        swipeRefresh.setOnRefreshListener(() -> fetchActeurs());

        // Apply status bar inset to toolbar so title sits lower
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(v.getPaddingLeft(), top + dpToPx(4), v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        fetchActeurs();
    }

    private int dpToPx(int dp) {
        return Math.round(getResources().getDisplayMetrics().density * dp);
    }

    private void startLoadingSkeleton() {
        if (!swipeRefresh.isRefreshing()) {
            shimmer.setVisibility(View.VISIBLE);
            shimmer.startShimmer();
        }
    }

    private void stopLoadingSkeleton() {
        shimmer.stopShimmer();
        shimmer.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);
    }

    private void updateCounter(int count) {
        if (toolbar != null) {
            toolbar.setTitle("Acteurs (" + count + ")");
        }
    }

    private void fetchActeurs() {
        startLoadingSkeleton();
        textError.setVisibility(View.GONE);
        service.getActeurs().enqueue(new Callback<List<Acteur>>() {
            @Override
            public void onResponse(@NonNull Call<List<Acteur>> call, @NonNull Response<List<Acteur>> response) {
                stopLoadingSkeleton();
                if (response.isSuccessful()) {
                    List<Acteur> list = response.body();
                    adapter.setData(list);
                    updateCounter(list == null ? 0 : list.size());
                } else {
                    showError("Réponse serveur: " + response.code());
                    updateCounter(0);
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Acteur>> call, @NonNull Throwable t) {
                stopLoadingSkeleton();
                showError("Erreur réseau: " + t.getMessage());
                updateCounter(0);
            }
        });
    }

    private void openActeurDialog(Acteur existing) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_acteur, null, false);
        EditText inputId = dialogView.findViewById(R.id.inputId);
        EditText inputName = dialogView.findViewById(R.id.inputName);
        EditText inputBio = dialogView.findViewById(R.id.inputBio);
        EditText inputPicture = dialogView.findViewById(R.id.inputPicture);

        boolean isEdit = existing != null;
        if (isEdit) {
            inputId.setText(String.valueOf(existing.getId()));
            inputId.setEnabled(false); // id not editable
            inputName.setText(existing.getName());
            inputBio.setText(existing.getBio());
            inputPicture.setText(existing.getPicture());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(isEdit ? "Modifier acteur" : "Nouvel acteur")
                .setView(dialogView)
                .setPositiveButton(isEdit ? "Mettre à jour" : "Créer", null) // set later to prevent auto-dismiss on validation errors
                .setNegativeButton("Annuler", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String idStr = inputId.getText().toString().trim();
                String name = inputName.getText().toString().trim();
                String bio = inputBio.getText().toString().trim();
                String picture = inputPicture.getText().toString().trim();

                if (!isEdit && idStr.isEmpty()) { inputId.setError("Requis"); return; }
                if (name.isEmpty()) { inputName.setError("Requis"); return; }
                if (bio.isEmpty()) { inputBio.setError("Requis"); return; }
                if (picture.isEmpty()) { inputPicture.setError("Requis"); return; }

                if (isEdit) {
                    updateActeur(existing.getId(), new ActeurUpdate(name, bio, picture), dialog);
                } else {
                    int id;
                    try { id = Integer.parseInt(idStr); } catch (NumberFormatException e) { inputId.setError("Nombre"); return; }
                    createActeur(new Acteur(id, name, bio, picture), dialog);
                }
            });
        });

        dialog.show();
    }

    private void createActeur(Acteur acteur, AlertDialog dialog) {
        startLoadingSkeleton();
        service.createActeur(acteur).enqueue(new Callback<Acteur>() {
            @Override
            public void onResponse(@NonNull Call<Acteur> call, @NonNull Response<Acteur> response) {
                stopLoadingSkeleton();
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Créé", Toast.LENGTH_SHORT).show();
                    fetchActeurs();
                } else if (response.code() == 409) {
                    Toast.makeText(MainActivity.this, "ID existe déjà", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Erreur: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Acteur> call, @NonNull Throwable t) {
                stopLoadingSkeleton();
                Toast.makeText(MainActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateActeur(int id, ActeurUpdate update, AlertDialog dialog) {
        startLoadingSkeleton();
        service.updateActeur(id, update).enqueue(new Callback<Acteur>() {
            @Override
            public void onResponse(@NonNull Call<Acteur> call, @NonNull Response<Acteur> response) {
                stopLoadingSkeleton();
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Mis à jour", Toast.LENGTH_SHORT).show();
                    fetchActeurs();
                } else if (response.code() == 404) {
                    Toast.makeText(MainActivity.this, "Introuvable", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Erreur: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Acteur> call, @NonNull Throwable t) {
                stopLoadingSkeleton();
                Toast.makeText(MainActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete(Acteur acteur) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer")
                .setMessage("Supprimer l'acteur " + acteur.getName() + " ?")
                .setPositiveButton("Oui", (d, w) -> deleteActeur(acteur.getId()))
                .setNegativeButton("Non", null)
                .show();
    }

    private void deleteActeur(int id) {
        startLoadingSkeleton();
        service.deleteActeur(id).enqueue(new Callback<Acteur>() {
            @Override
            public void onResponse(@NonNull Call<Acteur> call, @NonNull Response<Acteur> response) {
                stopLoadingSkeleton();
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Supprimé", Toast.LENGTH_SHORT).show();
                    fetchActeurs();
                } else if (response.code() == 404) {
                    Toast.makeText(MainActivity.this, "Déjà supprimé", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Erreur: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Acteur> call, @NonNull Throwable t) {
                stopLoadingSkeleton();
                Toast.makeText(MainActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showError(String msg) {
        textError.setText(msg);
        textError.setVisibility(View.VISIBLE);
    }

    // Search Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Rechercher...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { adapter.filter(query); return true; }
            @Override public boolean onQueryTextChange(String newText) { adapter.filter(newText); return true; }
        });
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override public boolean onMenuItemActionExpand(MenuItem item) { return true; }
            @Override public boolean onMenuItemActionCollapse(MenuItem item) { adapter.filter(""); return true; }
        });
        return true;
    }

    // Adapter Callbacks
    @Override
    public void onEdit(Acteur acteur) { openActeurDialog(acteur); }
    @Override
    public void onDelete(Acteur acteur) { confirmDelete(acteur); }
}