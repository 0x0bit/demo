package main

import (
	"fmt"
	"github.com/jackdanger/collectlinks"
	"log"
	"net/http"
)

func Download1(url string, queue chan string)  {
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
	for _, link := range links {
		fmt.Println("parse url --->", link)
		// 将获取到的链接循环放入到 queue 里面
		go func() {
			queue <- link
		}()
	}
}



func main() {
	url := "http://www.baidu.com/"

	queue := make(chan string)
	/**
	现在的流程是main有一个for循环读取来自名为queue的通道，
	download下载网页和链接解析，将发现的链接放入main使用的同一队列中，
	并再开启一个新的goroutine去抓取形成无限循环。
	*/
	go func() {
		queue <- url
	}()

	// 循环读取 queue 里面的链接
	for uri := range queue{
		Download1(string(uri), queue)
	}
}

