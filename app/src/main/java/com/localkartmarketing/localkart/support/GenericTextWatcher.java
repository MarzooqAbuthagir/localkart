package com.localkartmarketing.localkart.support;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.localkartmarketing.localkart.R;

public class GenericTextWatcher implements TextWatcher {
    private final EditText[] editText;
    private View view;

    public GenericTextWatcher(View view, EditText editText[]) {
        this.editText = editText;
        this.view = view;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        switch (view.getId()) {

            case R.id.otp_edit_box1:
                if (text.length() == 1) {
                    int len = editText[1].getText().toString().length();
                    if (len > 0) {
                        editText[1].setSelection(len);
                    } else {
                        editText[1].setSelection(0);
                    }
                    editText[1].requestFocus();
                }
                break;
            case R.id.otp_edit_box2:

                if (text.length() == 1) {
                    int len = editText[2].getText().toString().length();
                    if (len > 0) {
                        editText[2].setSelection(len);
                    } else {
                        editText[2].setSelection(0);
                    }
                    editText[2].requestFocus();
                } else if (text.length() == 0) {
                    int len = editText[0].getText().toString().length();
                    if (len > 0) {
                        editText[0].setSelection(len);
                    } else {
                        editText[0].setSelection(0);
                    }
                    editText[0].requestFocus();
                }
                break;
            case R.id.otp_edit_box3:
                if (text.length() == 1) {
                    int len = editText[3].getText().toString().length();
                    if (len > 0) {
                        editText[3].setSelection(len);
                    } else {
                        editText[3].setSelection(0);
                    }
                    editText[3].requestFocus();
                } else if (text.length() == 0) {
                    int len = editText[1].getText().toString().length();
                    if (len > 0) {
                        editText[1].setSelection(len);
                    } else {
                        editText[1].setSelection(0);
                    }
                    editText[1].requestFocus();
                }
                break;
            case R.id.otp_edit_box4:
                if (text.length() == 0) {
                    int len = editText[2].getText().toString().length();
                    if (len > 0) {
                        editText[2].setSelection(len);
                    } else {
                        editText[2].setSelection(0);
                    }
                    editText[2].requestFocus();
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }
}
