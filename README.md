# SimpleReader

简单阅读，欢迎star和fork
=======================

1.先上效果图
------------
<img src="https://github.com/97999/SimpleReader/blob/master/images/screenshot1.jpg" width="300"/>

<img src="https://github.com/97999/SimpleReader/blob/master/images/screenshot2.jpg" width="300"/>

<img src="https://github.com/97999/SimpleReader/blob/master/images/screenshot3.jpg" width="300"/>

<img src="https://github.com/97999/SimpleReader/blob/master/images/screenshot4.jpg" width="300"/>

<img src="https://github.com/97999/SimpleReader/blob/master/images/screenshot5.jpg" width="300"/>

<img src="https://github.com/97999/SimpleReader/blob/master/images/screenshot6.jpg" width="300"/>

2.读取本地文件问题
-----------------
所有的文件目录显示都是用的recyclerView。然后点击会直接添加到书架。

3.网络爬取网络书籍
-----------------
采用JSoup+OkHttp3 实现爬虫效果，会根据搜索的关键字并发爬取相关网页返回结果。

4.兼容本地和网络模式
-------------------
阅读有两种模式，点开本地书为本地阅读模式，点开网络书籍为在线阅读模式。

5.UI界面
--------
典型的有三个自定义UI设计，为安卓自定义控件，主界面底部为仿微信设计，然后等待Loading为随便写的一个动画，最后阅读界面是自定义的一个根据文字来绘画的控件。在包目录下UI文件夹有详细code。

6.最后说下，软件很多功能并不完善，主要是大体写了一个框架。下面说下关于软件完善方面的细节。
---------------------------------------------------------------------------------------------------------------

###### 1.获取目录
首先说说在线阅读，由于本人设计的在线阅读加载，跳转都是交给一个单例模式的管理类来实现的，那么加载目录只需调用该类的返回目录集合的函数即可。
然后是本地阅读，可以采取io流读取，每次读一行，然后正则匹配是否存在章节直到文件末尾，并记录每个章节在多少行，然后跳转到具体章节时计算所在字节数。
###### 2.缓存功能
缓存的话，可以将每个章节的内容存到数据库里面，每次打开时先检查数据库是否有相应的章节，有的话直接打开，没有的话再去网上加载。
###### 3.阅读时，长按实现选择复制
网上相关的方法很多，并不太复杂。
###### 4.阅读界面的滑动动画
由于我是采用的文字绘画的方法，先将文字根据一定的规则绘制到bitmap上，然后显示bitmap。制作滑动动画的话，得到下一张bitmap后，两张bitmap制动动画还是很简单的，比如制作简单的模拟滑动效果，一张在左，一张在右，控制两张bitmap显示的比列就行。其他效果同样还是扩展很方便的，具体可以参考我写的ReaderView代码。笔者由于考验时间有限，没有将之实现。欢迎读者扩展。

