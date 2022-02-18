package shane.pennihome.local.smartboard.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * startSize+=step;
 * Created by shane on 04/03/18.
 */

@SuppressLint("AppCompatCustomView")
public class TextViewAutoFit extends TextView {

    public TextViewAutoFit(Context context) {
        super(context);
    }

    public TextViewAutoFit(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewAutoFit(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        measureText(w, h);
    }

    private void measureText(int width, int height) {
        if (width == 0 || height == 0)
            return;

        int targetWidth = width - this.getPaddingStart() - this.getPaddingEnd() - this.getPaddingLeft() - this.getPaddingRight();
        int targetHeight = height - this.getPaddingTop() - this.getPaddingBottom();

        String val = this.getText().toString();

        float startSize = this.getTextSize();

        Rect currentRect = new Rect();
        Paint sizer = new Paint();
        sizer.set(this.getPaint());

        sizer.getTextBounds(val, 0, val.length(), currentRect);
        float step = 4f;
        boolean downSize = currentRect.width() > targetWidth || currentRect.height() > targetHeight;

        if (downSize) {
            while (currentRect.width() > targetWidth || currentRect.height() > targetHeight) {
                startSize -= step;
                sizer.setTextSize(startSize);
                sizer.getTextBounds(val, 0, val.length(), currentRect);
            }
        } else {
            while (true) {
                startSize += step;
                sizer.setTextSize(startSize);
                sizer.getTextBounds(val, 0, val.length(), currentRect);
                if (currentRect.width() > targetWidth || currentRect.height() > targetHeight) {
                    startSize -= step;
                    break;
                }
            }
        }

        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, startSize - 16f);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        measureText(this.getWidth(), this.getHeight());
    }
}

