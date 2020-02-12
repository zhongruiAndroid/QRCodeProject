## 二维码扫描(全格式,库文件较多)
### [查看仅QR_CODE格式分支](https://github.com/zhongruiAndroid/QRCodeProject/tree/develop_qrcode)

## [全格式Demo.apk下载](https://raw.githubusercontent.com/zhongruiAndroid/QRCodeProject/master/demo/demo.apk)



##### 第一步(Activity实现QRCodeListener 接口)
```java

public class YourActivity extends AppCompatActivity implements QRCodeListener {
	
}
```
##### 第二步
```java
/*找到布局中的SurfaceView*/
SurfaceView surfaceView = findViewById(R.id.surfaceView);
/*扫描框view*/
CodeScanView codeScanView= findViewById(R.id.codeScanView);

 /*实例化CameraManager*/
CameraManager cameraManager = new CameraManager(this);
/*给surfaceView的宽高设置为手机分辨率大小,防止预览变形*/
CameraManager.setFullSurfaceView(this,surfaceView);

/*常规操作*/
surfaceHolder = surfaceView.getHolder();
surfaceHolder.addCallback(new SurfaceHolder.Callback() {
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
	    //启用相机并且设置预览界面
        cameraManager.surfaceCreated(context,holder);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraManager.surfaceDestroyed();
    }
});
```

##### 第三步(重写onResume和onPause方法，页面跳转时控制相机暂停和恢复)
```java
@Override
protected void onResume() {
    super.onResume();
    cameraManager.onResume(this);
}

@Override
protected void onPause() {
    super.onPause();
    cameraManager.onPause();
}


```


##### 第四步 (Activity实现QRCodeListener接口，设置重写方法需要返回的参数)
```java
@Override
public Activity getAct() {
    return this;
}
@Override
public Rect getScanRect() {
    /*扫描框所识别的范围,如果返回null,则识别整个SurfaceView范围*/
    /*CodeScanView为默认提供的一个自定义view，下面详细说明使用方法,getScanRectForWindow获取扫描范围*/
    return codeScanView.getScanRectForWindow();
}
@Override
public boolean needGetBitmapForSuccess() {
    /*如果需要扫描成功时的二维码可以返回true，如果没这个需求，建议返回false*/
    return true;
}
@Override
public int getMaxFrameNum() {
    /*同时解码的数量，返回0默认为6，最大值不会超过20，建议返回0或者6*/
    return 5;
}
@Override
public void onSuccess(Result rawResult, Bitmap bitmap) {
    /*rawResult:扫描结果，rawResult.getText()获取文字内容*/
    /*bitmap:扫描成功时的图片，needGetBitmapForSuccess返回false时为空*/
}

@Override
public List<String> getCodeFormat() {
    /*需要识别的一维码、二维码格式*/
    /*如果需要支持多种格式，建议把常用的放在上面*/
    List<String>list=new ArrayList<>();
    list.add(CodeFormat.QR_CODE);
    list.add(CodeFormat.AZTEC);
    list.add(CodeFormat.CODABAR);
    list.add(CodeFormat.CODE_39);
    list.add(CodeFormat.CODE_93);
    list.add(CodeFormat.CODE_128);
    list.add(CodeFormat.DATA_MATRIX);
    list.add(CodeFormat.EAN_8);
    list.add(CodeFormat.EAN_13);
    list.add(CodeFormat.ITF);
    list.add(CodeFormat.MAXICODE);
    list.add(CodeFormat.PDF_417);
    list.add(CodeFormat.RSS_14);

    /*返回null默认为CodeFormat.QR_CODE:常用的二维条码*/
    /*如果没有其他格式需求，建议返回null*/
    return null;
}


```
##### 第五步
建议SurfaceView和CodeScanView放在FrameLayout中，layout_width、layout_height、layout_margin属性一定要保持一致
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <SurfaceView
        android:id="@+id/surfaceView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
    <com.github.qrcode.CodeScanView
        android:id="@+id/codeScanView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</FrameLayout>

```

#### 闪光灯控制
```java
/*打开闪光灯*/
cameraManager.openLight(true);

/*关闭闪光灯*/
cameraManager.openLight(false);

/*打开和关闭闪光灯的前提是已经开启相机并且能看到预览界面,如果不满足这个条件调用上述方法没效果*/
```

#### 启用相机和暂停相机(一般用于页面跳转)
```java
/*启用相机*/
cameraManager.onResume(this);

/*暂停相机*/
cameraManager.onPause();
```
#### 恢复识别和暂停识别(暂停识别时，页面还能继续预览，只不过不能继续识别,一般用于在当前页面处理结果)
```java
/*恢复识别(前提是启用相机的状态)*/
cameraManager.startDetect();

/*暂停识别(没暂停相机)*/
cameraManager.stopDetect();
```

#### 注意！！！建议进入扫描页面之前请求相机权限，如果进入页面之后获取权限，第一次识别可能会有点慢
---

### 自定义扫描框CodeScanView使用
| 属性                | 说明                    |                                 |
|---------------------|-------------------------|---------------------------------|
| borderColor         | 边框颜色                | 默认#108EE9                     |
| borderWidth         | 边框宽度                | 默认1dp                         |
| cornerWidth         | 四个角的线宽            | 默认3dp                         |
| cornerLength        | 四个角的线长            | 默认20dp                        |
| cornerColor         | 四个角的颜色            | 默认#108EE9                     |
| cornerXOffset       | 四个角向内X方向的偏移量 | 默认0                           |
| cornerYOffset       | 四个角向内Y方向的偏移量 | 默认0                           |
| scanLineWidth       | 扫描线的宽度            | 默认3px                         |
| scanLineColor       | 扫描线的颜色            | 默认#108EE9                     |
| scanLineLeftOffset  | 扫描线离左边框的距离    | 默认0px                         |
| scanLineRightOffset | 扫描线离右边框的距离    | 默认0px                         |
| scanColor           | 扫描渐变起始颜色        | 默认#60108EE9(该颜色值有透明度) |
| showScanColor       | 是否显示扫描渐变色      | 默认true                        |
| downTime            | 扫描下落速度,单位毫秒   | 默认2200毫秒                    |
| centerDrawable      | 用图片代替扫描线        | 默认无图片                      |
| centerDrawableColor | 图片颜色                | 默认不改变颜色                  |
| scanBorderLeft      | 设置扫描框的左边距离    | 默认居中效果                    |
| scanBorderTop       | 设置扫描框的顶部距离    | 默认居中效果                    |
| scanBorderWidth     | 扫描框的宽度            | 默认为view宽度的3/5             |
| scanBorderHeight    | 扫描框的高度            | 默认为view宽度的3/5             |

#### 获取扫描框所对应的扫描范围
```java
/*建议SurfaceView和CodeScanView放在FrameLayout中，layout_width、layout_height、layout_margin属性一定要保持一致*/
/*否则扫描范围就会存在误差*/
codeScanView.getScanRectForWindow()
```
