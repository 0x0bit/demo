package main

import (
	"fmt"
	"github.com/jackdanger/collectlinks"
	"log"
	"net/http"
	"net/url"
)

var visited = make(map[string]bool)

func Download2(url string, queue chan string)  {
	visited[url] = true
	client := http.Client{}
	req, _ := http.NewRequest("GET", url, nil)
	// 自定义 header
	req.Header.Set(`User-Agent`, `Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)`)
	// 发送请求
	resp, err := client.Do(req)

	if err != nil {
		log.Fatal("链接获取页面错误", err)
	}

	//	函数结束后关闭链接
	defer resp.Body.Close()

	links := collectlinks.All(resp.Body)
	for _,link := range links {
		absolute := urlJoin(link, url)
		if url != "" {
			if !visited[absolute] {
				fmt.Println("parse url--->", absolute)
				go func() {
					queue <- absolute
				}()
			}
		}
	}
}

/**
	将链接转为绝对路径
 */
func urlJoin(href, base string) string {
	uri, err := url.Parse(href)
	if err != nil {
		return ""
	}
	baseUrl,err := url.Parse(base)
	if err != nil {
		return ""
	}
	return baseUrl.ResolveReference(uri).String()
}



func main() {
	hostUrl := "http://www.baidu.com/"

	queue := make(chan string)
	/**
	现在的流程是main有一个for循环读取来自名为queue的通道，
	download下载网页和链接解析，将发现的链接放入main使用的同一队列中，
	并再开启一个新的goroutine去抓取形成无限循环。
	*/
	go func() {
		queue <- hostUrl
	}()

	// 循环读取 queue 里面的链接
	for uri := range queue{
		Download2(string(uri), queue)
	}
}

