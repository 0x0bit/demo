# 下载网页

这里先从Golang原生http库开始，直接使用`net/http`包内的函数请求

```go
import "net/http"
...
resp, err := http.Get("http://wwww.baidu.com")
```



所以代码可以这样写

```go
package main

import (
	"fmt"
	"io/ioutil"
	"net/http"
)

func main() {
	resp, err := http.Get("http://www.baidu.com/")
	if err != nil {
		fmt.Println("http get error", err)
		return
	}
  
	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		fmt.Println("read error", err)
		return
	}
	fmt.Println(string(body))
}
```

Golang的错误处理就是这样的，习惯就好。

这里更好的做法是把下载方法封装为函数。

```go
package main

import (
	"fmt"
	"io/ioutil"
	"net/http"
)

func main() {
	fmt.Println("Hello, world")
	url := "http://www.baidu.com/"
	download(url)
}

func download(url string) {
	client := &http.Client{}
	req, _ := http.NewRequest("GET", url, nil)
	// 自定义Header
	req.Header.Set("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)")

	resp, err := client.Do(req)
	if err != nil {
		fmt.Println("http get error", err)
		return
	}
  
	//函数结束后关闭相关链接
	defer resp.Body.Close()

	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		fmt.Println("read error", err)
		return
	}
	fmt.Println(string(body))
}
```



# 解析网页

go常见的解析器xpath、jquery、正则都有，直接搜索即可，我这里直接用别人写好的轮子`collectlinks`，可以提取网页中所有的链接，下载方法`go get -u github.com/jackdanger/collectlinks`

```go
package main

import (
	"fmt"
	"github.com/jackdanger/collectlinks"
	"net/http"
)

func main() {
	fmt.Println("Hello, world")
	url := "http://www.baidu.com/"
	download(url)
}

func download(url string) {
	client := &http.Client{}
	req, _ := http.NewRequest("GET", url, nil)
	// 自定义Header
	req.Header.Set("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)")

	resp, err := client.Do(req)
	if err != nil {
		fmt.Println("http get error", err)
		return
	}
	//函数结束后关闭相关链接
	defer resp.Body.Close()

	links := collectlinks.All(resp.Body)
	for _, link := range links {
		fmt.Println("parse url", link)
	}
}
```



# 并发

Golang使用关键字`go`即可开启一个新的go程，也叫`goroutine`，使用 go 语句开启一个新的 goroutine 之后，go 语句之后的函数调用将在新的 goroutine 中执行，而不会阻塞当前的程序执行。所以使用Golang可以很容易写成异步IO。

```go
package main

import (
	"fmt"
	"github.com/jackdanger/collectlinks"
	"net/http"
)

func main() {
	fmt.Println("Hello, world")
	url := "http://www.baidu.com/"

	queue := make(chan string)
	go func() {
		queue <- url
	}()
	for uri := range queue {
		download(uri, queue)
	}
}

func download(url string, queue chan string) {
	client := &http.Client{}
	req, _ := http.NewRequest("GET", url, nil)
	// 自定义Header
	req.Header.Set("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)")

	resp, err := client.Do(req)
	if err != nil {
		fmt.Println("http get error", err)
		return
	}
	//函数结束后关闭相关链接
	defer resp.Body.Close()

	links := collectlinks.All(resp.Body)
	for _, link := range links {
		fmt.Println("parse url", link)
		go func() {
			queue <- link
		}()
	}
}
```

现在的流程是main有一个for循环读取来自名为queue的通道，download下载网页和链接解析，将发现的链接放入main使用的同一队列中，并再开启一个新的goroutine去抓取形成无限循环。

好了，到这里爬虫基本上已经完成了，但是还有两个问题：去重、链接是否有效。

# 链接转为绝对路径

```go
package main

import (
	"fmt"
	"github.com/jackdanger/collectlinks"
	"net/http"
	"net/url"
)

func main() {
	fmt.Println("Hello, world")
	url := "http://www.baidu.com/"

	queue := make(chan string)
	go func() {
		queue <- url
	}()
	for uri := range queue {
		download(uri, queue)
	}
}

func download(url string, queue chan string) {
	client := &http.Client{}
	req, _ := http.NewRequest("GET", url, nil)
	// 自定义Header
	req.Header.Set("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)")

	resp, err := client.Do(req)
	if err != nil {
		fmt.Println("http get error", err)
		return
	}
	//函数结束后关闭相关链接
	defer resp.Body.Close()

	links := collectlinks.All(resp.Body)
	for _, link := range links {
		absolute := urlJoin(link, url)
		if url != " " {
			fmt.Println("parse url", absolute)
			go func() {
				queue <- absolute
			}()
		}
	}
}

func urlJoin(href, base string) string {
	uri, err := url.Parse(href)
	if err != nil {
		return " "
	}
	baseUrl, err := url.Parse(base)
	if err != nil {
		return " "
	}
	return baseUrl.ResolveReference(uri).String()
}
```

这里新写了一个`urlJoin`函数，功能和Python中的`urllib.parse.urljoin`一样。

# 去重和加 timeout并写入文件

我们维护一个map用来记录，那些是已经访问过的。

```go
package main

import (
	"fmt"
	"github.com/jackdanger/collectlinks"
	"net/http"
	"net/url"
	"os"
	"strings"
	"time"
)

var isVisited = make(map[string]bool)

func downloadUrl(url string, queue chan string)  {
	isVisited[url] = true
	// 设置超时时间
	timeout := time.Duration(5 * time.Second)
	client := &http.Client{
		Timeout:timeout,
	}

	req, _ := http.NewRequest("GET", url, nil)
	// 自定义Header
	req.Header.Set("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)")
	resp, err := client.Do(req)
	if err != nil {
		fmt.Println("http get error", err)
		return
	}

	// 请求结束后关闭
	defer resp.Body.Close()

	// 获取网页所以链接
	links := collectlinks.All(resp.Body)


	for _, link := range links {
		// 去重和转为绝对路径
		absolute := UrlJoins(link, url)
		if url !="" {
			if !isVisited[absolute] {
				fmt.Println("parse url--->", absolute)
				traceFile(`input.txt`, absolute)
				go func() {
					queue <- absolute
				}()
			}
		}
	}
}

func UrlJoins(href, base string) string {
	uri, err := url.Parse(href)
	if err != nil {
		return ""
	}
	baseUrl, err := url.Parse(base)
	if err != nil {
		return ""
	}
	return baseUrl.ResolveReference(uri).String()
}

/**
	追加写入文件
 */
func traceFile(fileName,strContent string)  {
	fd,_:=os.OpenFile(fileName, os.O_RDWR|os.O_CREATE|os.O_APPEND,0644)
	fdTime :=time.Now().Format(`2006-01-02 15:04:05`)
	fdContent :=strings.Join([]string{fdTime,"=====", strContent,"\n"},"")
	buf:=[]byte(fdContent)
	_, _ = fd.Write(buf)
	_ = fd.Close()
}

func main() {
	hostUrl := `http://www.baidu.com/`
	queue := make(chan string,)
	go func() {
		queue <- hostUrl
	}()
	for uri:= range queue {
		downloadUrl(uri, queue)
	}
}
```

好了大功告成，运行程序，会像一张网铺开一直不停的抓下去。