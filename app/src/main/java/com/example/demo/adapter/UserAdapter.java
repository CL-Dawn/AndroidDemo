package com.example.demo.adapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.R;
import com.example.demo.listelement.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onFollowClick(int position, User user);
        void onUnfollowClick(int position, User user);
        void onSpecialClick(int position, User user, boolean isSpecial);
    }

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void updateData(List<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);


        holder.btnFollow.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onFollowClick(position, user);
            }
        });


        holder.ivAvatar.setOnClickListener(v -> {
            Toast.makeText(v.getContext(),
                    "已选中 " + user.getName() ,
                    Toast.LENGTH_SHORT).show();
        });
        holder.btnSetting.setOnClickListener(v -> {
            showSettingDialog(v.getContext(), user,position);
        });


    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    // 创建并显示设置对话框
    private void showSettingDialog(Context context, User user,int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_user_settings, null);
        Switch toggleSwitch = dialogView.findViewById(R.id.switch_setting);
        TextView remarkText = dialogView.findViewById(R.id.text_remarks);
        TextView deleteText = dialogView.findViewById(R.id.text_delete);
        TextView usernameText = dialogView.findViewById(R.id.text_username);

        usernameText.setText(user.getName());
        toggleSwitch.setChecked(user.isSpecial());
        if (user.getRemark() != null) {
            usernameText.setText(user.getRemark());
        }
        toggleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (onItemClickListener != null) {
                onItemClickListener.onSpecialClick(position, user, isChecked);
            }
        });
        remarkText.setOnClickListener(v -> {
            showRemarkEditDialog(context, user, position, bottomSheetDialog);
        });
        deleteText.setOnClickListener(v -> {
            showUnfollowConfirmationDialog(context, user, position, bottomSheetDialog);
        });
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
    }

    private void showUnfollowConfirmationDialog(Context context, User user, int position,
                                                BottomSheetDialog bottomSheetDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("取消关注")
                .setMessage("确定不再关注 " + user.getName() + " 吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onUnfollowClick(position, user);
                        }

                        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                            bottomSheetDialog.dismiss();
                        }

                        Toast.makeText(context, "已取消关注 " + user.getName(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }
    private void showRemarkEditDialog(Context context, User user, int position,
                                      BottomSheetDialog bottomSheetDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("设置备注");

        // 创建输入框
        final EditText input = new EditText(context);
        input.setHint("请输入备注");
        if (user.getRemark() != null && !user.getRemark().isEmpty()) {
            input.setText(user.getRemark());
        }
        builder.setView(input);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String remark = input.getText().toString().trim();
                // 这里可以添加更新备注的逻辑
                user.setRemark(remark);
                Toast.makeText(context, "备注已更新", Toast.LENGTH_SHORT).show();

                // 刷新当前项显示
                notifyItemChanged(position);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        Button btnFollow;
        ImageView ivAvatar;
        Button btnSetting;
        TextView tvSpecialIndicator;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            btnFollow = itemView.findViewById(R.id.btn_follow);
            ivAvatar=itemView.findViewById(R.id.iv_avatar);
            btnSetting=itemView.findViewById(R.id.btn_setting);
            tvSpecialIndicator = itemView.findViewById(R.id.tv_special_indicator);
        }

        public void bind(User user) {
            tvUserName.setText(user.getRemarkOrName());
            ivAvatar.setImageResource(user.getAvatarResId());
            if (user.isSpecial()) {
                tvSpecialIndicator.setVisibility(View.VISIBLE);
            } else {
                tvSpecialIndicator.setVisibility(View.GONE);
            }
            if (user.isFollowed()) {
                btnFollow.setText("已关注");
                btnFollow.setSelected(true);
            } else {
                btnFollow.setText("关注");
                btnFollow.setSelected(false);
            }
        }
    }
}
