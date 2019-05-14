package io.github.kachaya.wnnengine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;

import jp.co.omronsoft.openwnn.JAJP.OpenWnnEngineJAJP;
import jp.co.omronsoft.openwnn.JAJP.Romkan;
import jp.co.omronsoft.openwnn.ComposingText;
import jp.co.omronsoft.openwnn.StrSegment;
import jp.co.omronsoft.openwnn.WnnWord;
import jp.co.omronsoft.openwnn.LetterConverter;

public class MainActivity extends AppCompatActivity {

    private OpenWnnEngineJAJP mConverter;
    private LetterConverter mPreConverter;
    private ComposingText mComposingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File filesDir = getFilesDir();
        String systemDictionaryName = new File(filesDir.getParent(), "/lib/libWnnJpnDic.so").getPath();
        String writableDictionaryName = new File(filesDir, "/wratableJAJP.dic").getPath();

        // かな漢字変換の初期化
        mConverter = new OpenWnnEngineJAJP(systemDictionaryName, writableDictionaryName);
        mConverter.init();

        // ローマ字・かな変換
        mPreConverter = new Romkan();

        // 変換結果が格納されます
        mComposingText = new ComposingText();

        insert("k");
        insert("a");
        insert("n");
        insert("j");
        insert("i");
        insert("n");
        insert("i");
        insert("h");
        insert("e");
        insert("n");
        insert("k");
        insert("a");
        insert("n");
        insert("n"); // 'ん'をかなに変換させておかないと、nのまま独立した文節になってしまう？

        // かなを漢字に変換します。変換結果はLAYER2に格納されます。
        int res = mConverter.convert(mComposingText);

        // 連文節変換の文節ごとに変換候補を取得します。
        for (int i = 0; i < mComposingText.size(ComposingText.LAYER2); ++i)
        {
            // 文節の変換結果です。
            String clause = mComposingText.toString(ComposingText.LAYER2, i, i);

            // 文節のその他の候補を取得できるようにします。
            if (0 < mConverter.makeCandidateListOf(i))
            {
                // 文節の変換候補を取得します。
                WnnWord word;
                while ((word = mConverter.getNextCandidate()) != null)
                {
                }
            }
        }

        mConverter.close();
    }

    private boolean insert(String str)
    {
        // 入力文字はLAYER0とLAYER1に追加します。
        mComposingText.insertStrSegment(ComposingText.LAYER0, ComposingText.LAYER1, new StrSegment(str));

        // 一文字入力するたびにかなに変換します。
        // 入力された文字がかなに変換できた時は true を返ります。
        return mPreConverter.convert(mComposingText);
    }
}