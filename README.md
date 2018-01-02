# FloatView
悬浮控件
需要自己获取权限

### 使用方式
> FloatView.getInstance(this)
                  .setView(view) //你的view  不设置也能显示默认的view
                  .setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          Toast.makeText(MainActivity.this,"我被点击了",Toast.LENGTH_SHORT).show();
                      }
                  })
                  .show();