package com.example.sportine.ui.usuarios.social;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportine.R;
import com.example.sportine.models.Notificacion;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotifViewHolder> {

    private List<Notificacion> lista;
    private Context context;
    private PrettyTime prettyTime = new PrettyTime(new Locale("es"));

    public NotificationsAdapter(List<Notificacion> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        Notificacion notif = lista.get(position);

        // 1. ✅ OBTENER NOMBRE REAL (Prioridad: nombreActor > "Usuario")
        String nombreMostrar = (notif.getNombreActor() != null && !notif.getNombreActor().isEmpty())
                ? notif.getNombreActor()
                : "Usuario";

        // Formato: "Juan Perez le dio like..."
        String texto = "<b>" + nombreMostrar + "</b> " + notif.getMensaje();
        holder.tvTexto.setText(Html.fromHtml(texto, Html.FROM_HTML_MODE_LEGACY));

        // 2. FECHA (Sin cambios, tu lógica estaba bien)
        try {
            // Nota: Si tu backend manda LocalDateTime (ej: 2023-10-25T10:00:00), este formato funciona.
            // Si agrega milisegundos, ajusta a "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            // Asumimos que el server manda la hora local o UTC. Si es UTC:
            // sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = sdf.parse(notif.getFecha());
            holder.tvTiempo.setText(prettyTime.format(date));
        } catch (Exception e) {
            // Fallback si el formato no coincide exacto
            holder.tvTiempo.setText("Reciente");
        }

        // 3. ÍCONOS DE TIPO (Sin cambios)
        switch (notif.getTipo()) {
            case "LIKE":
                holder.ivIconoTipo.setImageResource(R.drawable.ic_favorite_black_24dp);
                holder.ivIconoTipo.setColorFilter(Color.RED);
                break;
            case "COMENTARIO":
                holder.ivIconoTipo.setImageResource(R.drawable.ic_chat_bubble_outline_black_24dp);
                holder.ivIconoTipo.setColorFilter(Color.BLUE);
                break;
            case "SEGUIDOR":
                holder.ivIconoTipo.setImageResource(R.drawable.ic_person_add_black_24dp);
                holder.ivIconoTipo.setColorFilter(Color.parseColor("#4CAF50")); // Verde
                break;
            default:
                holder.ivIconoTipo.setImageResource(R.drawable.ic_notifications_black_24dp);
                holder.ivIconoTipo.setColorFilter(Color.GRAY);
                break;
        }

        // 4. ✅ CARGAR FOTO DE PERFIL CON GLIDE
        // Si fotoActor es null, Glide usa el placeholder automáticamente
        Glide.with(context)
                .load(notif.getFotoActor()) // <--- URL que viene del backend
                .placeholder(R.drawable.avatar_user_male) // Mientras carga o si es null
                .error(R.drawable.avatar_user_male)       // Si falla la carga
                .circleCrop()
                .into(holder.ivAvatar);
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public void setLista(List<Notificacion> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    static class NotifViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar, ivIconoTipo;
        TextView tvTexto, tvTiempo;

        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_notif_avatar);
            ivIconoTipo = itemView.findViewById(R.id.iv_notif_type_icon);
            tvTexto = itemView.findViewById(R.id.tv_notif_text);
            tvTiempo = itemView.findViewById(R.id.tv_notif_time);
        }
    }
}