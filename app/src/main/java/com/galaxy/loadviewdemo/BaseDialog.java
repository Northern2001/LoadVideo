package com.galaxy.loadviewdemo;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public abstract class BaseDialog extends DialogFragment {
    private static boolean isShowing = false;

    protected abstract int getDialogLayout();

    Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getDialogLayout(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getDialog() != null) hideSystemUi(getDialog().getWindow());
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() != null) hideSystemUi(getDialog().getWindow());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    @Override
    public void show(@NonNull FragmentManager manager, String tag) {
        try {
            if (isShowing) return;

            super.show(manager, tag);
            isShowing = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showNow(@NonNull FragmentManager manager, @Nullable String tag) {
        try {
            if (isShowing) return;

            super.showNow(manager, tag);
            isShowing = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        isShowing = false;
        super.onDismiss(dialog);
    }

    public static void setIsShowing(boolean isShowing) {
        BaseDialog.isShowing = isShowing;
    }

    public static boolean getIsShowing() {
        return isShowing;
    }

    protected void setupDialog(int width, int height) {
        if (getDialog() == null || getDialog().getWindow() == null) {
            return;
        }
        //set dialog size
        getDialog().getWindow().setLayout(width,
                height);
    }
}
