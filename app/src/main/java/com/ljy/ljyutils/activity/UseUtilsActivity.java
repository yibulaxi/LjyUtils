package com.ljy.ljyutils.activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljy.ljyutils.R;
import com.ljy.ljyutils.base.BaseActivity;
import com.ljy.util.LjyColorUtil;
import com.ljy.util.LjyDensityUtil;
import com.ljy.util.LjyEncryUtil;
import com.ljy.util.LjyLogUtil;
import com.ljy.util.LjyScreenUtils;
import com.ljy.util.LjyStringUtil;
import com.ljy.util.LjySystemUtil;
import com.ljy.util.LjyTimeUtil;

import java.math.BigDecimal;
import java.security.KeyPair;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UseUtilsActivity extends BaseActivity {

    @BindView(R.id.imageView)
    ImageView mImageView;
    @BindView(R.id.textView)
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_util);
        ButterKnife.bind(mActivity);
        LjyLogUtil.setAppendLogMsg(true);
        useUtil();
        LjyLogUtil.clearAllLogMsg();
        LjyLogUtil.setAppendLogMsg(false);
        LjyLogUtil.i("--->"+LjyLogUtil.getAllLogMsg());
    }

    private void useUtil() {
        //log:
        LjyLogUtil.i("log.i----");
        LjyLogUtil.w("log.w----");
        LjyLogUtil.e("log.e----");
        //判断当前栈顶的activity
        boolean isForeground = LjySystemUtil.isForeground(mContext, UseUtilsActivity.class.getSimpleName());
        LjyLogUtil.i("isForeground:" + isForeground);
        //screen
        int screenWidth = LjyScreenUtils.getScreenWidth(mContext);
        LjyLogUtil.i("screenWidth:" + screenWidth);
        int screenHeight = LjyScreenUtils.getScreenHeight(mContext);
        LjyLogUtil.i("screenHeight:" + screenHeight);
        //string
        LjyLogUtil.i("123asd:" + LjyStringUtil.isNumber("123asd"));
        LjyLogUtil.i("123:" + LjyStringUtil.isNumber("123"));

        BigDecimal decimal = new BigDecimal(Double.toString(12.5d));
        LjyLogUtil.i("12.5d:" + LjyStringUtil.keepAfterPoint(decimal, 3));
        //time
        long t1 = System.currentTimeMillis();
        LjyLogUtil.i("时间：" + LjyTimeUtil.timestampToDate(t1, null));
        long t2 = t1 + 1000 * 60 * 60 * 1;//+1小时
        long t3 = t1 + 1000 * 60 * 60 * 24;//+24小时
        LjyLogUtil.i("+1是否同一天：" + LjyTimeUtil.isSameDay(t1, t2));
        LjyLogUtil.i("+24是否同一天：" + LjyTimeUtil.isSameDay(t1, t3));
        //spUtil
        String key1 = "spKey1";
        String key2 = "spKey2";
        String key3 = "spKey3";
        String key4 = "spKey4";
        getSpUtilInstance().save(key1, true);
        getSpUtilInstance().save(key2, 123);
        getSpUtilInstance().save(key3, 1.2f);
        getSpUtilInstance().save(key4, "abc");
        LjyLogUtil.i(key1 + ":" + getSpUtilInstance().get(key1, false));
        LjyLogUtil.i(key2 + ":" + getSpUtilInstance().get(key2, 0));
        LjyLogUtil.i(key3 + ":" + getSpUtilInstance().get(key3, 0f));
        LjyLogUtil.i(key4 + ":" + getSpUtilInstance().get(key4, ""));
        LjyLogUtil.i("getAll:" + getSpUtilInstance().getAll());
        getSpUtilInstance().clearAll();
        LjyLogUtil.i("getAll:" + getSpUtilInstance().getAll());
        //EncryUtil
        int count = 3;
        String str1 = "刘123ab.|";
        LjyLogUtil.i("str1:" + str1);
        //凯撒加密
        String encodeCaesar = LjyEncryUtil.encodeCaesar(str1, count);
        LjyLogUtil.i("encodeCaesar:" + encodeCaesar);
        LjyLogUtil.i("decodeCaesar:" + LjyEncryUtil.decodeCaesar(encodeCaesar, count));
        //aes加密
        String aesKey = LjyEncryUtil.getAESKey();
        LjyLogUtil.i("aesKey:" + aesKey);
        String encodeAES = LjyEncryUtil.encodeAES(str1, aesKey, false);
        LjyLogUtil.i("encodeAES:" + encodeAES);
        LjyLogUtil.i("decodeAES:" + LjyEncryUtil.decodeAES(encodeAES, aesKey, false));
        encodeAES = LjyEncryUtil.encodeAES(str1, aesKey, true);
        LjyLogUtil.i("encodeAES_hex:" + encodeAES);
        LjyLogUtil.i("decodeAES_hex:" + LjyEncryUtil.decodeAES(encodeAES, aesKey, true));
        //des加密
        String ivStr = LjyEncryUtil.getDESIV();
        String encodeDES = LjyEncryUtil.encodeDES(str1, aesKey, ivStr);
        LjyLogUtil.i("encodeDES:" + encodeDES);
        LjyLogUtil.i("decodeDES:" + LjyEncryUtil.decodeDES(encodeDES, aesKey, ivStr));
        //RSA
        KeyPair key = LjyEncryUtil.getRsaKey(1024);
        String encodeRSA = LjyEncryUtil.encodeRSA(str1, key.getPublic());
        LjyLogUtil.i("encodeRSA:" + encodeRSA);
        LjyLogUtil.i("decodeRSA:" + LjyEncryUtil.decodeRSA(encodeRSA, key.getPrivate()));
        //MD5
        LjyLogUtil.i("getMD5:" + LjyEncryUtil.getMD5(str1));
        //SHA
        LjyLogUtil.i("getSHA256:" + LjyEncryUtil.getSHA(str1, LjyEncryUtil.SHA_256));
        LjyLogUtil.i("getSHA512:" + LjyEncryUtil.getSHA(str1, LjyEncryUtil.SHA_512));
        //二维码
        int size = LjyDensityUtil.dp2px(mContext, 200f);
        mImageView.setImageBitmap(LjyEncryUtil.getQrCode(str1, size, size, true));
        mImageView.setClickable(true);
        //一个view左右晃动的动画
        final Animation mShakeAnim = AnimationUtils.loadAnimation(mContext, R.anim.shake_x);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageView.startAnimation(mShakeAnim);
            }
        });

        int white=0xffffff;
        int blue= LjyColorUtil.blue(white);
        int green= LjyColorUtil.green(white);
        int red= LjyColorUtil.red(white);
        LjyLogUtil.i(String.format("white=%d,blue=%d,green=%d,red=%d",white,blue,green,red));




        //显示上面log的叠加
        mTextView.setText(LjyLogUtil.getAllLogMsg());
    }
}
