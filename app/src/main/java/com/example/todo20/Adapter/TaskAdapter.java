package com.example.todo20.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.todo20.Applications;
import com.example.todo20.Models.AppDatabase;
import com.example.todo20.Models.Task;
import com.example.todo20.Models.Task_Interface;
import com.example.todo20.OnItemClickListener;
import com.example.todo20.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.viewHolder>{
    private Context context;
    private List<Task> tasks;
    private OnItemClickListener itemClickListener;
    Date date = null;
    AppDatabase db;
    Task_Interface task_interface;
    String outputDateString = null;
    NotificationManager notificationManager ;

    public SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd MMM yyyy", Locale.US);
    public SimpleDateFormat inputDateFormat = new SimpleDateFormat("M/dd/yyyy", Locale.US);

    public TaskAdapter(List<Task> list, Context context, OnItemClickListener itemClickListener) {
        this.tasks = list;
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.notificationManager = notificationManager = (NotificationManager) Applications.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);

        return new viewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Task task = tasks.get(position);
        holder.title.setText(task.getTask_title());
        holder.description.setText(task.getTask_description());
        holder.time.setText(task.getTask_time());
        holder.priority.setText(task.getTask_priority().toString());
        holder.status.setText(task.getTask_status() ? "COMPLETED" : "INCOMPLETED");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(position);
            }
        });
      holder.options.setOnClickListener(view -> showPopUpMenu(view, position));

        try {
            date = inputDateFormat.parse(task.getTask_date());
            outputDateString = dateFormat.format(date);

            String[] items1 = outputDateString.split(" ");
            String day = items1[0];
            String dd = items1[1];
            String month = items1[2];

            holder.day.setText(day);
            holder.date.setText(dd);
            holder.month.setText(month);

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    public void UpdateList(List<Task> tasks){
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    private void showPopUpMenu(View view, int position) {
        final Task task = tasks.get(position);
        db = AppDatabase.getInstance(context.getApplicationContext());
        task_interface = db.task_interface();
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.task_option_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.delete:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("confirmation ").setMessage("Are you sure you want to delete").
                            setPositiveButton("yes", (dialog, which) -> {
                                task_interface.deletebyid(task.getId());
                                tasks.remove(task);
                                try {

                                    notificationManager.cancel(task.getId());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d("TAG", "onCreate: notification nh gayi" + e.getLocalizedMessage());
                                }
                                //notifyDataSetChanged();
                                Toast.makeText(context,"deleted",Toast.LENGTH_LONG).show();

                            })
                            .setNegativeButton("No", (dialog, which) -> dialog.cancel()).show();
                    break;

                case R.id.completed:
                    AlertDialog.Builder completeAlertDialog = new AlertDialog.Builder(context);
                    completeAlertDialog.setTitle("Confirmation").setMessage("Are you sure you want to mark this task as complete").
                            setPositiveButton("Yes", (dialog, which) -> {
                                task.setTask_status(true);
                                task_interface.updatestatus(task.getId(),true,false);
                                try {

                                    notificationManager.cancel(task.getId());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d("TAG", "onCreate: notification nh gayi" + e.getLocalizedMessage());
                                }
                             //   notifyDataSetChanged();
                                Toast.makeText(context,"Marked yes",Toast.LENGTH_LONG).show();})
                            .setNegativeButton("No", (dialog, which) ->{ Toast.makeText(context,"Marked No",Toast.LENGTH_LONG).show();
                                dialog.cancel();
                            }).show();
                    break;
            }
            return false;
        });
        popupMenu.show();

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView day;

        TextView date;

        TextView month;

        TextView title;

        TextView description;

        TextView status;

        ImageView options;

        TextView priority;

        TextView time;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            day=itemView.findViewById(R.id.day);
            month=itemView.findViewById(R.id.month);
            date=itemView.findViewById(R.id.date);
            title=itemView.findViewById(R.id.title);
            description=itemView.findViewById(R.id.description);
            status=itemView.findViewById(R.id.status);
            priority = itemView.findViewById(R.id.priority);
            options = itemView.findViewById(R.id.options);
            time= itemView.findViewById(R.id.time);


        }
    }


}
