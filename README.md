## android-httpdns

在写android应用时，经常会需要通过网络调用http Api，或远程加载图片等。
但是我们经常遇到因域名解析而耗时很久的问题, 甚至因域名劫持访问到了错误的IP上。
为此国内最大的域名解析商[DNSPod](https://www.dnspod.cn)为app开发者推出了[D+(http dns)](https://www.dnspod.cn/httpdns)的服务来解决这个问题。
我在做一个图片app时也遇到了域名解析的问题，正好封装了一下。

- [源码在这里](https://github.com/onlytiancai/android-httpdns/blob/master/HttpDNS/app/src/main/java/com/ihuhao/app/httpdns/HttpDNS.java), 直接拷贝到项目里用就行
- [测试代码](https://github.com/onlytiancai/android-httpdns/blob/master/HttpDNS/app/src/main/java/com/ihuhao/app/httpdns/MainActivity.java)
- 其它文件基本都是Android Studio自动生成的，本项目可以Clone直接真机调试。

### 使用方法

如下

	String ip = HttpDNS.getAddressByName("www.dnspod.cn");
	Log.i("httpdns", String.format("getAddressByName: %s", ip));

	String json = HttpDNS.getStrWithHttpDNS("http://image.baidu.com/user/msg");
	Log.i("httpdns", String.format("getStrWithHttpDNS: %s", json));

	Bitmap img = HttpDNS.getBitmapWithHttpDNS("http://img0.bdstatic.com/static/common/widget/search_box_search/logo/logo_3b6de4c.png");
	Log.i("httpdns", String.format("getBitmapWithHttpDNS: %d", img.getByteCount()));

### 效果测试

测试代码如下

	String host = url.getHost();

	Log.i("HttpDNS resove:", "begin:" + host);
	String ip = HttpDNS.getAddressByName(host);
	Log.i("HttpDNS resove:", "end:" + host + " " + ip);

	Log.i("dns resove:", "begin:" + host);
	InetAddress address = InetAddress.getByName(host);
	Log.i("dns resove:", "end:" + host + " " + address.getAddress());

测试结果如下

	06-07 20:05:18.264  23990-24418/com.ihuhao.app.myapplication I/HttpDNS resove:﹕ begin:e.hiphotos.baidu.com
	06-07 20:05:18.354  23990-24418/com.ihuhao.app.myapplication I/getips﹕ getips: e.hiphotos.baidu.com 121.15.253.48
	06-07 20:05:18.354  23990-24418/com.ihuhao.app.myapplication I/cachedns﹕ add ips to cache:e.hiphotos.baidu.com
	06-07 20:05:18.354  23990-24418/com.ihuhao.app.myapplication I/cachedns﹕ get ips:e.hiphotos.baidu.com 1
	06-07 20:05:18.354  23990-24418/com.ihuhao.app.myapplication I/HttpDNS resove:﹕ end:e.hiphotos.baidu.com 121.15.253.48
	06-07 20:05:18.354  23990-24418/com.ihuhao.app.myapplication I/dns resove:﹕ begin:e.hiphotos.baidu.com
	06-07 20:05:38.454  23990-24418/com.ihuhao.app.myapplication I/dns resove:﹕ end:e.hiphotos.baidu.com [B@435f8ec0

可以看到使用http dns在本机没有缓存的情况下，解析域名用了不到1秒，而用InetAddress.getByName则花了20秒，我用真机测试的，联网方式是wifi。
改用电信3G方式测试，http dns比系统自带的解析慢300毫秒吧，我封装的库会缓存结果几分钟，后面的请求会快很多, 再就是我在电信3G模式下
有时候也经常解析10秒以上，但用http dns从来没鱼洞啊过，所以整体上来看用http dns还是很有优势的。


	06-07 20:14:40.444  28905-29173/com.ihuhao.app.myapplication I/HttpDNS resove:﹕ begin:c.hiphotos.baidu.com
	06-07 20:14:40.444  28905-29173/com.ihuhao.app.myapplication I/cachedns﹕ soft reference miss
	06-07 20:14:40.824  28905-29173/com.ihuhao.app.myapplication I/getips﹕ getips: c.hiphotos.baidu.com 121.15.253.48
	06-07 20:14:40.824  28905-29173/com.ihuhao.app.myapplication I/cachedns﹕ add ips to cache:c.hiphotos.baidu.com
	06-07 20:14:40.824  28905-29173/com.ihuhao.app.myapplication I/cachedns﹕ get ips:c.hiphotos.baidu.com 1
	06-07 20:14:40.824  28905-29173/com.ihuhao.app.myapplication I/HttpDNS resove:﹕ end:c.hiphotos.baidu.com 121.15.253.48
	06-07 20:14:40.824  28905-29173/com.ihuhao.app.myapplication I/dns resove:﹕ begin:c.hiphotos.baidu.com
	06-07 20:14:40.904  28905-29173/com.ihuhao.app.myapplication I/dns resove:﹕ end:c.hiphotos.baidu.com [B@435da0a0


### 其它说明

- 线程安全稍后支持
- 欢迎大家改进
