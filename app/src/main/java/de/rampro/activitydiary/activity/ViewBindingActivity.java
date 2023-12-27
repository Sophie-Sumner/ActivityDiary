package de.rampro.activitydiary.activity;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;

import de.rampro.activitydiary.R;
import de.rampro.activitydiary.util.ReflectUtil;


public class ViewBindingActivity<VB extends ViewBinding> extends BaseActivity {
    protected VB binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ReflectUtil.getBinding(getLayoutInflater(), this.getClass());
        setContentView(binding.getRoot());

//        Log.d("view bind", this.getClass()+"");
        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //添加默认返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回可用
    }



}
