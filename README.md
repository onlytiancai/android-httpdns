## android-httpdns

在写android应用时，经常会需要通过网络调用http Api，或远程加载图片等。
但是我们经常遇到因域名解析而耗时很久的问题, 甚至因域名劫持访问到了错误的IP上。
为此国内最大的域名解析商[DNSPod](https://www.dnspod.cn)为app开发者推出了[D+(http dns)](https://www.dnspod.cn/httpdns)的服务来解决这个问题。
我在做一个图片app时也遇到了域名解析的问题，正好封装了一下。

### 使用

如下

	String ip = HttpDNS.getAddressByName("www.dnspod.cn");
	Log.i("httpdns", String.format("getAddressByName: %s", ip));

	String json = HttpDNS.getStrWithHttpDNS("http://image.baidu.com/user/msg");
	Log.i("httpdns", String.format("getStrWithHttpDNS: %s", json));

	Bitmap img = HttpDNS.getBitmapWithHttpDNS("http://img0.bdstatic.com/static/common/widget/search_box_search/logo/logo_3b6de4c.png");
	Log.i("httpdns", String.format("getBitmapWithHttpDNS: %d", img.getByteCount()));

### 其它说明

- 线程安全稍后支持
- 欢迎大家改进
