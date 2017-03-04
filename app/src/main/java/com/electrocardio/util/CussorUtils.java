package com.electrocardio.util;

import android.text.Selection;
import android.text.Spannable;
import android.widget.EditText;

/**
 * Created by ZhangBo on 2016/2/24.
 */
public class CussorUtils {
    /**
     * 使光标停在句尾
     *
     * @param editText
     */
    public static void setCussour(EditText editText) {

        CharSequence userNameText = editText.getText();
        if (userNameText != null) {

            if (userNameText instanceof Spannable) {
                Spannable spannableText = (Spannable) userNameText;
                Selection.setSelection(spannableText, userNameText.length());
            }
        }
    }
}
