package com.ui.article;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.C;
import com.app.annotation.apt.Extra;
import com.app.annotation.apt.Router;
import com.app.annotation.apt.SceneTransition;
import com.base.BaseActivity;
import com.base.util.ImageUtil;
import com.base.util.SpUtil;
import com.base.util.ViewUtil;
import com.data.Pointer;
import com.data.entity.Image;
import com.data.repository.CommentInfoRepository;
import com.google.gson.Gson;
import com.ui.login.LoginActivity;
import com.ui.main.R;
import com.view.layout.TRecyclerView;

import butterknife.Bind;

@Router(C.ARTICLE)
public class ArticleActivity extends BaseActivity<ArticlePresenter> implements ArticleContract.View {
    @Extra(C.HEAD_DATA)
    public Image mArticle;
    @SceneTransition(C.TRANSLATE_VIEW)
    @Bind(R.id.image)
    public ImageView image;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_comment)
    EditText et_comment;
    @Bind(R.id.bt_comment)
    Button bt_comment;
    @Bind(R.id.lv_comment)
    TRecyclerView lv_comment;

    @Override
    public int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    public void initView() {
        ImageUtil.loadImg(image, mArticle.image);
        setTitle(mArticle.title);
        bt_comment.setOnClickListener(v -> {
            String comment = et_comment.getText().toString();
            if (TextUtils.isEmpty(comment))
                Snackbar.make(fab, "评论不能为空!", Snackbar.LENGTH_LONG).show();
            else mPresenter.createComment(comment, mArticle, SpUtil.getUser());
        });
        String article = new Gson().toJson(new Pointer(Image.class.getSimpleName(), mArticle.objectId));

        lv_comment.setHeadView(R.layout.list_item_article, mArticle)
                .setViewAndRepository(R.layout.list_item_comment, CommentInfoRepository.class)
                .setParam(C.INCLUDE, C.CREATER)
                .setParam(C.ARTICLE, article)
                .setIsRefreshable(false)
                .fetch();
    }

    @Override
    public void commentSuc() {
        lv_comment.reFetch();
        Snackbar.make(fab, "评论成功!", Snackbar.LENGTH_LONG).show();
        ViewUtil.hideKeyboard(this);
        et_comment.setText("");
    }

    @Override
    public void commentFail() {
        Snackbar.make(fab, "评论失败!", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showLoginAction() {
        Snackbar.make(fab, "请先登录!", Snackbar.LENGTH_LONG)
                .setAction("登录", view -> startActivity(new Intent(mContext, LoginActivity.class))).show();
    }
}
