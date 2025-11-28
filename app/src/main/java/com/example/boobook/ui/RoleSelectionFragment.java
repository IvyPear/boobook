package com.example.boobook.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.boobook.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class RoleSelectionFragment extends Fragment {

    private MaterialCardView cardUser, cardAuthor, cardAdmin;
    private LinearLayout featuresContainer;
    private MaterialButton btnContinue;

    private String selectedRole = "User";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_role_selection, container, false);

        cardUser = view.findViewById(R.id.cardUser);
        cardAuthor = view.findViewById(R.id.cardAuthor);
        cardAdmin = view.findViewById(R.id.cardAdmin);
        featuresContainer = view.findViewById(R.id.featuresContainer);
        btnContinue = view.findViewById(R.id.btnContinue);

        setupRoleSelection();

        return view;
    }

    private void setupRoleSelection() {
        // Reset tất cả về trạng thái chưa chọn
        cardUser.setStrokeWidth(0);
        cardAuthor.setStrokeWidth(0);
        cardAdmin.setStrokeWidth(0);

        View.OnClickListener roleClick = v -> {
            // Reset tất cả
            cardUser.setStrokeWidth(0);
            cardAuthor.setStrokeWidth(0);
            cardAdmin.setStrokeWidth(0);

            // Highlight cái được bấm
            if (v instanceof MaterialCardView) {
                ((MaterialCardView) v).setStrokeWidth(4);
                ((MaterialCardView) v).setStrokeColor(0xFF3B82F6);
            }

            // Cập nhật theo role
            if (v.getId() == R.id.cardUser) {
                selectedRole = "User";
                updateFeatures(getUserFeatures());
                btnContinue.setText("Continue as User");
            } else if (v.getId() == R.id.cardAuthor) {
                selectedRole = "Author";
                updateFeatures(getAuthorFeatures());
                btnContinue.setText("Continue as Author");
            } else if (v.getId() == R.id.cardAdmin) {
                selectedRole = "Admin";
                updateFeatures(getAdminFeatures());
                btnContinue.setText("Continue as Admin");
            }
        };

        cardUser.setOnClickListener(roleClick);
        cardAuthor.setOnClickListener(roleClick);
        cardAdmin.setOnClickListener(roleClick);

        // Mặc định chọn User khi mở màn
        cardUser.performClick();
    }

    private String[] getUserFeatures() { return new String[]{"Browse Stories", "My Library", "Profile"}; }
    private String[] getAuthorFeatures() { return new String[]{"Create Blog", "Edit Posts", "My Blogs"}; }
    private String[] getAdminFeatures() { return new String[]{"Manage Users", "Manage Sliders", "Admin Panel"}; }

    private void updateFeatures(String[] features) {
        featuresContainer.removeAllViews();
        for (String feature : features) {
            View item = LayoutInflater.from(requireContext()).inflate(R.layout.item_feature, featuresContainer, false);
            TextView tv = item.findViewById(R.id.tvFeature);
            tv.setText(feature);
            featuresContainer.addView(item);
        }
    }
}