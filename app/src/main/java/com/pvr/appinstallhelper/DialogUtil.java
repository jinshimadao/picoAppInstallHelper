package com.pvr.appinstallhelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

/**
 * Created by goodman.ye on 2019/8/6.
 */
public class DialogUtil {
    public Context context;
    private static ProgressDialog progressDialog;

    private DialogUtil() {
    }

    /**
     * 创建并展示一个简单的进度对话框，该对话框不显示文字且阻止用户关闭
     *
     * @param context Context
     * @return 对话框对象
     */
    public static ProgressDialog showProDialog(Context context) {
        return showProDialog(context, "请稍候...");
    }

    /**
     * 创建并展示一个简单的进度对话框，该对话框阻止用户关闭
     *
     * @param context Context
     * @return 对话框对象
     */
    public static ProgressDialog showProDialog(Context context, String str) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setMessage(str);
            return progressDialog;
        }
        progressDialog = new ProgressDialog(context);
        if (str != null && !str.equals("")) {
            progressDialog.setMessage(str);
        }
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    public static void setProDialog(String str) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setMessage(str);
        }
    }

    /**
     * 关闭对话框
     */
    public static void dismissProDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /* -----------------------流式API调用以下方法创建对话框----------------------- */

    private static DialogUtil dialogUtil;
    private static AlertDialog.Builder builder;

    public static final int POSITIVE = 1;  //点击的是确定按钮
    public static final int NEGATIVE = 2;  //点击是取消按钮
    public static final int NEUTRAL = 3;  //点击是中性按钮

    /**
     * 初始化,应最先调用
     *
     * @param context Context
     * @return 当前类对象DialogUtil
     */
    public static DialogUtil init(Context context) {
        dialogUtil = new DialogUtil();
        builder = new AlertDialog.Builder(context);
        dialogUtil.context = context;
        return dialogUtil;
    }


    /**
     * 设置标题
     *
     * @param title 标题内容
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setTitle(String title) {
        builder.setTitle(title);
        return this;
    }

    /**
     * 设置文本
     *
     * @param message 文本内容
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setMessage(String message) {
        builder.setMessage(message);
        return this;
    }

    /**
     * 设置标题和文本
     * 需在init方法之后调用
     *
     * @param title   标题内容
     * @param message 文本内容
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setInfo(String title, String message) {
        builder.setTitle(title)
                .setMessage(message);
        return this;
    }

    /**
     * 设置列表项对话框
     *
     * @param items    CharSequence数组,用于显示条目
     * @param listener 子条目点击监听器
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setItems(CharSequence[] items, DialogInterface.OnClickListener listener) {
        builder.setItems(items, listener);
        return this;
    }

    /**
     * 设置列表项对话框
     *
     * @param itemsId  字符串数组资源id,用于显示条目
     * @param listener 子条目点击监听器
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setItems(int itemsId, DialogInterface.OnClickListener listener) {
        builder.setItems(itemsId, listener);
        return this;
    }

    /**
     * 设置列表项对话框
     *
     * @param items    List集合,用于显示条目
     * @param listener 子条目点击监听器
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setItems(List<String> items, DialogInterface.OnClickListener listener) {
        String[] strings = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            strings[i] = items.get(i);
        }
        builder.setItems(strings, listener);
        return this;
    }

    /**
     * 设置自定义视图
     *
     * @param view View
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setView(View view) {
        builder.setView(view);
        return this;
    }

    /**
     * 设置自定义视图
     *
     * @param layoutResId layoutResId
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setView(int layoutResId) {
        builder.setView(layoutResId);
        return this;
    }



    /**
     * 设置"确定"和"取消"按钮以及按钮的点击监听,默认按钮文字为"确定"和"取消"
     * 需在init方法之后调用
     *
     * @param listener 按钮点击监听器
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setButton(OnClickListener listener) {
        return setButton("确定", "取消", listener);
    }


    /**
     * 设置"确定"和"取消"按钮以及按钮的点击监听,按钮文字自定义指定
     * 需在init方法之后调用
     *
     * @param positive 确定按钮文字
     * @param negative 取消按钮文字
     * @param listener 按钮点击监听器
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setButton(String positive, String negative, final OnClickListener listener) {
        if (TextUtils.isEmpty(positive)) {
            positive = "确定";
        }
        if (TextUtils.isEmpty(negative)) {
            negative = "取消";
        }
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (listener != null) {
                    listener.onClick(dialogInterface, POSITIVE);
                }
            }
        }).setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (listener != null) {
                    listener.onClick(dialogInterface, NEGATIVE);
                }
            }
        });
        return this;
    }

    /**
     * 设置"确定"、"取消"按钮和"中性"按钮以及按钮的点击监听,按钮文字自定义指定
     * 需在init方法之后调用
     *
     * @param positive 确定按钮文字
     * @param negative 取消按钮文字
     * @param neutral  中性按钮文字
     * @param listener 按钮点击监听器
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setButton(String positive, String negative, String neutral, final OnClickListener listener) {
        if (TextUtils.isEmpty(positive)) {
            positive = "确定";
        }
        if (TextUtils.isEmpty(negative)) {
            negative = "取消";
        }
        if (neutral == null) {
            neutral = "";
        }
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (listener != null) {
                    listener.onClick(dialogInterface, POSITIVE);
                }
            }
        }).setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (listener != null) {
                    listener.onClick(dialogInterface, NEGATIVE);
                }
            }
        }).setNeutralButton(neutral, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onClick(dialog, NEUTRAL);
                }
            }
        });
        return this;
    }

    /**
     * 设置按钮,只有一个确定按钮
     *
     * @param listener 确定按钮 的监听器
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setOnlyButton(final OnClickListener listener) {
        return setOnlyButton("确定", listener);
    }

    /**
     * 设置按钮,只有一个确定按钮
     *
     * @param positive 确定按钮文字
     * @param listener 确定按钮 的监听器
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setOnlyButton(String positive, final OnClickListener listener) {
        if (TextUtils.isEmpty(positive)) {
            positive = "确定";
        }
        if (listener != null) {
            builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    listener.onClick(dialogInterface, POSITIVE);
                }
            });
        } else {
            builder.setPositiveButton(positive, null);
        }
        return this;
    }

    /**
     * 获取AlertDialog.Builder对象
     *
     * @return AlertDialog.Builder
     */
    public AlertDialog.Builder builder() {
        return builder;
    }

    /**
     * 显示对话框,并返回AlertDialog对象
     *
     * @return 设置完毕的AlertDialog对象
     */
    public AlertDialog show() {
        return builder.show();
    }


    /**
     * 对话框按钮点击事件监听器
     */
    public interface OnClickListener {
        /**
         * 点击确定或取消按钮回调方法
         *
         * @param dialogInterface DialogInterface
         * @param flag            若flag为POSITIVE则点击的是确定按钮,若flag为NEGATIVE则点击的是取消按钮
         */
        void onClick(DialogInterface dialogInterface, int flag);
    }

    /* -----------------------流式API调用以下方法定制对话框样式----------------------- */

    private static AlertDialog dialog;
    private static Window window;

    /**
     * 应最先调用，开始对话框样式的定制
     *
     * @param dialog AlertDialog
     * @return 当前类对象DialogUtil
     */
    public static DialogUtil setting(AlertDialog dialog) {
        if (dialog != null) {
            DialogUtil.dialog = dialog;
            window = dialog.getWindow();
        }
        if (dialogUtil == null) {
            dialogUtil = new DialogUtil();
        }
        return dialogUtil;
    }

    /**
     * 设置宽高
     *
     * @param width  宽
     * @param height 高
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setLayout(int width, int height) {
        if (window != null) {
            window.setLayout(width, height);
        }
        return this;
    }

    /**
     * 设置Gravity
     *
     * @param gravity gravity
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setGravity(int gravity) {
        if (window != null) {
            window.setGravity(gravity);
        }
        return this;
    }

    /**
     * 设置Gravity
     *
     * @param gravity gravity
     * @param xoff    x方向的偏移量
     * @param yoff    y方向的偏移量
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setGravity(int gravity, int xoff, int yoff) {
        if (window != null) {
            window.setGravity(gravity);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.x = xoff;
            lp.y = yoff;
            window.setAttributes(lp);
        }
        return this;
    }

    /**
     * 设置对话框弹出关闭动画，可以在style定义，属性为windowEnterAnimation和windowExitAnimation
     *
     * @param resId 资源id
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setWindowAnimations(int resId) {
        if (window != null) {
            window.setWindowAnimations(resId);
        }
        return this;
    }

    /**
     * 设置对话框弹出时，后面的页面透明度
     *
     * @param activity Activity
     * @param alpha    透明度0-1
     * @return 当前类对象DialogUtil
     */
    public DialogUtil setBgAlpha(Activity activity, float alpha) {
        if (activity != null) {
            if (alpha > 1 || alpha < 0) {
                return this;
            }
            final Window activityWindow = activity.getWindow();
            WindowManager.LayoutParams lp = activityWindow.getAttributes();
            lp.alpha = alpha;
            activityWindow.setAttributes(lp);

            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    WindowManager.LayoutParams lp = activityWindow.getAttributes();
                    lp.alpha = 1;
                    activityWindow.setAttributes(lp);

                }
            });
        }
        return this;
    }
}
