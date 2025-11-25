package com.example.sportine.ui.usuarios.social;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.transition.TransitionInflater;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.sportine.R;

public class DetallePostFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. ANIMACIÓN DE ENTRADA Y SALIDA
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        setEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.fade));
        setExitTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.fade));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detalle_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) return;

        String urlImagen = args.getString("imagenUrl");
        String transitionName = args.getString("transitionName");

        ImageView ivDetalle = view.findViewById(R.id.iv_detalle_imagen_grande);
        ImageView btnCerrar = view.findViewById(R.id.btn_close_detalle);

        // 3. ASIGNAR EL NOMBRE DE TRANSICIÓN
        ivDetalle.setTransitionName(transitionName);

        // 4. POSPONER LA TRANSICIÓN (Esperar a Glide)
        postponeEnterTransition();

        Glide.with(this)
                .load(urlImagen)
                .dontAnimate()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(ivDetalle);

        // 5. BOTÓN CERRAR (Regresar)
        btnCerrar.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());
        // Cerrar también al tocar la imagen
        ivDetalle.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());
    }
}