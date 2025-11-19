package com.example.sportine.ui.usuarios.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.Comentario;
import com.example.sportine.ui.usuarios.dto.ComentarioRequest;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComentariosFragment extends BottomSheetDialogFragment {

    private static final String ARG_POST_ID = "arg_post_id";
    private Integer postId;

    private RecyclerView rvComments;
    private EditText etCommentInput;
    private ImageView btnSend;

    private ComentariosAdapter adapter;
    private ApiService apiService;

    // Método estático para crear el fragmento fácilmente pasándole el ID
    public static ComentariosFragment newInstance(Integer postId) {
        ComentariosFragment fragment = new ComentariosFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getInt(ARG_POST_ID);
        }

        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comentarios, container, false);

        rvComments = view.findViewById(R.id.rv_comments);
        etCommentInput = view.findViewById(R.id.et_comment_input);
        btnSend = view.findViewById(R.id.btn_send_comment);

        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ComentariosAdapter(new ArrayList<>());
        rvComments.setAdapter(adapter);

        cargarComentarios();

        btnSend.setOnClickListener(v -> enviarComentario());

        return view;
    }

    private void cargarComentarios() {
        apiService.verComentarios(postId).enqueue(new Callback<List<Comentario>>() {
            @Override
            public void onResponse(Call<List<Comentario>> call, Response<List<Comentario>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    android.util.Log.d("ComentariosDebug", "Llegaron " + response.body().size() + " comentarios");

                    adapter.setComentarios(response.body());

                    if (!response.body().isEmpty()) {
                        rvComments.scrollToPosition(response.body().size() - 1);
                    }
                } else {
                    android.util.Log.e("ComentariosDebug", "Error en API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Comentario>> call, Throwable t) {
                android.util.Log.e("ComentariosDebug", "Fallo red: " + t.getMessage());
            }
        });
    }

    private void enviarComentario() {
        String texto = etCommentInput.getText().toString().trim();
        if (texto.isEmpty()) return;

        btnSend.setEnabled(false);

        ComentarioRequest request = new ComentarioRequest(texto);

        apiService.crearComentario(postId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnSend.setEnabled(true);
                if (response.isSuccessful()) {
                    etCommentInput.setText("");
                    cargarComentarios();
                } else {
                    Toast.makeText(getContext(), "Error al enviar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnSend.setEnabled(true);
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}