# SimpleReader

简单阅读，欢迎star和fork
=======================

1.先上效果图
------------
<img src="https://github.com/97999/SimpleReader/blob/master/images/screenshot1.jpg" width="300" height="450"/>

<img src="https://github.com/97999/SimpleReader/blob/master/images/screenshot2.jpg" width="300" height="450"/>

<img src="https://github.com/97999/SimpleReader/blob/master/images/screenshot3.jpg" width="300" height="450"/>

<img src="https://github.com/97999/SimpleReader/blob/master/images/screenshot4.jpg" width="300" height="450"/>

<img src="https://github.com/97999/SimpleReader/blob/master/images/screenshot5.jpg" width="300" height="450"/>

<img src="https://github.com/97999/SimpleReader/blob/master/images/screenshot6.jpg" width="300" height="450"/>

2.读取本地文件问题
-----------------
所有的文件目录显示都是用的revyvlerView。然后点击会直接添加到书架。

3.网络爬取网络书籍
-----------------
采用Jsoup+OkHttp3 实现爬虫效果，会根据搜索的关键字并发爬取相关网页返回结果。

4.兼容本地和网络模式
-------------------
阅读有两种模式，点开本地书为本地阅读模式，点开网络书籍为在线阅读模式。

5.UI界面
--------
典型的有两个UI设计，主界面底部为仿微信设计，然后等待Loading为随便写的一个动画。在包目录下UI文件夹与详细code。

6.最后说下，由于本菜鸟决定考研学点东西啦，软件很多功能并不完善，主要是大体写了一个框架。下面说下关于软件完善方面的细节。(本人刚入门，没啥经验，请轻喷)
---------------------------------------------------------------------------------------------------------------

###### 1.获取目录
首先说说在线阅读，由于本人设计的在线阅读加载，跳转都是交给一个单例模式的管理类来实现的，那么加载目录只需调用该类的返回目录集合的函数即可。
然后是本地阅读，可以采取io流读取，每次读一行，然后正则匹配是否存在章节直到文件末尾，并记录每个章节在多少行，然后跳转到具体章节时计算所在字节数。
本人暂时没有其他思路。
###### 2.缓存功能
缓存的话，可以将每个章节的内容存到数据库里面，每次打开时先检查数据库是否有相应的章节，有的话直接打开，没有的话再去网上加载。
###### 3.阅读时长按实现选择复制
网上相关的方法很多，并不太复杂，随便抄一个就好。
###### 4.其他的细节有待优化，另外，考研加油，加油！！！
