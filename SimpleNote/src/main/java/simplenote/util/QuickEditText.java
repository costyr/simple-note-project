package simplenote.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

/**
 * Created by Costi on 8/3/2014.
 *
 * Removes the IME_FLAG_NO_ENTER_ACTION flag to allow IME_ACTION_DONE when input is multiline.
 */
public class QuickEditText extends EditText
{
    public QuickEditText(Context context)
    {
        super(context);
    }

    public QuickEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public QuickEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs)
    {
        InputConnection conn = super.onCreateInputConnection(outAttrs);
        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return conn;
    }
}
